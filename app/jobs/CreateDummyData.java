/*******************************************************************************
 *        File: CreateDummyData.java
 *    Revision: 2
 * Description: Create some dummy data if nothing is stored in database.
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: August 26, 2014
 *     Project: itrc.ics
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package jobs;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import models.Account;
import models.AccountRole;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.libs.WS;
import play.libs.WS.FileParam;

@OnApplicationStart
public class CreateDummyData extends Job {

  public void doJob() throws Exception {
    if (Account.count()==1) { //Except for the default administrator
      Account account2 = new Account("Mori Ansari", "ansarinia@me.com", "password");
      account2.role = AccountRole.ADMINISTRATOR;
      account2.save();      
    }

  }

}
