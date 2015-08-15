/*******************************************************************************
 *        File: Security.java
 *    Revision: 4
 * Description: Security core module.
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: Aug 8, 2014
 *     Project: itrc.cset
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package controllers;

import java.util.Date;

import org.hibernate.validator.util.privilegedactions.GetConstructor;

import models.Account;
import models.AccountRole;
import play.Play;
import play.data.validation.Required;
import play.libs.Crypto;
import play.libs.Time;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Util;
import utils.HttpsRequired;
import utils.NoSecurityCheck;
import utils.SecurityCheck;

import com.google.gson.JsonElement;

//@With(HttpsRequired.class)
public class Security extends Controller {

  /**
   * This method returns the current connected user.
   * @return
   */
  @Util
  public static Account connected() {
    return findAndStoreSessionAccount();
  }

  /**
   * Indicate if a user is currently connected
   * @return  true if the user is connected
   */
  public static boolean isConnected() {
    return session.contains("account.id");
  }
  
  @Before
  static void checkAccess() throws Throwable {
    
    NoSecurityCheck noCheck = getActionAnnotation(NoSecurityCheck.class);
    if(noCheck != null) {
      return;
    }
    
    noCheck = getControllerAnnotation(NoSecurityCheck.class);
    if(noCheck != null) {
      return;
    }
        
    // Authentication
    if(!isConnected()) {
      flash.put("url", "GET".equals(request.method) ? request.url : Play.ctxPath + "/"); // seems a good default
      login();
    }

    // action checks
    SecurityCheck check = getActionAnnotation(SecurityCheck.class);
    if(check != null) {
      check(check);
    }

    // controller checks
    check = getControllerInheritedAnnotation(SecurityCheck.class);
    if(check != null) {
      check(check);
    }
  }

  private static void check(SecurityCheck check) throws Throwable {
    for(AccountRole profile : check.value()) {
      if(!check(profile)) {
        forbidden();
      }
    }
  }
  
  public static Boolean check(AccountRole role) {
    Account account = connected();
    if (account!=null && account.role.compareTo(role) >= 0) {
      return true;
    }
    return false;
  }

  @NoSecurityCheck
  public static void authenticate(@Required String username, String password, boolean remember) throws Throwable {
    Account account = Account.authenticate(username, password);
    if(validation.hasErrors() || account==null) {
      flash.keep("url");
      flash.error("security.AuthenticationError");
      params.flash();
      login();
    }
    // Mark user as connected
    session.put("account.id", account.id);
    
    // Remember if needed
    if(remember) {
      Date expiration = new Date();
      String duration = Play.configuration.getProperty("secure.remember.duration","30d"); 
      expiration.setTime(expiration.getTime() + Time.parseDuration(duration) * 1000 );
      response.setCookie("remember", Crypto.sign(account.id + "-" + expiration.getTime()) + "-" + account.id + "-" + expiration.getTime(), duration);

    }
    // Redirect to the original URL (or /)
    redirectToOriginalURL();
  }

  // ~~~ Utils

  static void redirectToOriginalURL() throws Throwable {
    String url = flash.get("url");
    if(url == null) {
      url = Play.ctxPath + "/";
    }
    redirect(url);
  }


  /**
   * Find current username from session or cookie.
   */
  @Util
  private static Account findAndStoreSessionAccount() {
    
    Account account = null;

    // Check cookies if nothing is in session.
    if (!session.contains("account.id")) {
      Http.Cookie remember = request.cookies.get("remember");
      if(remember != null) {
        int firstIndex = remember.value.indexOf("-");
        int lastIndex = remember.value.lastIndexOf("-");
        if (lastIndex > firstIndex) {
          String sign = remember.value.substring(0, firstIndex);
          String restOfCookie = remember.value.substring(firstIndex + 1);
          String accountId = remember.value.substring(firstIndex + 1, lastIndex);
          String time = remember.value.substring(lastIndex + 1);
          Date expirationDate = new Date(Long.parseLong(time)); // surround with try/catch?
          Date now = new Date();
          if(expirationDate!=null
              && expirationDate.after(now)
              && Crypto.sign(restOfCookie).equals(sign)) {
            // Put cookie into the session
            try {
              account = Account.findById(Long.valueOf(accountId));
              account.remoteAddress = request.remoteAddress;
              account.save();
              session.put("account.id", account.id);
            } catch(Exception e) {
              // remove invalid account from session and cookie
              request.cookies.remove("remember");
            }
          }
        }
      }
    } else {
      // Already in session
      account = Account.findById(Long.valueOf(session.get("account.id")));
    }
    
    if (account==null) {
      // Create empty acocunt with player role
      account = new Account();
      account.save();
      session.put("account.id", account.id);
        
      // Store account.id in cookie
      Date expiration = new Date();
      String duration = Play.configuration.getProperty("secure.remember.duration","30d");
      expiration.setTime(expiration.getTime() + Time.parseDuration(duration) * 1000l);
      if (response!=null)
        response.setCookie("remember", Crypto.sign(account.id + "-" + expiration.getTime()) + "-" + account.id + "-" + expiration.getTime(), duration);
    }
    
    
    return account;
    
  }


  @NoSecurityCheck
  public static void login() {
    flash.keep("url");
    render("accounts/login.html");
  }

  @NoSecurityCheck
  public static void logout() {
    session.clear();
    response.removeCookie("remember");
    flash.success("security.LoggedOutSuccessfully");
    login();
  }
  
}
