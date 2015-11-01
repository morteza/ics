/*******************************************************************************
 *        File: ImportQuestionsCsv.java
 *    Revision: 2
 * Description: 
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
import models.assessment.Assessment;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.libs.WS;
import play.libs.WS.FileParam;

public class ImportQuestionsCsv extends Job {
  
  Assessment assessment;
  File csvFile;
  
  public ImportQuestionsCsv(Assessment assessment, File csvFile) {
    this.assessment = assessment;
  }
  
  public void doJob() throws Exception {
    System.out.println("CSV has been imported!");
  }

}
