/*******************************************************************************
 *        File: ModelWithTimestamp.java
 *    Revision: 1
 * Description: Data models with createdAt and modifiedAt timestamps.
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: April 14, 2014
 *     Project: itrc.cset
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package models;

import java.util.Date;

import javax.persistence.MappedSuperclass;
import javax.persistence.PreUpdate;

import play.data.validation.Required;
import play.db.jpa.Model;

@MappedSuperclass
public class ModelWithTimestamp extends Model {

  @Required
  public Date createdAt = new Date();

  @Required
  public Date modifiedAt = new Date(createdAt.getTime());
  
  public Boolean isDeleted;
  public Date deletedAt;

  public ModelWithTimestamp() {
    this.createdAt = new Date();
    this.modifiedAt= new Date();
    this.isDeleted = false;
  }
  
  @PreUpdate
  public void onPreUpdate() {
    this.modifiedAt = new Date();
  }
  

}
