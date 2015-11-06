/*******************************************************************************
 *        File: AssessmentDesigner.java
 *    Revision: 5
 * Description: Design an assessment.
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: Aug 8, 2014
 *     Project: itrc.ics
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;

import models.AccountRole;
import models.assessment.Assessment;
import models.elements.MetricElement;
import models.elements.QuestionElement;
import models.elements.SubMetricElement;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.With;
import utils.SecurityCheck;

@With(Security.class)
@SecurityCheck(AccountRole.MAINTAINER)
public class AssessmentDesigner extends Controller {
  
  public static void create(Assessment assessment) {
    //validation.valid(assessment);
    validation.required(assessment.title).message("Title is required.");
    validation.required(assessment.description).message("Description is required.");
    validation.minSize(assessment.title, 2).message("Minimum size for title is 2 characters.");
    if (validation.hasErrors()) {
      params.flash();
      validation.keep();
      flash.error(Messages.get("assessment.InvalidTitleOrDescription", "Invalid title or description."));
      flash.keep();
      Application.dashboard();
    }
    assessment.createdBy = Security.connected();
    assessment.save();
    general(assessment.code);
  }
  
  public static void save(Assessment assessment) {
    notFoundIfNull(assessment);
    notFoundIfNull(assessment.code);
    assessment.save();
    flash.success(Messages.get("assessment.UpdatedSuccessfully"));
    flash.keep();
    general(assessment.code);
  }
  
  public static void general(String code) {
    Assessment assessment = Assessment.findByCode(code);
    notFoundIfNull(assessment);

    render("designer/general.html", assessment);
  }
  
  public static void results(String code) {
    Assessment assessment = Assessment.findByCode(code);
    notFoundIfNull(assessment);

    render("designer/results.html", assessment);
  }
  
  public static void importQuestions(String code, File file) {
    Assessment assessment = Assessment.findByCode(code);
    notFoundIfNull(assessment);

    //TODO new ImportQuestionsCsv(assessment, file).now();
    
    try {
      BufferedReader br = new BufferedReader(new FileReader(file));
      String line;
      while ((line = br.readLine()) != null) {
        //TODO: assessment.questions.add(line);
      }
      br.close();
    } catch (Exception e) {
      e.printStackTrace();
      flash.error(Messages.get("An error occured while reading questions file."));
      elements(assessment.code);
    }
    
    assessment.save();
    
    flash.success(Messages.get("ics.designer.QuestionFileHasBeenImportedSuccessfully"));
    
    elements(code);

  }
 

  /**
   * Show a view to manage assessment elements.
   * @param code assessment code
   */
  public static void elements(String code) {
    Assessment assessment = Assessment.findByCode(code);
    notFoundIfNull(assessment);

    boolean disableNewQuestion = (0 == SubMetricElement.count("assessment", assessment));
    boolean disableNewSubMetric = (0 == MetricElement.count("assessment", assessment));
        
    render("designer/elements.html", assessment, disableNewSubMetric, disableNewQuestion);    
  }

  public static void publish(String code) {
    todo();
  }
  
  /**
   * Delete an assessment by setting its isDeleted flag.
   * @param code
   */
  @SecurityCheck(AccountRole.MAINTAINER)
  public static void remove(String code) {
    Assessment assessment = Assessment.findByCode(code);
    notFoundIfNull(assessment);
    
    assessment.isDeleted = true;
    assessment.deletedAt = new Date();
    assessment.save();
    
    Application.dashboard();
  }

}
