package models.cms;

import java.util.*;

import javax.persistence.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import controllers.Security;
import models.Account;
import models.ModelWithTimestamp;
import play.data.binding.*;
import play.data.validation.*;
import play.db.jpa.Model;

@Entity
@Table(name="cms_page")
public class Page extends ModelWithTimestamp {

  @MaxSize(255)
  @Required
  @Unique
  public String name;

  @Required
  @MaxSize(255)
  public String title;

  @Lob
  @Required
  public String content;

  @Required
  @ManyToOne
  public Account author;
  
  @Required
  public String template;
  
  public Boolean isPublished = false;

  public Page() {
    this.isPublished = true;
    this.content = "";
    this.template = "blog_entry.html";
  }

  public static Page findByName(String name) {
    Page page = find("byName", name).first();
    return page;
  }
  
  public String snippet() {
    String plainContent =  content.replaceAll("\\<.*?\\>", "");
    return WordUtils.abbreviate(plainContent, 256, -1, "...");
  }
  
  public String toString() {
    return name;
  }
  
  public Boolean isBlogEntry() {
    return "blog_entry.html".equalsIgnoreCase(this.template);
  }

  
  public Boolean isSupportEntry() {
    return "support_entry.html".equalsIgnoreCase(this.template);
  }
}
