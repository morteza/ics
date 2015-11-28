/*******************************************************************************
 *        File: CMS.java
 *    Revision: 2
 * Description: Content management system.
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: August 4, 2014
 *     Project: cg125.cut
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package controllers.cms;

import java.net.URLDecoder;
import java.util.List;

import controllers.Application;
import controllers.Security;
import models.AccountRole;
import models.cms.Attachment;
import models.cms.Feedback;
import models.cms.Page;
import play.cache.Cache;
import play.data.validation.Error;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.libs.Codec;
import play.mvc.Controller;
import play.mvc.With;
import utils.NoSecurityCheck;
import utils.SecurityCheck;

public class CMS extends Controller {

  /**
   * Displays a CMS page.
   * @param pageName
   */
  public static void show(String name) throws Exception {
    Page page = Page.findByName(name);
    notFoundIfNull(page);
    if (page.isPublished) {
      notFoundIfNull(page.template);
      render("cms/"+page.template, page);
    } else {
      boolean isAdmin = Security.check(AccountRole.ADMINISTRATOR);
      if (isAdmin) {
        render("cms/"+page.template, page, isAdmin);        
      }
      forbidden();
    }
  }

  public static void blog() {
    List<Page> pages = Page.find("template=:template AND isPublished=true ORDER BY createdAt DESC")
        .setParameter("template", "blog_entry.html").fetch();
    render("cms/blog.html", pages);
  }

  public static void support() {
    List<Page> pages = Page.find("template=:template AND isPublished=true ORDER BY createdAt DESC")
        .setParameter("template", "support_entry.html").fetch();
    render("cms/support.html", pages);
  }

  public static void contact() {
    Feedback feedback = new Feedback(Security.connected());
    String randomUUID = Codec.UUID();
    render("cms/contact.html", feedback, randomUUID);    
  }
  
  public static void saveFeedback(@Valid Feedback feedback) {
    String captcha = request.params.get("human");
    String randomUUID = request.params.get("randomUUID");
    validation.required(captcha);
    validation.equals(captcha, Cache.get(randomUUID)).message("Invalid captcha. Please type it again.");
    if (validation.hasErrors()) {
      validation.keep();
      flash.error(Messages.get("cms.ErrorWhileSavingFeedback"));      
      randomUUID = Codec.UUID();
      render("cms/contact.html", feedback, randomUUID);
    }

    //feedback.authorInfo = Security.connected();
    
    feedback.save();
    
    //flash.success(Messages.get("cms.FeedbackSaved"));
    render("cms/contact.html", feedback);
  }
  
  /**
   * Render an attached file or image.
   *
   * @param name attachment unique name.
   */
  public static void attachment(String name) {
    Attachment attachment = Attachment.findByName(name);
    notFoundIfNull(attachment);
    response.contentType = attachment.data.type();
    renderBinary(attachment.data.get());
  }
}
