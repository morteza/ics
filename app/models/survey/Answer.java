/*******************************************************************************
 *        File: Answer.java
 *    Revision: 1
 * Description: 
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: July 3, 2015
 *     Project: itrc.ics
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package models.survey;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import models.Account;
import models.ModelWithTimestamp;

@Entity(name="answer")
public class Answer extends ModelWithTimestamp {
  
  @ManyToOne
  Survey survey;
  
  @ManyToOne
  Account actor;
  
  int questionIndex;
  
  @ManyToOne
  Question question;
  
  public String content;
  
  public Answer(Account actor, Survey survey, int question, String content) {
    this.actor = actor;
    this.survey = survey;
    this.questionIndex = question;
    this.content = content;
  }
  
  public Answer(Account actor, Survey survey, Question question, String content) {
    this.actor = actor;
    this.survey = survey;
    this.question = question;
    this.content = content;
  }
}
