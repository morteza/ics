/*******************************************************************************
 *        File: Elements.java
 *    Revision: 1
 * Description: Manages building block elements of a survey.
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: Oct 31, 2015
 *     Project: itrc.ics
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package controllers;

import models.AccountRole;
import models.elements.MetricElement;
import models.elements.QuestionElement;
import models.elements.SubMetricElement;
import models.survey.Survey;
import play.mvc.Controller;
import play.mvc.With;
import utils.SecurityCheck;

@With(Security.class)
@SecurityCheck(AccountRole.MAINTAINER)
public class Elements extends Controller {
  public static void saveQuestion(Survey survey, QuestionElement element) {
    //TODO: read parent sub metric from params, if available.
    String content = request.params.get("content");
    
    // Check if elements is new, or we are updating one.
    if (element!=null && element.id!=null) {
      // Updating an element.
    } else {
      // It is a new element.
    }
    SurveyDesigner.elements(survey.code);
  }
  
  public static void saveMetric(Survey survey, MetricElement element) {
    String title = request.params.get("title");
    String content = request.params.get("content");
  }
  
  public static void saveSubMetric(Survey survey, SubMetricElement element) {
    //TODO: read parent metric from params, if available.    
    String title = request.params.get("title");
    String content = request.params.get("content");
  }
}
