package dna.rest.common;

import org.springframework.core.NestedRuntimeException;

/**
 * The Class RestException.
 * 
 * <pre>
 * 
 * </pre>
 */
public class RestException extends NestedRuntimeException {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -8212350120534140752L;
  
  /** The body. */
  private RestResource<?> body;

  /**
   * Instantiates a new rest exception.
   * 
   * @param message the message
   * @param body the body
   */
  public RestException(String message, RestResource<? extends Object> body) {
    super(message);
    this.setBody(body);
  }

  /**
   * Gets the body.
   * 
   * @return the body
   */
  public RestResource<?> getBody() {
    return body;
  }

  /**
   * Sets the body.
   * 
   * @param body the new body
   */
  public void setBody(RestResource<?> body) {
    this.body = body;
  }

}