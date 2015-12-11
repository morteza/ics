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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import models.ModelWithTimestamp;
import models.assessment.Assessment;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.i18n.Messages;

@Entity(name="question_element")
public class QuestionElement extends BaseElement {

  @ManyToOne
  public SubMetricElement parent;
  
  @MaxSize(100000)
  @Lob
  @Column(columnDefinition="TEXT")
  public String description;
  
  public enum SeverityLevel {
    LOW,
    MODERATE,
    HIGH,
    VERY_HIGH,
    ALL
  };
  
  public SeverityLevel level;
  
  public QuestionElement(SubMetricElement parent) {
    super(parent.assessment);
    this.type = "question";
    this.parent = parent;
    this.level = SeverityLevel.MODERATE;
    this.rank = 0;
  }

  public void setParent(SubMetricElement parent) {
    this.parent = parent;
    this.assessment = parent.assessment;
  }
  
}
