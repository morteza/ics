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
import models.assessment.Assessment;
import models.assessment.Assessor;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Http.Cookie;
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
    
    List<Assessments> assessments = Assessment.all().fetch();
    
    render("assessments/standards.html", assessments);
  }
  
  /**
   * Starts an assessment, verify, check, or set cookies and other stuff.
   * @param code assessment code
   */
  public static void startAssessment(String code) {
    Assessment assessment = Assessment.findByCode(code);
    //notFoundIfNull(assessment);
    if (assessment==null) {
      flash.error(Messages.get("errors.SelectStandard"));
      standards(null);
    }
    
    Cookie assessmentCookie = request.cookies.get(ASSESSMENT_COOKIE_PREFIX + code);
    String assessmentSession = session.get(ASSESSMENT_COOKIE_PREFIX + code);
    
    String md5Code = code; //TODO play.libs.Codec.hexMD5(code);
    
    int page = 0;
    
    if(!md5Code.equals(assessmentSession)) {
      session.put(ASSESSMENT_COOKIE_PREFIX + code, md5Code);
      session.put(PAGE_COOKIE_PREFIX + code, "" + page); // put the current step (first) into an assessment
    }
    
    next(code, null, false);
  }
  
  /**
   * Move to the next page in a assessment. It's the main navigator of the assessment.
   * @param code assessment code
   * @param partialResult
   */
  public static void next(String code, String[] answers, boolean isUniversal) {
    
    // Check if the assessment is already started for current session.
    if (!session.contains(PAGE_COOKIE_PREFIX + code) || !session.contains(ASSESSMENT_COOKIE_PREFIX + code)) {
      startAssessment(code);
    }

    int page = Integer.valueOf(session.get(PAGE_COOKIE_PREFIX + code));
    
    // Increase page counter
    session.put(PAGE_COOKIE_PREFIX + code, page+1);
    
    
    Assessment assessment = Assessment.findByCode(code);
    notFoundIfNull(assessment);
    
    Account me = Security.connected();

    // Cleanup past assessment results
    Response.delete("actor=? AND assessment=?", me, assessment);
    
    // Storing results.
    if (answers!=null && answers.length>0) {
      for (int i=0;i<answers.length; i++) {
        Response a = null;
        //TODO: save responses
        a.save();
      }      
    }

    
    //TODO find total pages
    int pages = 2;

    if (page >= pages) {
      results(code);
    }
    
    
    //TODO: find questions belong to the current page.
    List<String> questions = null;
            
    render("assessments/questions.html", assessment, page, pages, questions);

  }

  public static void results(String code) {
    Assessment assessment = Assessment.findByCode(code);
    notFoundIfNull(assessment);
    
    session.remove(ASSESSMENT_COOKIE_PREFIX + code);
    session.remove(PAGE_COOKIE_PREFIX + code);

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
  
  public static void finish(String code) {
    Assessment assessment = Assessment.findByCode(code);
    notFoundIfNull(assessment);
    
    session.remove(ASSESSMENT_COOKIE_PREFIX + code);
    session.remove(PAGE_COOKIE_PREFIX + code);
    
    flash.success(Messages.get("ThankYou"));
    
    Application.dashboard();

  }
  
  /**
   * Move to the previous step of the assessment. It does not save the results.
   * @param code assessment code
   */
  public static void prev(String code) {
    
  }
}
