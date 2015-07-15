/*******************************************************************************
 *        File: Bootstrap.java
 *    Revision: 3
 * Description: 
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: August 4, 2014
 *     Project: itrc.cset
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package jobs;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import models.Account;
import models.AccountRole;
import play.i18n.Lang;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.libs.WS;
import play.libs.WS.FileParam;

@OnApplicationStart
public class Bootstrap extends Job {

  public void doJob() throws Exception {
    
    // Set default language to Farsi (fa-IR).
    Lang.set("fa");
    
    //TODO Query accounts for administrators, and create if it's no administrator
    if (Account.count() == 0) {
      // Create default administrator account.
      Account account = new Account("Administrator", "admin@itrc.ac.ir", "hello123");
      account.role = AccountRole.ADMINISTRATOR;
      account.save();
    }
  }

}
