/*******************************************************************************
 *        File: Feedback.java
 *    Revision: 4
 * Description: User feedbacks.
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: November 16, 2014
 *     Project: itrc.cset
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package models.cms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import models.Account;
import models.ModelWithTimestamp;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import controllers.Security;

/**
 * User feedbacks.
 *
 */
@Entity
public class Feedback extends ModelWithTimestamp {
  
  @ManyToOne
  @Required
  public Account actor;
  
  public String authorEmail;
  public String authorName;
  
  @Lob
  @Column(columnDefinition="TEXT")
  public String extra;
  
  @Lob
  @Column(columnDefinition="TEXT")
  public String content;
  
  public Feedback(Account actor) {
    this.actor = actor;
    this.content = "";
    this.authorName = actor.fullName==null?"":actor.fullName;
    this.authorEmail = actor.email==null?"":actor.email;
    this.extra = "";
  }
}
