/*******************************************************************************
 *        File: SubMetricElement.java
 *    Revision: 2
 * Description: 
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: Oct 31, 2015
 *     Project: itrc.ics
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package models.elements;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import models.ModelWithTimestamp;
import models.survey.Survey;
import play.i18n.Messages;

@Entity(name="sub_metric_element")
public class SubMetricElement extends ModelWithTimestamp {
  
  @ManyToOne
  public Survey survey;
  
  public String title;
  public String content;
  
  @ManyToOne
  public MetricElement parent;
  
  @OneToMany
  public List<QuestionElement> questions;
  
  public SubMetricElement(Survey survey) {
    this.survey = survey;
    this.questions = new ArrayList<QuestionElement>();
  }
  
}
