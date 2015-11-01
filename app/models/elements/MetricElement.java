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

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import models.ModelWithTimestamp;
import models.assessment.Assessment;
import play.i18n.Messages;

@Entity(name="metric_element")
public class MetricElement extends ModelWithTimestamp {
  
  @ManyToOne
  public Assessment assessment;
  
  public String description;

  @OneToMany
  public List<SubMetricElement> children;
  
  public MetricElement(Assessment assessment) {
    this.assessment = assessment;
    children = new ArrayList<SubMetricElement>();
  }
  
}
