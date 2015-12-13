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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.Query;

import utils.MetricWeightComparator;

import models.Account;
import models.AccountRole;
import models.assessment.Response;
import models.elements.MetricElement;
import models.elements.QuestionElement;
import models.elements.SubMetricElement;
import models.assessment.Assessment;
import models.assessment.Assessor;
import play.db.jpa.JPA;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Http.Cookie;
import play.mvc.Util;
import play.mvc.With;
import utils.SecurityCheck;

@With(Security.class)
@SecurityCheck(AccountRole.GUEST)
public class Assessments extends Controller {
  
  public static void chooseAssessmentType() {
    render("assessments/choose_type.html");
  }
  
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
    
    List<Assessment> assessments = Assessment.find("isDeleted=false and isPublished=true").fetch();

    for (Assessment assessment: assessments) {
      session.current().remove(assessment.code + "_current");
    }

    render("assessments/standards.html", assessments, assessor);
  }
  
  /**
   * 
   * @param code
   * @param assessor
   * @param level
   * @param step
   */
  public static void jumpToQuestionsPage(String code, Long assessorId, String level, int page) {
    Assessment assessment = Assessment.findByCode(code);
    Assessor assessor = Assessor.findById(assessorId);
    Query metricQuery = JPA.em().createQuery("SELECT m.id, m.code, m.title FROM models.elements.MetricElement m "
        + "WHERE m.assessment=:assessment ORDER BY m.rank ASC");
    metricQuery.setParameter("assessment", assessment);
    List<Object[]> metrics = metricQuery.getResultList();
    
    if (page>metrics.size()) {
      results(code, assessor.id);
    }
    if (page<1) {
      standards(assessor);
    }
    
    int pages = metrics.size();
    String currentMetricCode = (String) metrics.get(page-1)[1];

    session.current().put(assessment.code + "_current", currentMetricCode);
    
    MetricElement metric = (MetricElement) Elements.findElementByCode(currentMetricCode);
    
    //. Find sub-metrics
    Query query = JPA.em().createQuery("SELECT sm.id, sm.title, sm.description FROM models.elements.SubMetricElement sm "
        + "WHERE sm.parent=:parent ORDER BY sm.rank ASC");
    query.setParameter("parent", metric);
    List<Object[]> subMetrics = query.getResultList();
    renderArgs.put("subMetrics", subMetrics);

    // This is the previous page
    page--;
    
    //. Render questions of this metrics and corresponding subMetrics
    render("assessments/questions.html", assessment, assessor, metrics, metric, subMetrics, page, pages, level);
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

    Query query = JPA.em().createQuery("SELECT m.id, m.code, m.title FROM models.elements.MetricElement m "
        + "WHERE m.assessment=:assessment ORDER BY m.rank ASC");
    query.setParameter("assessment", assessment);
    List<Object[]> metrics = query.getResultList();
    
    int pages = metrics.size();

    //1. Find current metric from session
    String currentMetricCode = session.current().get(assessment.code + "_current");
    MetricElement metric = (MetricElement) Elements.findElementByCode(currentMetricCode);
    System.out.println("[1] currentMetricCode=" + currentMetricCode);

    //2. save results if any
    for (String key: params.all().keySet()) {
      if (key.startsWith("response_")) {
        String strQuestionId = key.substring(9); // find question id (i.e. from 'question.*' strings).
        QuestionElement qElement = (QuestionElement) Elements.findElementByCode("question." + strQuestionId);
        String content = params.get(key);
        Response response = new Response(qElement, content);
        response.save();
        assessor.responses.add(response);
        assessor.save();
      }
    }
    
    //3. find next metric and the page
    int page = (metric==null)?0:(metric.rank);
    if (page>=pages) {
      session.current().remove(assessment.code + "_current");
      results(assessment.code, assessor.id);
    }
    metric = MetricElement.findById(metrics.get(page)[0]);
    session.current().put(assessment.code + "_current", metric.code);
    
    //4. Find sub-metrics
    query = JPA.em().createQuery("SELECT sm.id, sm.title, sm.description FROM models.elements.SubMetricElement sm "
        + "WHERE sm.parent=:parent ORDER BY sm.rank ASC");
    query.setParameter("parent", metric);
    List<Object[]> subMetrics = query.getResultList();
    renderArgs.put("subMetrics", subMetrics);

    //5. Render questions of this metrics and corresponding subMetrics
    render("assessments/questions.html", assessment, assessor, metrics, metric, subMetrics, page, pages, level);
  }

  public static void results(String code, Long assessorId) {    
    notFoundIfNull(assessorId);
    
    Assessment assessment = Assessment.findByCode(code);
    notFoundIfNull(assessment);

    Assessor assessor = Assessor.findById(assessorId);
    notFoundIfNull(assessor);

    session.current().remove(assessment.code + "_current");

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
    
    Query metricQuery = JPA.em().createQuery("SELECT m.id, m.title FROM models.elements.MetricElement m "
        + "WHERE m.assessment=:assessment");
    metricQuery.setParameter("assessment", assessment);
    List<Object[]> metrics = metricQuery.getResultList(); // [id, title]
    Map<Long, String> metricTitles = new HashMap<Long, String>();
    
    //1. Calculate top categories of concerns (same as metrics)

    Map<Long, Double> failures = new HashMap<Long, Double>();
    Map<Long, Double> weights = new HashMap<Long, Double>();

    for (Object[] m: metrics) {
      failures.put((Long)m[0], 0.0);
      weights.put((Long)m[0], 0.0);
      metricTitles.put((Long)m[0], (String)m[1]);
    }    

    List<QuestionElement> noAndNoneAndUA;
    
    //Add all questions with no response
    noAndNoneAndUA = QuestionElement.find("SELECT DISTINCT q FROM question_element q, response r, Assessor a WHERE "
        + "a=:assessor AND (r MEMBER OF a.responses) AND r.question=q AND (r.content='no' OR r.content='none' OR r.content='')").setParameter("assessor", assessor).fetch();

    MetricElement parentMetric;
    
    long numOfAllQuestions = QuestionElement.count("assessment=?", assessment);
    double totalWeight = 0.0;//(numOfAllQuestions*(numOfAllQuestions+1))/2.0;
        
    for (QuestionElement q: noAndNoneAndUA) {
      //TODO: calculate and add weights in the same order of the questions
      //weights.add(0.0);
      //FIXME: workaround
      if (q.rank==null)
        q.rank = 1;
      
      parentMetric = q.parent.parent;
      Double parentFailures = failures.get(parentMetric.id);
      if (parentFailures == null) parentFailures = 0.0;
      failures.put(parentMetric.id, parentFailures+1.0);
      
      Double parentWeight = weights.get(parentMetric.id);
      if (parentWeight == null) parentWeight = 0.0;
      double w = 1.0 + ((1-q.rank)/(numOfAllQuestions));
      weights.put(parentMetric.id, parentWeight+(w));
      totalWeight += w;
    }
    
    for (Long mId: failures.keySet()) {
      double failureCount = failures.get(mId);
      long numOfQ = ((MetricElement)MetricElement.findById(mId)).numOfQuestions();
      double failurePercentage = 0.0;
      if (numOfQ>0)
        failurePercentage = 100 * failureCount / numOfQ;
      failures.put(mId, failurePercentage);
    }
    
    Double normalizedTotalWeight = totalWeight;
    
    if (Double.compare(totalWeight, 0.0)>0) {
      for (Long mId: weights.keySet()) {
        double w = weights.get(mId);
        double normalizedW = (100.0 * w / totalWeight);
        weights.put(mId, normalizedW);
      }      
      //FIXME: this is extreme stupidity!
      normalizedTotalWeight = 100 * totalWeight / totalWeight;

    }

    // Sort according to the weights
    Map<Long, Double> sortedWeights  = new TreeMap<Long, Double>(new MetricWeightComparator(weights));
    sortedWeights.putAll(weights);
    
    renderArgs.put("metrics", sortedWeights.keySet());
    
        
    render("assessments/concerns.html", assessment, assessor, metricTitles, weights, normalizedTotalWeight, totalWeight, failures);
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
