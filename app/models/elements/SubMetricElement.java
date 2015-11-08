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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import models.ModelWithTimestamp;
import models.assessment.Assessment;
import play.data.validation.MaxSize;
import play.i18n.Messages;

@Entity(name="sub_metric_element")
public class SubMetricElement extends BaseElement {
  
  @ManyToOne
  public MetricElement parent;
  
  @MaxSize(100000)
  @Lob
  @Column(columnDefinition="TEXT")
  public String description;
  
  public SubMetricElement(MetricElement parent) {
    super(parent.assessment);
    this.type = "sub_metric";
    this.parent = parent;
  }

  public void setParent(MetricElement parent) {
    this.parent = parent;
    this.assessment = parent.assessment;
  }
  
}
