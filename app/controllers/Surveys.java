/**
 * 
 */
package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Account;
import models.AccountRole;
import models.survey.Answer;
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
    
    next(code, null, false);
  }
  
  /**
   * Move to the next page in a survey. It's the main navigator of the survey.
   * @param code survey code
   * @param partialResult
   */
  public static void next(String code, String[] answers, boolean isUniversal) {
    
    // Check if the survey is already started for current session.
    if (!session.contains(PAGE_COOKIE_PREFIX + code) || !session.contains(SURVEY_COOKIE_PREFIX + code)) {
      start(code);
    }

    int page = Integer.valueOf(session.get(PAGE_COOKIE_PREFIX + code));
    
    // Increase page counter
    session.put(PAGE_COOKIE_PREFIX + code, page+1);
    
    
    Survey survey = Survey.findByCode(code);
    notFoundIfNull(survey);
    
    Account me = Security.connected();

    // Cleanup past survey results
    Answer.delete("actor=? AND survey=?", me, survey);
    
    // Storing results.
    if (answers!=null && answers.length>0) {
      for (int i=0;i<answers.length; i++) {
        Answer a = null;
        if (isUniversal) {
          Question question = survey.universalQuestions.get(i);
          a = new Answer(me, survey, question, answers[i]);
        } else {
          a = new Answer(me, survey, i, answers[i]);        
        }
        a.save();
      }      
    }

    
    //TODO find total pages
    int pages = 2;

    if (page >= pages) {
      results(code);
    }
    
    if (page==0) {
      //Show universal questions on the first page.
      List<Question> questions = survey.universalQuestions;
      render("surveys/universal_questions.html", survey, page, pages, questions);
    }
    
    //TODO find questions belong to the current page.
    List<String> questions = survey.questions;
            
    render("surveys/questions.html", survey, page, pages, questions);

  }

  public static void results(String code) {
    Survey survey = Survey.findByCode(code);
    notFoundIfNull(survey);
    
    session.remove(SURVEY_COOKIE_PREFIX + code);
    session.remove(PAGE_COOKIE_PREFIX + code);

    Account me = Security.connected();
    
    List<Answer> yess = Answer.find("actor=:me AND survey=:survey AND content='yes'")
        .setParameter("me", me)
        .setParameter("survey", survey)
        .fetch();

    
    List<Answer> nos = Answer.find("actor=:me AND survey=:survey AND content='no'")
        .setParameter("me", me)
        .setParameter("survey", survey)
        .fetch();

    
    List<Answer> nones = Answer.find("actor=:me AND survey=:survey AND content='none'")
        .setParameter("me", me)
        .setParameter("survey", survey)
        .fetch();

    
    List<Answer> alts = Answer.find("actor=:me AND survey=:survey AND content='alt'")
        .setParameter("me", me)
        .setParameter("survey", survey)
        .fetch();
    
    render("surveys/results.html", survey, yess, nos, nones, alts);
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
