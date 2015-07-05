/*******************************************************************************
 *        File: API.java
 *    Revision: 2
 * Description: Several API calls to access core functionality. This controller
 *              might expose some internal functionalities of other controllers.
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: August 5, 2014
 *     Project: itrc.cset
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package controllers;

import java.io.File;
import java.io.InputStreamReader;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import models.AccountRole;
import models.cms.Attachment;
import play.Play;
import play.i18n.Lang;
import play.libs.WS;
import play.mvc.Controller;
import play.mvc.With;
import utils.NoSecurityCheck;
import utils.SecurityCheck;

/**
 * Implements exposed actions for programmable access to the application.
 *
 */
@With(Security.class)
@SecurityCheck(AccountRole.MAINTAINER)
public class API extends Controller {


  /**
   * Uploads a file and returns an id.
   * @param attachment
   * @throws Exception
   */
  public static void uploadFile(File attachment) {
    try {
      Attachment storedAttachment = new Attachment(attachment.getName(), attachment.getName(), attachment);
      storedAttachment.save();
      renderText(storedAttachment.id);      
    } catch (Exception e) {
      e.printStackTrace();
      error("Error while uploading file.");
    }
  }
  
  /**
   * Download a file located by its id.
   * @param id
   */
  public static void downloadFile(Long id) {
    notFoundIfNull(id);
    Attachment attachment = Attachment.findById(id);
    notFoundIfNull(attachment);

    // render not found message
    if (!attachment.data.exists()) {
      notFound("File not found.");
    }
    
    response.setContentTypeIfNotSet(attachment.data.type());
    renderBinary(attachment.data.get());
  }
  
  @NoSecurityCheck
  public static void changeUserLanguage(String lang) {
    if ("en".equalsIgnoreCase(lang) || "fa".equalsIgnoreCase(lang)) {
      Lang.change(lang);
    }
    //TODO REMOVE this rediect
    Application.index();
  }

}
