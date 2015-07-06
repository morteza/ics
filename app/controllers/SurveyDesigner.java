package controllers;

import java.util.Date;

import models.AccountRole;
import models.survey.Survey;
import play.i18n.Messages;
import play.mvc.Controller;
import utils.SecurityCheck;

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
    todo();
  }
  
  public static void results(String code) {
    todo();
  }
  
  public static void flow(String code) {
    todo();
  }

  /**
   * Delete a survey by setting its isDeleted flag.
   * @param code
   */
  @SecurityCheck(AccountRole.MAINTAINER)
  public static void remove(String code) {
    Survey s = Survey.findByCode(code);
    notFoundIfNull(s);
    
    s.isDeleted = true;
    s.deletedAt = new Date();
    s.save();
    
    Application.dashboard();
  }
}
