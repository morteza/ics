/*******************************************************************************
 *        File: QuestionElement.java
 *    Revision: 2
 * Description: An element represents default multiple choice questions.
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: Oct 31, 2015
 *     Project: itrc.ics
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package models.elements;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import models.ModelWithTimestamp;
import models.assessment.Assessment;
import play.i18n.Messages;

@Entity(name="question_element")
public class QuestionElement extends BaseElement {
  
  public String content;
  public Integer rank;
  
  @ManyToOne
  public SubMetricElement parent;
    
  public QuestionElement(SubMetricElement parent) {
    super(parent.assessment);
    this.type = "question";
    this.parent = parent;
  }
  
}
