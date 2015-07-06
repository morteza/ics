/*******************************************************************************
 *        File: Survey.java
 *    Revision: 2
 * Description: 
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: July 3, 2015
 *     Project: itrc.cset
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package models.survey;

import java.util.ArrayList;
import java.util.List;

public class Survey {
  
  public String title;
  
  public List<Question> questions;
  
  public Survey() {
    questions = new ArrayList<Question>();
  }
}
