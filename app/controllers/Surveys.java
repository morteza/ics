/**
 * 
 */
package controllers;

import java.util.ArrayList;
import java.util.List;

import models.AccountRole;
import models.survey.Question;
import models.survey.Survey;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Http.Cookie;
import play.mvc.With;
import utils.SecurityCheck;

@With(Security.class)
@SecurityCheck(AccountRole.GUEST)
public class Surveys extends Controller {
  
  private static String SURVEY_COOKIE_PREFIX = "itrc_cset_srvy_";
  private static String PAGE_COOKIE_PREFIX = "itrc_cset_stp_";
  
  /**
   * Starts a survey, verify, check, or set cookies and other stuff.
   * @param code survey code
   */
  public static void start(String code) {
    Survey survey = Survey.findByCode(code);
    notFoundIfNull(survey);
    
    Cookie surveyCookie = request.cookies.get(SURVEY_COOKIE_PREFIX + code);
    String surveySession = session.get(SURVEY_COOKIE_PREFIX + code);
    
    String md5Code = code; //TODO play.libs.Codec.hexMD5(code);
    
    int page = 0;
    
    if(!md5Code.equals(surveySession)) {
      session.put(SURVEY_COOKIE_PREFIX + code, md5Code);
      session.put(PAGE_COOKIE_PREFIX + code, "" + page); // put the current step (first) into a survey
    }
    
    next(code, null);
  }
  
  /**
   * Move to the next page in a survey. It's the main navigator of the survey.
   * @param code survey code
   * @param partialResult
   */
  public static void next(String code, String partialResult) {

    // Check if the survey is already started for current session.
    if (!session.contains(PAGE_COOKIE_PREFIX + code) || !session.contains(SURVEY_COOKIE_PREFIX + code)) {
      start(code);
    }

    int page = Integer.valueOf(session.get(PAGE_COOKIE_PREFIX + code));
    
    // Increase page counter
    session.put(PAGE_COOKIE_PREFIX + code, page+1);
    
    
    Survey survey = Survey.findByCode(code);
    notFoundIfNull(survey);
    
    //TODO find total pages
    int pages = 1;

    if (page >= pages) {
      results(code);
    }
    
    
    //TODO find questions belong to the current page.
    List<String> questions = survey.questions;
        
    render("surveys/page.html", survey, page, pages, questions);

  }

  public static void results(String code) {
    Survey survey = Survey.findByCode(code);
    notFoundIfNull(survey);
    
    session.remove(SURVEY_COOKIE_PREFIX + code);
    session.remove(PAGE_COOKIE_PREFIX + code);

    render("surveys/results.html", survey);
  }
  
  public static void finish(String code) {
    Survey survey = Survey.findByCode(code);
    notFoundIfNull(survey);
    
    session.remove(SURVEY_COOKIE_PREFIX + code);
    session.remove(PAGE_COOKIE_PREFIX + code);
    
    flash.success(Messages.get("ThankYou"));
    
    Application.dashboard();

  }
  
  /**
   * Move to the previous step of the survey. It does not save the results.
   * @param code survey code
   */
  public static void prev(String code) {
    
  }
}
