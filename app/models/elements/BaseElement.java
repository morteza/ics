package models.elements;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import models.ModelWithTimestamp;
import models.assessment.Assessment;
import play.data.validation.Required;

@MappedSuperclass
public class BaseElement extends ModelWithTimestamp {
  public String title;
  
  @ManyToOne
  public Assessment assessment;

  
  @Required
  public String type = null;

  public String code = null;
  
  @Required
  public Integer rank = 0;

  public BaseElement(Assessment assessment) {
    this.assessment = assessment;
  }

  public String toString() {
    if (code!=null)
      return code;
    return super.toString();
  }
  
  /**
   * Returns an string represents the type of this element.
   * @return
   */
  public String elementType() {
    return type;
  }
}
