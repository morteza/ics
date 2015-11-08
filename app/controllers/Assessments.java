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
  
  public static void assessorInformation() {    
    render("assessments/assessor.html");
  }
  
  public static void standards(Assessor assessor) {
    if (assessor == null) {
      flash.error(Messages.get("errors.NoAssessorInfo"));
      assessorInformation();
    }
    assessor.account = Security.connected();
    assessor.save();
    
    List<Assessment> assessments = Assessment.all().fetch();

    for (Assessment assessment: assessments) {
      session.current().remove(assessment.code + "_current");
    }

    render("assessments/standards.html", assessments, assessor);
  }
  
  /**
   * Starts an assessment, verify, check, or set cookies and other stuff.
   * @param code assessment code
   */
  public static void questions(String code, Assessor assessor, String level) {
    Assessment assessment = Assessment.findByCode(code);
    //notFoundIfNull(assessment);
    if (assessment==null) {
      flash.error(Messages.get("errors.SelectStandard"));
      standards(assessor);
    }

    List<MetricElement> metrics = MetricElement.find("assessment", assessment).fetch();
    int pages = metrics.size();
    
    //1. Find current metric from session
    String currentMetricCode = session.current().get(assessment.code + "_current");
    MetricElement metric = (MetricElement) Elements.findElementByCode(currentMetricCode);
    
    //2. save results if any
    for (String key: params.all().keySet()) {
      if (key.startsWith("response_")) {
        String strQuestionId = key.substring(9); // find question id
        QuestionElement qElement = (QuestionElement) Elements.findElementByCode("question." + strQuestionId);
        String content = params.get(key);
        Response response = new Response(qElement, content);
        response.save();
        assessor.responses.add(response);
        assessor.save();
      }
    }
    
    //3. find next metric and the page
    int page = metrics.indexOf(metric) + 1;
    if (page>=pages) {
      session.current().remove(assessment.code + "_current");
      results(assessment.code, assessor.id);
    }
    metric = metrics.get(page);
    session.current().put(assessment.code + "_current", metric.code);
    
    //4. Find sub-metrics
    List<SubMetricElement> subMetrics = SubMetricElement.find("parent", metric).fetch();
    renderArgs.put("subMetrics", subMetrics);

    //5. Render questions of this metrics and corresponding subMetrics
    render("assessments/questions.html", assessment, assessor, metric, subMetrics, page, pages, level);
  }

  public static void results(String code, Long assessorId) {    
    notFoundIfNull(assessorId);
    
    Assessment assessment = Assessment.findByCode(code);
    notFoundIfNull(assessment);

    Assessor assessor = Assessor.findById(assessorId);
    notFoundIfNull(assessor);
        
    List<Response> yess = new ArrayList<Response>();
    List<Response> nos = new ArrayList<Response>();
    List<Response> nones = new ArrayList<Response>();
    List<Response> alts = new ArrayList<Response>();
    
    for (Response r:assessor.responses) {
      if ("yes".equalsIgnoreCase(r.content)) yess.add(r);
      if ("no".equalsIgnoreCase(r.content)) nos.add(r);
      if ("none".equalsIgnoreCase(r.content)) nones.add(r);
      if ("alt".equalsIgnoreCase(r.content)) alts.add(r);
    }
    
    render("assessments/results.html", assessment, assessor, yess, nos, nones, alts);
  }
  
  public static void concerns(String code, Long assessorId) {
    notFoundIfNull(assessorId);
    
    Assessment assessment = Assessment.findByCode(code);
    notFoundIfNull(assessment);

    Assessor assessor = Assessor.findById(assessorId);
    notFoundIfNull(assessor);
    
    //1. Calculate top categories of concerns (same as metrics)
    List<MetricElement> metrics = MetricElement.find("SELECT DISTINCT m FROM metric_element m, response r, question_element q, "
        + "Assessor a WHERE a=:assessor AND (r MEMBER OF a.responses) AND r.question=q AND q.parent.parent=m "
        + "AND (r.content='no' OR r.content='' OR r.content=null)").setParameter("assessor", assessor).fetch();
    List<Double> metricWeights = new ArrayList<Double>();
    List<Integer> metricCounts = new ArrayList<Integer>();
    for (MetricElement m: metrics) {
      //TODO: calculate and add weights in the same order of the metrics
      metricWeights.add(0.1);
      metricCounts.add(10);
    }    
    renderArgs.put("metrics", metrics);
    renderArgs.put("metricWeights", metricWeights);
    renderArgs.put("metricCounts", metricCounts);
    
    List<QuestionElement> questions;

    //Add all questions with no response
    questions = QuestionElement.find("SELECT DISTINCT q FROM question_element q, response r, Assessor a WHERE "
        + "a=:assessor AND (r MEMBER OF a.responses) AND r.question=q AND (r.content='no' OR r.content='' OR r.content=null)").setParameter("assessor", assessor).fetch();
    
    List<Double> weights = new ArrayList<Double>();
    for (QuestionElement q: questions) {
      //TODO: calculate and add weights in the same order of the questions
      weights.add(0.0);
    }
    
    render("assessments/concerns.html", assessment, assessor, questions, weights);
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
