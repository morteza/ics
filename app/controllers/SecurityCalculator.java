/*******************************************************************************
 *        File: SecurityCalculator.java
 *    Revision: 1
 * Description: Simple calculator for security measures.
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: July 6, 2014
 *     Project: itrc.cset
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package controllers;

import play.mvc.Controller;

public class SecurityCalculator extends Controller {
  public static void index() {
    render("calculator/index.html");
  }
}
