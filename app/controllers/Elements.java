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

import models.AccountRole;
import models.assessment.Assessment;
import models.elements.MetricElement;
import models.elements.QuestionElement;
import models.elements.SubMetricElement;
import play.mvc.Controller;
import play.mvc.With;
import utils.SecurityCheck;

@With(Security.class)
@SecurityCheck(AccountRole.MAINTAINER)
public class Elements extends Controller {
  public static void saveQuestion(Assessment assessment, QuestionElement element) {
    //TODO: read parent sub metric from params, if available.
    String content = request.params.get("content");
    
    // Check if elements is new, or we are updating one.
    if (element!=null && element.id!=null) {
      // Updating an element.
    } else {
      // It is a new element.
    }
    AssessmentDesigner.elements(assessment.code);
  }
  
  public static void saveMetric(Assessment assessment, MetricElement element) {
    String title = request.params.get("title");
    String content = request.params.get("content");
  }
  
  public static void saveSubMetric(Assessment assessment, SubMetricElement element) {
    //TODO: read parent metric from params, if available.    
    String title = request.params.get("title");
    String content = request.params.get("content");
  }
  
  public static void create(String assessmentCode, String type) {
    Assessment assessment = Assessment.findByCode(assessmentCode);
    notFoundIfNull(assessment);
    
    String viewPath = "elements/" + type + ".html";
    renderTemplate(viewPath, assessment);
  }
  
  /**
   * Updates and sort order of elements that belong to an experiment.
   * @param experiment
   * @param elements
   */
  public static void sortElements(Assessment assessment, String[] elements) {
    assessment.elements.clear();
    if (elements!=null) {
      for (String element: elements) {
        assessment.elements.add(element);
      }
    }
    assessment.save();
    renderText("Elements have been reordered.");
  }
  
}
