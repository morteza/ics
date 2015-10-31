/*******************************************************************************
 *        File: Survey.java
 *    Revision: 4
 * Description: 
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: July 3, 2015
 *     Project: itrc.ics
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package models.survey;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import models.Account;
import models.ModelWithTimestamp;
import models.elements.MetricElement;
import models.elements.QuestionElement;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.data.validation.Unique;

@Entity
@Table(name="survey")
public class Survey extends ModelWithTimestamp {
  
  @Required
  @MinSize(1)
  public String title;
  
  @Required
  @ManyToOne
  public Account createdBy;

  @Required
  @MinSize(1)
  @MaxSize(10000)
  @Lob
  @Column(columnDefinition="TEXT")
  public String description;

  @Required
  @Unique
  public String code;
  
  // This is maximum alpha value to be compliance of a security standard;
  public Double complianceThreshold;
  
  public Boolean isPublished;
  
  public Boolean isPublic;
  
  public SurveyType type;

  @OneToMany
  public List<MetricElement> metrics;
  
  @OneToMany
  public List<QuestionElement> questions;

  public Survey() {
    metrics = new ArrayList<MetricElement>();
    questions = new ArrayList<QuestionElement>();
    code = generateCode(5);
    //TODO set isPublished to false, and add an action to publish it manually.
    isPublished = true;
    isDeleted = false;
    isPublic = true;
    type= SurveyType.REGULAR;
  }  
  
  private String generateCode(int size) {
    String chars = "234567890abcdefghijkmnopqrstuvwxyz";
    String result = "";
    //TODO prevent duplicate
    Random gen = new Random();
    for (int i = 0; i<size; i++)
      result += chars.charAt(gen.nextInt(chars.length()));
    return result;
  }
  
  public static Survey findByCode(String code) {
    return Survey.find("byCode", code).first();
  }
}
