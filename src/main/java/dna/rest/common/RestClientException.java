package dna.rest.common;

import dna.rest.common.RestResource;

/**
 * <pre> RestClientException. </pre>
 *
 * @author Rocko Tseng
 */
public class RestClientException extends RestException {


  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -609543780401108820L;


  /**
   * Instantiates a new rest client exception.
   * 
   * @param message the message
   * @param body the body
   */
  public RestClientException(String message, RestResource<? extends Object> body) {
    super(message, body);
  }

}
