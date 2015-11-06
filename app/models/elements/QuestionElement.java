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
  
  public Integer rank;
  
  @ManyToOne
  public SubMetricElement parent;
  
  public String description;
  
  public enum SeverityLevel {
    LOW,
    MODERATE,
    HIGH
  };
  
  public SeverityLevel level;
  
  public QuestionElement(SubMetricElement parent) {
    super(parent.assessment);
    this.type = "question";
    this.parent = parent;
    this.level = SeverityLevel.MODERATE;
  }

  public void setParent(SubMetricElement parent) {
    this.parent = parent;
    this.assessment = parent.assessment;
  }
  
}
