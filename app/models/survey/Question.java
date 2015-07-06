/*******************************************************************************
 *        File: Question.java
 *    Revision: 2
 * Description: 
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: July 3, 2015
 *     Project: itrc.cset
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package models.survey;

import play.i18n.Messages;

public class Question {
  public String title;
  
  public String id;
  
  public String text;
  
  public String description;
  
  public AnswerFormat answerFormat;
  
  public Question() {
    this.title = Messages.get("unknown","Unknown");
  }
  
}
