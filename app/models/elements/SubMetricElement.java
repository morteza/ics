/*******************************************************************************
 *        File: SubMetricElement.java
 *    Revision: 1
 * Description: 
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: Oct 31, 2015
 *     Project: itrc.ics
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package models.elements;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import models.ModelWithTimestamp;
import play.i18n.Messages;

@Entity(name="sub_metric_element")
public class SubMetricElement extends ModelWithTimestamp {
  
  public String description;
  public Integer rank;
    
  public SubMetricElement() {
  }
  
}
