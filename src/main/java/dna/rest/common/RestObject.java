package dna.rest.common;


import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Base class.
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RestObject implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 8076208956122711777L;

  /** The key. */
  private UUID key;

  /** The status. */
  private Integer status;

  /** The errors. */
  private List<RestError> errors;

  /**
   * Gets the key.
   * 
   * @return the key
   */
  public UUID getKey() {
    return key;
  }

  /**
   * Sets the key.
   * 
   * @param key the new key
   */
  public void setKey(UUID key) {
    this.key = key;
  }

  /**
   * Gets the status.
   * 
   * @return the status
   */
  public Integer getStatus() {
    return status;
  }

  /**
   * Sets the status.
   * 
   * @param status the new status
   */
  public void setStatus(Integer status) {
    this.status = status;
  }

  /**
   * Gets the errors.
   * 
   * @return the errors
   */
  public List<RestError> getErrors() {
    return errors;
  }

  /**
   * Sets the errors.
   * 
   * @param errors the new errors
   */
  public void setErrors(List<RestError> errors) {
    this.errors = errors;
  }
  
}
