package dna.rest.common;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class RestError.
 * 
 * <pre>
 * 
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RestError {

  /** The code. */
  private Integer code;
  
  /** The message. */
  private String message;

  /** The description. */
  private String description;
  /**
   * Gets the code.
   * 
   * @return the code
   */
  public Integer getCode() {
    return code;
  }

  /**
   * Sets the code.
   * 
   * @param code the new code
   */
  public void setCode(Integer code) {
    this.code = code;
  }

  /**
   * Gets the message.
   * 
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  /**
   * Sets the message.
   * 
   * @param message the new message
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * Gets the description.
   * 
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description.
   * 
   * @param description the new description
   */
  public void setDescription(String description) {
    this.description = description;
  }
  
}
