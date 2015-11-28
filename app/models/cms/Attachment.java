package models.cms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PostRemove;
import javax.persistence.PreRemove;

import models.ModelWithTimestamp;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.Blob;
import play.libs.MimeTypes;

@Entity
public class Attachment extends ModelWithTimestamp {

  @Required
  @Unique
  public String name;

  @Required
  @MaxSize(255)
  public String title;

  public Blob data;

  public Attachment() {

  }

  public Attachment(String name, String title, File file) throws FileNotFoundException {
    this.name = name;
    this.title = title;
    data = new Blob();
    data.set(new FileInputStream(file), MimeTypes.getContentType(file.getName()));
  }


  public static Attachment findByName(String name) {
    Attachment attachment = find("byName", name).first();
    return attachment;
  }
  
  public String toString() {
    return name;
  }
  
  @PreRemove
  public void removeData() {
    if (data != null && data.exists()) {
      data.getFile().delete();
    }
  }
}
