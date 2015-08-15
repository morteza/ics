/*******************************************************************************
 *        File: Accounts.java
 *    Revision: 5
 * Description: User account management including creation, deletion, editing,
 *              and showing all user accounts. Its actions mostly need
 *              Administration or Super User levels of access.
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: August 4, 2014
 *     Project: itrc.cset
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package controllers;

import java.util.Date;
import java.util.List;

import models.Account;
import models.AccountRole;
import play.cache.Cache;
import play.data.validation.Error;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.libs.Codec;
import play.mvc.Controller;
import play.mvc.With;
import utils.NoSecurityCheck;
import utils.SecurityCheck;

/**
 * Provides action related to the account management, e.g. registration, edit, reset password, and delete.
 *
 */
@With(Security.class)
public class Accounts extends Controller {

  /**
   * Default action that lists all accounts
   * Only administrators are authenticated to access this action.
   */
  @SecurityCheck(AccountRole.ADMINISTRATOR)
  public static void index() {
    list();
  }

  /**
   * Lists all persisted accounts except for players and deleted.
   * Only administrators are authenticated to access this action.
   */
  @SecurityCheck(AccountRole.ADMINISTRATOR)
  public static void list() {
    List<Account> accounts = Account.find("isDeleted=false and (role!=models.AccountRole.GUEST)").fetch();
    render("accounts/list.html", accounts);    
  }

  /**
   * Lists all persisted accounts.
   * Only administrators are authenticated to access this action.
   */
  @SecurityCheck(AccountRole.ADMINISTRATOR)
  public static void listAll() {
    List<Account> accounts = Account.find("isDeleted=false").fetch();
    render("accounts/list.html", accounts);    
  }
  
  /**
   * Renders registration page to create a new account.
   * Accessible to all users.
   */
  @NoSecurityCheck
  public static void signup() {
    
    boolean isAlreadyRegistered = Security.check(AccountRole.REGISTERED);
    
    if (isAlreadyRegistered) {
      flash.error(Messages.get("security.YouAreAlreadyRegistered"));
      Application.dashboard();
    }
    
    Account account = new Account();
    account.role = AccountRole.REGISTERED;
    String randomUUID = Codec.UUID();
    render("accounts/signup.html", account, randomUUID);
  }

  /**
   * Create new account based on passed email and password.
   * If email is currently unavailable, then shows warning to the user.
   * On success goes back to the index page. This action is available to all users.
   * TODO redirect to the registration page on fail.
   * @param email a valid email
   * @param password
   */
  @NoSecurityCheck
  public static void create(@Valid Account account) {

    String captcha = request.params.get("human");
    String randomUUID = request.params.get("randomUUID");

    validation.required(captcha);
    validation.equals(captcha, Cache.get(randomUUID)).message("Invalid captcha. Please type it again.");

    if (Account.findByEmail(account.email) != null) {
      validation.keep();
      flash.error(Messages.get("accounts.EmailIsNotAvailable"));
      randomUUID = Codec.UUID();
      render("accounts/signup.html", account, randomUUID);
    }

    if(validation.hasErrors()){
      validation.keep();
      flash.error(Messages.get("accounts.AccountRegistrationFailed"));
      if (Security.check(AccountRole.ADMINISTRATOR))
        list();
      randomUUID = Codec.UUID();
      render("accounts/signup.html", account, randomUUID);
    }

    account.passwordHash = play.libs.Crypto.passwordHash(account.passwordHash);
    account.role = AccountRole.REGISTERED;
    account.save();
    
    flash.success("accounts.SignedUpSuccessfully");
    flash.keep();
    if (Security.check(AccountRole.ADMINISTRATOR))
      list();
    Security.login();
  }

  /**
   * Shows profile details for the passed account.
   * TODO Make this only available to the user himself, or administrators
   * @param id
   */
  @SecurityCheck(AccountRole.MAINTAINER)
  public static void profile(Long id) {
    Account account = Account.findById(id);
    render("accounts/profile.html", account);    
  }

  /**
   * Shows a form with all current values of selected account details for further edits.
   * TODO only accessible to administrators or if current user chose himself.
   * @param id
   */
  @SecurityCheck(AccountRole.REGISTERED)
  public static void edit(Long id) {
    boolean isAllowed = Security.check(AccountRole.ADMINISTRATOR);
    isAllowed = isAllowed || (Security.connected().id == id);
    //FIXME Prevent editing other accounts if current user is not an administrator
    if (!isAllowed)
      forbidden();
    Account account = Account.findById(id);
    render("accounts/edit.html", account);
  }

  /**
   * Update selected account fields to the passed values.
   * TODO only accessible to administrators or if current user chose himself.
   * @param id
   */
  @SecurityCheck(AccountRole.REGISTERED)
  public static void update(Long id) {
    
    // it's allowed if user is admin, or editing own profile.
    boolean isAllowed = Security.check(AccountRole.ADMINISTRATOR);
    isAllowed = isAllowed || (Security.connected().id == id);
    
    if (!isAllowed)
      forbidden();
    
    Account account = Account.findById(id);
    notFoundIfNull(account);
    account.edit(params.getRootParamNode(), "account");

    validation.valid(account);
    if(validation.hasErrors()){
      params.flash();
      validation.keep();
      render("accounts/edit.html", account);
    }

    // Update account.passwordHash if request[password] is not empty
    String password = params.get("password");
    if (password!=null && password.length()>7) {
      account.passwordHash = play.libs.Crypto.passwordHash(password);
    }

    account.save();

    flash.success(Messages.get("security.AccountUpdated"));
    
    // Redirect profile edits to their own page
    if (Security.connected().id == id)
      profile(id);

    index();
  }

  /**
   * Remove desired account from database. It marks the account as deleted instead of
   * removing all traces, so previous events won't be removed.
   * TODO only accessible to administrators or if current user chose himself.
   * @param id
   */
  @SecurityCheck(AccountRole.ADMINISTRATOR)
  public static void delete(Long id) {
    Account account = Account.findById(id);
    if (account.role.ordinal() >= AccountRole.ADMINISTRATOR.ordinal()) {
      flash.error(Messages.get("security.AccountCannotBeDeleted"));
      flash.keep();
      list();
    }

    account.isDeleted = true;
    account.deletedAt = new Date();
    account.save();

    flash.success(Messages.get("security.AccountDeleted"));
    flash.keep();
    list();
  }

  /**
   * Marks an account as confirmed via email, if the passed token is valid.
   * It finds respective account using the token.
   * @param confirmationToken
   */
  @SecurityCheck(AccountRole.GUEST)
  public static void confirm(String confirmationToken) {
    Account account = Account.find("byConfirmationToken", confirmationToken).first();
    
    if (account==null) {
      error(Messages.get("security.ConfirmationError"));
    }
    
    account.confirmationToken = "";
    account.save();

    flash.success(Messages.get("security.AccountConfirmed"));
    flash.keep();
    Security.login();
  }
  
  /**
   * Shows a form to enter an email, to reset password if forgotten.
   */
  @NoSecurityCheck
  public static void resetPassword() {
    render("accounts/reset.html");
  }
  
  /**
   * Sends an email to the user with a link to reset password.
   * Then returns to the login page.
   * TODO redirect to the login page on success.
   * @param email
   */
  @NoSecurityCheck
  public static void sendResetPasswordEmail(String email) {
    Account account = Account.findByEmail(email);
    if (account==null) {
      flash.error(Messages.get("security.InvalidResetEmail"));
      flash.put("email", email);
      flash.keep();
      resetPassword();
    }
    try {
      //TODO send reset password email
      flash.success(Messages.get("security.ResetPasswordEmailSent"));
      flash.keep();
      Security.login();
    } catch (Throwable e) {
      Application.index();
    }
  }
  
}
