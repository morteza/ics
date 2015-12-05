/*******************************************************************************
 *        File: Elements.java
 *    Revision: 1
 * Description: Manages building block elements of an assessment.
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: Oct 31, 2015
 *     Project: itrc.ics
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package controllers;

import java.util.List;

import javax.lang.model.element.Element;

import models.Account;
import models.AccountRole;
import models.assessment.Assessment;
import models.elements.BaseElement;
import models.elements.MetricElement;
import models.elements.QuestionElement;
import models.elements.QuestionElement.SeverityLevel;
import models.elements.SubMetricElement;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Util;
import play.mvc.With;
import utils.SecurityCheck;

@With(Security.class)
@SecurityCheck(AccountRole.MAINTAINER)
public class Elements extends Controller {
  public static void saveQuestion(SubMetricElement parent, QuestionElement element) {
    //TODO: read parent metric from params, if available.    
    String title = request.params.get("title");
    String description = request.params.get("description");
    String level = request.params.get("level");
    String rank = request.params.get("rank");

    // Check if elements is new, or we are updating one.
    if (element!=null && element.id!=null) {
      element.title = title;
      element.description = description;
      element.level = QuestionElement.SeverityLevel.valueOf(level);
      element.rank = Integer.valueOf(rank);
      element.setParent(parent);
      element.save();
      flash.success(Messages.get("assessments.elements.QuestionUpdated"));
    } else {
      element = new QuestionElement(parent);
      element.title = title;
      element.description = description;
      element.level = QuestionElement.SeverityLevel.valueOf(level);
      element.rank = Integer.valueOf(rank);
      element.save();
      element.code = "question." + element.id;
      element.save();
      element.assessment.elements.add(element.code);
      element.assessment.save();
      //reassignQuestionRanks(elem.assessment);
      flash.success(Messages.get("assessments.elements.QuestionCreated"));
    }
    AssessmentDesigner.questions(parent.assessment.code, parent.parent.id, element.level);
  }
  
  public static void saveMetric(Assessment assessment, MetricElement element) {
    String title = request.params.get("title");
    String description = request.params.get("description");
    
    // Check if elements is new, or we are updating one.
    if (element!=null && element.id!=null) {
      element.title = title;
      element.description = description;
      element.save();
      flash.success(Messages.get("assessments.elements.MetricUpdated"));
    } else {
      MetricElement elem = new MetricElement(assessment, title, description);
      elem.save();
      elem.code = "metric." + elem.id;
      elem.save();
      assessment.elements.add(elem.code);
      assessment.save();
      flash.success(Messages.get("assessments.elements.MetricCreated"));
    }
    AssessmentDesigner.metrics(assessment.code);
  }
  
  public static void saveSubMetric(MetricElement parent, SubMetricElement element) {
    //TODO: read parent metric from params, if available.    
    String title = request.params.get("title");
    String description = request.params.get("description");

    // Check if elements is new, or we are updating one.
    if (element!=null && element.id!=null) {
      element.title = title;
      element.description = description;
      element.setParent(parent);
      element.save();
      flash.success(Messages.get("assessments.elements.SubMetricUpdated"));
    } else {
      SubMetricElement elem = new SubMetricElement(parent);
      elem.title = title;
      elem.description = description;
      elem.save();
      elem.code = "sub_metric." + elem.id;
      elem.save();
      elem.assessment.elements.add(elem.code);
      elem.assessment.save();
      flash.success(Messages.get("assessments.elements.SubMetricCreated"));
    }
    AssessmentDesigner.subMetrics(parent.assessment.code);
  }
  
  public static void create(String assessmentCode, String type) {
    Assessment assessment = Assessment.findByCode(assessmentCode);
    notFoundIfNull(assessment);
    
    // New Sub Metric or Question
    if ("sub_metric".equalsIgnoreCase(type) || "question".equalsIgnoreCase(type)) {
      // Add available metrics to the render arguments.
      List<MetricElement> metrics = MetricElement.find("assessment", assessment).fetch();
      renderArgs.put("metrics", metrics);
    }
    
    String viewPath = "elements/" + type + ".html";
    renderTemplate(viewPath, assessment);
  }
  
  @Util
  public static BaseElement findElementByCode(String code) {
    if (code==null || !code.contains("."))
      return null;

    try {
      String[] parts = code.split("\\.");
      String elemType = parts[0];
      
      Long elemId = Long.parseLong(parts[1]);

      switch(elemType.toLowerCase()) {
      case "metric":
        MetricElement mElement = MetricElement.findById(elemId);
        return mElement;
      case "sub_metric":
        SubMetricElement smElement = SubMetricElement.findById(elemId);
        return smElement;
      case "question":
        QuestionElement qElement = QuestionElement.findById(elemId);
        return qElement;
      }      
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  public static void preview(String code) {
    BaseElement element = findElementByCode(code);
    notFoundIfNull(element);

    Assessment assessment = element.assessment;
    notFoundIfNull(assessment);
    
    String type = element.elementType();
    notFoundIfNull(type);

    // New Sub Metric or Question
    if ("sub_metric".equalsIgnoreCase(type) || "question".equalsIgnoreCase(type)) {
      // Add available metrics to the render arguments.
      List<MetricElement> metrics = MetricElement.find("assessment", assessment).fetch();
      renderArgs.put("metrics", metrics);
    }
   
    //long numOfRelatedResults = Account.find("SELECT DISTINCT a FROM Account a, BlockResult r WHERE r.actor=a AND r.assessment=?", assessment).fetch().size();      
    boolean preventRemoval = false;//Long.compare(numOfRelatedResults, 0) >0;

    render("elements/" + type + ".html", assessment, element, preventRemoval);

  }

  public static void delete(String code) {
    BaseElement element = findElementByCode(code);
    notFoundIfNull(element);
    
    Assessment assessment = element.assessment;
    notFoundIfNull(assessment);
    
    try {
      element.delete();  
      assessment.elements.remove(code);    
      assessment.save();
    } catch(Exception e) {
      flash.error(Messages.get("assessments.elements.RemoveFailed"));     
      //TODO: move to appropriate page
      AssessmentDesigner.metrics(assessment.code);
    }
    
    flash.success(Messages.get("assessments.elements.Removed"));
    
    //TODO: move to appropriate page  
    AssessmentDesigner.metrics(assessment.code);
  }
  
  /**
   * Updates and sort order of elements that belong to an experiment.
   * @param experiment
   * @param elements
   */
  public static void sortElements(Assessment assessment, String[] elements) {
    assessment.elements.clear();
    if (elements!=null) {
      for (String elementCode: elements) {
        assessment.elements.add(elementCode);
      }
    }
    
    assessment.save();
    
    //reassignQuestionRanks(assessment);
    
    renderText(Messages.get("assessments.elements.Reordered"));
  }
  
  @Util
  private static void addAdditionalRenderArgs() {
    //TODO:
  }
  
  public static List<SubMetricElement> getSubMetrics(MetricElement parent) {
    List<SubMetricElement> subMetrics = SubMetricElement.find("byParent", parent).fetch();
    return subMetrics;
  }
  
  
  @Util
  public static List<QuestionElement> getQuestions(SubMetricElement subMetric, String level) {
    QuestionElement.SeverityLevel sLevel = SeverityLevel.valueOf(level);
    //TODO check if sLevel is not null, or return all questions if it's null.   
    
    List<QuestionElement> questions = QuestionElement.find("byParentAndLevel", subMetric, sLevel).fetch();
    return questions;
  }
  
  @Util
  private static void reassignQuestionRanks(Assessment assessment) {
    int rankIndex = 0;
    for (String elementCode: assessment.elements) {
      if (elementCode.startsWith("question")) {
        QuestionElement element = (QuestionElement) findElementByCode(elementCode);
        if (element!=null) {
          element.rank = (++rankIndex);
          element.save();         
        } else {
          //assessment.elements.remove(elementCode);
          //assessment.save();
        }
      }
    }
  }

}
