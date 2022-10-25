package dna.rest.common;

import java.util.UUID;

/**
 * A factory for creating RestResource objects.
 * 
 */
public class RestResourceFactory {

  /**
   * New instance.
   * 
   * @param <E> the element type
   * @return the rest resource
   */
  public static <E> RestResource<E> newInstance() {
    RestResource<E> r = new RestResource<E>();
    r.setKey(UUID.randomUUID());
    return r;
  }
  
}
