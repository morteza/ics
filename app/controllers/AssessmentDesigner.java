/*******************************************************************************
 *        File: AssessmentDesigner.java
 *    Revision: 5
 * Description: Design an assessment.
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: Aug 8, 2014
 *     Project: itrc.ics
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.AccountRole;
import models.assessment.Assessment;
import models.elements.BaseElement;
import models.elements.MetricElement;
import models.elements.QuestionElement;
import models.elements.QuestionElement.SeverityLevel;
import models.elements.SubMetricElement;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.With;
import utils.SecurityCheck;

@With(Security.class)
@SecurityCheck(AccountRole.MAINTAINER)
public class AssessmentDesigner extends Controller {
  
  public static void create(Assessment assessment) {
    //validation.valid(assessment);
    validation.required(assessment.title).message("Title is required.");
    validation.required(assessment.description).message("Description is required.");
    validation.minSize(assessment.title, 2).message("Minimum size for title is 2 characters.");
    if (validation.hasErrors()) {
      params.flash();
      validation.keep();
      flash.error(Messages.get("assessment.InvalidTitleOrDescription", "Invalid title or description."));
      flash.keep();
      Application.dashboard();
    }
    assessment.createdBy = Security.connected();
    assessment.save();
    general(assessment.code);
  }
  
  public static void save(Assessment assessment) {
    notFoundIfNull(assessment);
    notFoundIfNull(assessment.code);
    assessment.save();
    flash.success(Messages.get("assessment.UpdatedSuccessfully"));
    flash.keep();
    general(assessment.code);
  }
  
  public static void general(String code) {
    Assessment assessment = Assessment.findByCode(code);
    notFoundIfNull(assessment);

    render("designer/general.html", assessment);
  }
  
  public static void results(String code) {
    Assessment assessment = Assessment.findByCode(code);
    notFoundIfNull(assessment);

    render("designer/results.html", assessment);
  }
  
  public static void importQuestions(String code, File file) {
    Assessment assessment = Assessment.findByCode(code);
    notFoundIfNull(assessment);

    //TODO new ImportQuestionsCsv(assessment, file).now();
    
    try {
      BufferedReader br = new BufferedReader(new FileReader(file));
      String line;
      while ((line = br.readLine()) != null) {
        //TODO: assessment.questions.add(line);
      }
      br.close();
    } catch (Exception e) {
      e.printStackTrace();
      flash.error(Messages.get("An error occured while reading questions file."));
      questions(assessment.code, null, null);
    }
    
    assessment.save();
    
    flash.success(Messages.get("ics.designer.QuestionFileHasBeenImportedSuccessfully"));
    
    questions(code, null, null);

  }
 
  /**
   * Show a view to manage assessment metrics.
   * @param code assessment code
   */
  public static void metrics(String code) {
    Assessment assessment = Assessment.findByCode(code);
    notFoundIfNull(assessment);
    
    List<MetricElement> metrics = MetricElement.find("byAssessment", assessment).fetch();
    
    render("designer/metrics.html", assessment, metrics);    
  }
  
  /**
   * Show a view to manage assessment sub-metrics.
   * @param code assessment code
   */
  public static void subMetrics(String code) {
    Assessment assessment = Assessment.findByCode(code);
    notFoundIfNull(assessment);
    
    List<MetricElement> subMetrics = SubMetricElement.find("byAssessment", assessment).fetch();
    
    render("designer/sub_metrics.html", assessment, subMetrics);    
  }

  /**
   * Show a view to manage assessment elements.
   * @param code assessment code
   */
  public static void questions(String code, Long parentId, SeverityLevel level) {
    Assessment assessment = Assessment.findByCode(code);
    notFoundIfNull(assessment);
    
    List<MetricElement> metrics = MetricElement.find("assessment", assessment).fetch();
    List<QuestionElement> questions;
    MetricElement parent = null;
    
    if (parentId!=null) {
      parent = MetricElement.findById(parentId);
    }
    
    // Fetch questions
    if (parent!=null) {
      if (level!=null) {
        // Find all elements regarding the passed metric and level
        questions = QuestionElement.find("parent.parent=:parent AND level=:level")
          .setParameter("parent", parent)
          .setParameter("level", level)
          .fetch();
      } else {
        // Find all questions regardless of level
        questions = QuestionElement.find("parent.parent=:parent")
            .setParameter("parent", parent)
            .fetch();
      }
      renderArgs.put("parent", parent);
      renderArgs.put("questions", questions);
    }

    render("designer/questions.html", assessment, metrics);    
  }

  public static void publish(String code) {
    todo();
  }
  
  /**
   * Delete an assessment by setting its isDeleted flag.
   * @param code
   */
  @SecurityCheck(AccountRole.MAINTAINER)
  public static void remove(String code) {
    Assessment assessment = Assessment.findByCode(code);
    notFoundIfNull(assessment);
    
    assessment.isDeleted = true;
    assessment.deletedAt = new Date();
    assessment.save();
    
    Application.dashboard();
  }

  
  public static void cleanup(String code) {
    Assessment assessment = Assessment.findByCode(code);
    notFoundIfNull(assessment);
    
    assessment.elements.clear();

    List<MetricElement> metrics = MetricElement.find("SELECT DISTINCT m FROM metric_element m WHERE "
        + "m.assessment=:assessment")
        .setParameter("assessment", assessment).fetch();
    List<SubMetricElement> subMetrics = SubMetricElement.find("SELECT DISTINCT sm FROM sub_metric_element sm WHERE "
        + "sm.assessment=:assessment")
        .setParameter("assessment", assessment).fetch();
    List<QuestionElement> questions = QuestionElement.find("SELECT DISTINCT q FROM question_element q WHERE "
        + "q.assessment=:assessment")
        .setParameter("assessment", assessment).fetch();
    
    for (MetricElement m: metrics) {
      assessment.elements.add("metric."+m.id);
    }
    for (SubMetricElement sm: subMetrics) {
      assessment.elements.add("sub_metric."+sm.id);
    }
    for (QuestionElement q: questions) {
      assessment.elements.add("question."+q.id);
    }
    assessment.save();
    
    flash.success("Cleaned Up!");
    metrics(assessment.code);
  }
}
