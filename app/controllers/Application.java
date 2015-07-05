/*******************************************************************************
 *        File: Application.java
 *    Revision: 2
 * Description: General actions.
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: August 4, 2014
 *     Project: itrc.cset
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package controllers;

import models.Account;
import models.AccountRole;
import play.CorePlugin;
import play.cache.Cache;
import play.libs.Images;
import play.mvc.Controller;
import play.mvc.With;
import utils.NoSecurityCheck;
import utils.SecurityCheck;

/**
 * Common application-wide action.
 *
 */
@With(Security.class)
public class Application extends Controller {

  /**
   * Shows the main page.
   */
  @NoSecurityCheck
  public static void index() {
    render("application/index.html");
  }

  /**
   * Shows list of available experiments, and more actions.
   * Controls are changed in the view according to the user role.
   */
  @SecurityCheck(AccountRole.REGISTERED)
  public static void dashboard() {    
    Account me = Security.connected();
    notFoundIfNull(me);

    render("application/dashboard.html");
  }

  /**
   * To provide details on the current status of the backend and server.
   * Accessible to the administrators only.
   */
  @SecurityCheck(AccountRole.ADMINISTRATOR)
  public static void status() {
    render("application/status.html");
  }

  /**
   * Sends a json object via ajax contains current details of the backend and server.
   */
  @SecurityCheck(AccountRole.ADMINISTRATOR)
  public static void statusJson() {
    String statusJson = CorePlugin.computeApplicationStatus(true);
    renderJSON(statusJson);
  }
  
  @NoSecurityCheck
  public static void captcha(String id) {
    Images.Captcha captcha = Images.captcha();
    String code = captcha.getText("#2185d0");
    Cache.set(id, code, "5mn");
    renderBinary(captcha);
  }

}
