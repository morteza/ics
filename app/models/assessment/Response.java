/*******************************************************************************
 *        File: Response.java
 *    Revision: 2
 * Description: 
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: July 3, 2015
 *     Project: itrc.ics
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package models.assessment;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import models.Account;
import models.ModelWithTimestamp;
import models.elements.QuestionElement;

@Entity(name="response")
public class Response extends ModelWithTimestamp {
  
  @ManyToOne
  QuestionElement question;
  
  public String content;
  
  public Response(QuestionElement question, String content) {
    this.question = question;
    this.content = content;
  }

}
