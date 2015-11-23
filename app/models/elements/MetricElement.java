/*******************************************************************************
 *        File: MetricElement.java
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import models.ModelWithTimestamp;
import models.assessment.Assessment;
import play.data.validation.MaxSize;
import play.i18n.Messages;

@Entity(name="metric_element")
public class MetricElement extends BaseElement {
    
  @MaxSize(100000)
  @Lob
  @Column(columnDefinition="TEXT")
  public String description;
    
  public MetricElement(Assessment assessment, String title, String description) {
    super(assessment);
    this.type = "metric";
    this.title = title;
    this.description = description;
  }
  
  public long numOfQuestions() {
    long size = QuestionElement.count("parent.parent.id="+this.id);
    return size;
  }

}
