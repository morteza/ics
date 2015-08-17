package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;

import jobs.ImportQuestionsCsv;
import models.AccountRole;
import models.survey.Survey;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.With;
import utils.SecurityCheck;

@With(Security.class)
@SecurityCheck(AccountRole.ADMINISTRATOR)
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
  
  public static void update(Survey survey) {
    notFoundIfNull(survey);
    notFoundIfNull(survey.code);
    survey.save();
    flash.success("Survey has been updated!");
    flash.keep();
    general(survey.code);
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
  
  public static void importQuestions(String code, File file) {
    Survey survey = Survey.findByCode(code);
    notFoundIfNull(survey);

    //TODO new ImportQuestionsCsv(survey, file).now();
    if (survey.questions==null)
      survey.questions = new ArrayList<String>();
    
    try {
      BufferedReader br = new BufferedReader(new FileReader(file));
      String line;
      while ((line = br.readLine()) != null) {
        survey.questions.add(line);
      }
      br.close();
    } catch (Exception e) {
      e.printStackTrace();
      flash.error(Messages.get("An error occured while reading questions file."));
      render("designer/flow.html", survey);
    }
    
    survey.save();
    
    flash.success(Messages.get("survey.designer.QuestionFileHasBeenImportedSuccessfully"));
    
    questions(code);

  }
  
  public static void questions(String code) {
    Survey survey = Survey.findByCode(code);
    notFoundIfNull(survey);

    render("designer/questions.html", survey);    
  }
  
  public static void flow(String code) {
    Survey survey = Survey.findByCode(code);
    notFoundIfNull(survey);

    render("designer/flow.html", survey);
  }
  
  public static void updateFlow(String code, String json) {
    Survey survey = Survey.findByCode(code);
    notFoundIfNull(survey);
    
    renderText(Messages.get("survey.designer.FlowHasBeenUpdated"));
  }

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
