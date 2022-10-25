package dna.rest.common;

import dna.rest.common.RestResource;

/**
 * <pre> RestServerException. </pre>
 *
 */
public class RestServerException extends RestException {
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 861472030833287587L;

  /**
   * Instantiates a new rest server exception.
   * 
   * @param message the message
   * @param body the body
   */
  public RestServerException(String message, RestResource<? extends Object> body) {
    super(message, body);
  }

}
