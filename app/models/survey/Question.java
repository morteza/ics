/*******************************************************************************
 *        File: Question.java
 *    Revision: 3
 * Description: 
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: July 3, 2015
 *     Project: itrc.ics
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package models.survey;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import models.ModelWithTimestamp;
import play.i18n.Messages;

@Entity(name="question")
public class Question extends ModelWithTimestamp {
  
  @ManyToOne
  public Survey survey;
  
  public String title;
  
  public String description;
  
  public AnswerFormat format;
  
  public Question(Survey survey) {
    survey.save();
    this.survey = survey;
    this.title = Messages.get("unknown","Unknown");
  }
  
}
