/*******************************************************************************
 *        File: Assessments.java
 *    Revision: 4
 * Description: Runs an assessment for the subject.
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: Aug 8, 2014
 *     Project: itrc.ics
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Account;
import models.AccountRole;
import models.assessment.Response;
import models.elements.MetricElement;
import models.elements.QuestionElement;
import models.elements.SubMetricElement;
import models.assessment.Assessment;
import models.assessment.Assessor;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Http.Cookie;
import play.mvc.Util;
import play.mvc.With;
import utils.SecurityCheck;

@With(Security.class)
@SecurityCheck(AccountRole.GUEST)
public class Assessments extends Controller {
  
  private static String ASSESSMENT_COOKIE_PREFIX = "itrc_ics_assessment_";
  private static String PAGE_COOKIE_PREFIX = "itrc_ics_element_";
  
  public static void assessorInformation() {    
    render("assessments/assessor.html");
  }
  
  public static void standards(Assessor assessor) {
    if (assessor!=null) {
      assessor.save();
      //flash.success(Messages.get("assessor.Saved"));
    }
    
    List<Assessment> assessments = Assessment.all().fetch();

    for (Assessment assessment: assessments) {
      session.current().remove(assessment.code + "_current");
    }

    render("assessments/standards.html", assessments);
  }
  
  /**
   * Starts an assessment, verify, check, or set cookies and other stuff.
   * @param code assessment code
   */
  public static void questions(String code) {
    Assessment assessment = Assessment.findByCode(code);
    //notFoundIfNull(assessment);
    if (assessment==null) {
      flash.error(Messages.get("errors.SelectStandard"));
      standards(null);
    }

    List<MetricElement> metrics = MetricElement.find("assessment", assessment).fetch();
    int pages = metrics.size();
    
    //1. Find current metric from session
    String currentMetricCode = session.current().get(assessment.code + "_currrent");
    MetricElement metric = (MetricElement) Elements.findElementByCode(currentMetricCode);
    
    //TODO: 2. save results if any
    
    //3. find next metric and the page
    int page = metrics.indexOf(metric) + 1;
    if (page>=pages) {
      session.current().remove(assessment.code + "_current");
      results(assessment.code);
    }
    metric = metrics.get(page);
    session.current().put(assessment.code + "_currrent", metric.code);
    
    //4. Find sub-metrics
    List<SubMetricElement> subMetrics = SubMetricElement.find("parent", metric).fetch();
    renderArgs.put("subMetrics", subMetrics);

    //5. Render questions of this metrics and corresponding subMetrics
    render("assessments/questions.html", assessment, metric, subMetrics, page, pages);
  }

  public static void results(String code) {
    Assessment assessment = Assessment.findByCode(code);
    notFoundIfNull(assessment);
    
    session.current().remove(assessment.code + "_current");

    Account me = Security.connected();
    
    List<Response> yess = Response.find("actor=:me AND assessment=:assessment AND content='yes'")
        .setParameter("me", me)
        .setParameter("assessment", assessment)
        .fetch();

    
    List<Response> nos = Response.find("actor=:me AND assessment=:assessment AND content='no'")
        .setParameter("me", me)
        .setParameter("assessment", assessment)
        .fetch();

    
    List<Response> nones = Response.find("actor=:me AND assessment=:assessment AND content='none'")
        .setParameter("me", me)
        .setParameter("assessment", assessment)
        .fetch();

    
    List<Response> alts = Response.find("actor=:me AND assessment=:assessment AND content='alt'")
        .setParameter("me", me)
        .setParameter("assessment", assessment)
        .fetch();
    
    render("assessments/results.html", assessment, yess, nos, nones, alts);
  }
  
  /**
   * Move to the previous step of the assessment. It does not save the results.
   * @param code assessment code
   */
  public static void prev(String code) {
    todo();
  }
  
  public static void finish(String code) {
    Application.index();
  }

}
