/*******************************************************************************
 *        File: AnswerFormat.java
 *    Revision: 3
 * Description: 
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: July 3, 2015
 *     Project: itrc.ics
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package models.survey;

public enum AnswerFormat {
  STATEMENT,
  TEXT,
  MULTIPLE_CHOICE,
  SCALE;
  
  public static AnswerFormat parse(String type) {
    if (type.equalsIgnoreCase("statement") || type.equalsIgnoreCase("text")) {
      return STATEMENT;
    }else if (type.equalsIgnoreCase("text_question")) {
      return TEXT;
    }else if (type.equalsIgnoreCase("scale_question")) {
      return SCALE;
    }else if (type.equalsIgnoreCase("multiple_choice_question")) {
      return MULTIPLE_CHOICE;
    }
    return null;
  }
}
