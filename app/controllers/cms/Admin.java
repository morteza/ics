/*******************************************************************************
 *        File: Admin.java
 *    Revision: 2
 * Description: Content management system administration.
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: April 15, 2015
 *     Project: cg125.cut
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package controllers.cms;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import controllers.Security;
import models.AccountRole;
import models.cms.Attachment;
import models.cms.Feedback;
import models.cms.Page;
import play.data.parsing.UrlEncodedParser;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.With;
import utils.NoSecurityCheck;
import utils.SecurityCheck;

@With(controllers.Security.class)
@SecurityCheck(AccountRole.ADMINISTRATOR)
public class Admin extends Controller {

  public static void index() {
    List<Page> pages = Page.find("order by modifiedAt desc").fetch();
    render("cms/admin.html", pages);
  }

  public static void feedbacks() {
    List<Feedback> feedbacks = Feedback.find("order by modifiedAt desc").fetch();
    render("cms/feedbacks.html", feedbacks);
  }

  public static void deleteFeedback(Long id) {
    Feedback feedback = Feedback.findById(id);
    notFoundIfNull(feedback);
    feedback.delete();
    //page.isDeleted = true;
    //page.save();
    flash.success(Messages.get("cms.FeedbackRemoved"));
    flash.keep();
    feedbacks();
  }
  
  public static void edit(String name) {
    Page page = Page.findByName(name);
    notFoundIfNull(page);
    renderTemplate("cms/edit.html", page);
  }
  
  public static void add() {
    Page page = new Page();
    page.author = Security.connected();
    renderTemplate("cms/edit.html", page);
  }

  public static void save(@Valid Page page) {
    if (validation.hasErrors()) {
      flash.error(Messages.get("cms.ErrorWhileSavingPage"));
      render("cms/edit.html", page);
    }

    //page.author = Security.connected();
    
    // Add blog/ or support/ prefixes
/*  if(page.isBlogEntry() && !page.name.startsWith("blog_")) {
      page.name = "blog_" + page.name;
    }
    if(page.isSupportEntry() && !page.name.startsWith("support_")) {
      page.name = "support_" + page.name;
    }
*/    

    page.save();
    
    flash.success(Messages.get("cms.PageSaved"));
    flash.keep();
    index();
  }
  
  public static void publish(String name) {
    Page page = Page.findByName(name);
    page.author = Security.connected();
    notFoundIfNull(page);
    page.isPublished = true;
    page.save();
    index();
  }

  public static void unpublish(String name) {
    Page page = Page.findByName(name);
    notFoundIfNull(page);
    page.isPublished = false;
    page.save();
    index();
  }

  public static void delete(String name) {
    Page page = Page.findByName(name);
    notFoundIfNull(page);
    page.delete();
    //page.isDeleted = true;
    //page.save();
    flash.success(Messages.get("cms.PageRemoved"));
    flash.keep();
    index();
  }
  
  public static void fileManager() {
    todo();
  }

}
