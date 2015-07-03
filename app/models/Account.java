/*******************************************************************************
 *        File: Account.java
 *    Revision: 4
 * Description:
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: August 8, 2014
 *     Project: itrc.cset
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package models;

import javax.persistence.Entity;

import play.data.validation.Email;
import play.data.validation.Password;
import play.data.validation.Required;
import play.data.validation.Unique;

@Entity
public class Account extends ModelWithTimestamp {

  @Required
  public String fullName;
  
  @Required
  @Unique
  @Email
  public String email;

  @Required
  @Password
  public String passwordHash;

  public Boolean deleted;

  public String confirmationToken;
  public String resetPasswordToken;

  public AccountRole role = AccountRole.GUEST;
  
  public String remoteAddress;
  
  public String mTurkId;

  public Account() {
    this.role = AccountRole.GUEST;
    this.deleted = false;
    this.remoteAddress = "";
  }

  public Account(String fullName, String email, String password) {
    this.fullName = fullName;
    this.email = email;
    this.passwordHash = play.libs.Crypto.passwordHash(password);
    this.deleted = false;
    this.remoteAddress = "";
    this.role = AccountRole.REGISTERED;
    //TODO generate confirmation token
  }

  public static Account findByEmail(String email) {
    Account account = find("byEmail", email).first();
    if (account==null) {
      play.Logger.error("Email not found: " + email);
    }
    return account;
  }

  public static Account authenticate(String email, String password) {
    Account account = findByEmail(email);
    if (account!=null && account.passwordHash.equals(play.libs.Crypto.passwordHash(password))) {
      return account;
    }
    return null;
  }

  public void changePassword(String password) {
    this.passwordHash = play.libs.Crypto.passwordHash(password);
    this.save();
  }

  public boolean isConfirmed() {
    return confirmationToken==null || confirmationToken=="";
  }

}
