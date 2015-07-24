package controllers;

import java.util.Date;

import models.AccountRole;
import models.survey.Survey;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.With;
import utils.SecurityCheck;

@With(Security.class)
@SecurityCheck(AccountRole.REGISTERED)
public class SurveyDesigner extends Controller {
  
  public static void create(Survey survey) {
    //validation.valid(survey);
    validation.required(survey.title).message("Title is required.");
    validation.required(survey.description).message("Description is required.");
    validation.minSize(survey.title, 2).message("Minimum size for title is 2 characters.");
    if (validation.hasErrors()) {
      params.flash();
      validation.keep();
      flash.error(Messages.get("survey.InvalidTitleOrDescription", "Invalid title or description."));
      flash.keep();
      Application.dashboard();
    }
    survey.createdBy = Security.connected();
    survey.save();
    flow(survey.code);
  }
  
  public static void update() {
    todo();
  }
  
  public static void general(String code) {
    Survey survey = Survey.findByCode(code);
    notFoundIfNull(survey);

    render("designer/general.html", survey);
  }
  
  public static void results(String code) {
    Survey survey = Survey.findByCode(code);
    notFoundIfNull(survey);

    render("designer/results.html", survey);
  }
  
  public static void flow(String code) {
    Survey survey = Survey.findByCode(code);
    notFoundIfNull(survey);

    render("designer/flow.html", survey);  }

  public static void publish(String code) {
    todo();
  }
  
  /**
   * Delete a survey by setting its isDeleted flag.
   * @param code
   */
  @SecurityCheck(AccountRole.MAINTAINER)
  public static void remove(String code) {
    Survey survey = Survey.findByCode(code);
    notFoundIfNull(survey);
    
    survey.isDeleted = true;
    survey.deletedAt = new Date();
    survey.save();
    
    Application.dashboard();
  }
}
