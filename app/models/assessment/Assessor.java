package models.assessment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import models.ModelWithTimestamp;
import play.data.validation.MaxSize;
import play.db.jpa.Blob;

@Entity
@Table(name="assessor")
public class Assessor extends ModelWithTimestamp {
  public String firstName;
  public String lastName;
  public String email;
  public String role;
  public String phone;
  public String orgName;
  public String orgGrade;
  public String orgStandards;
  public String orgContact;
  public String siteName;
  public String siteCity;
  public String siteRegion;
  public String siteBrand;
  public String siteNetworkAddress;
  public Blob supplementaryFile;
  
  @MaxSize(10000)
  @Lob
  @Column(columnDefinition="TEXT")
  public String description;
  
  @MaxSize(10000)
  @Lob
  @Column(columnDefinition="TEXT")
  public String comments;
  
  @MaxSize(10000)
  @Lob
  @Column(columnDefinition="TEXT")
  public String contacts;
}
