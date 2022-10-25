package dna.rest.common;

import java.util.Calendar;

/**
 * The Class TimeInterval.
 * 
 * <pre>
 * 
 * </pre>
 */
public class TimeInterval {

  /** The start. */
  private Calendar start;
  
  /** The end. */
  private Calendar end;

  /**
   * Setup.
   * 
   * @param start the start
   * @param end the end
   */
  private void setup(Calendar start, Calendar end) {
    this.setStart(start);
    this.setEnd(end);
  }

  /**
   * Instantiates a new time interval.
   * 
   * @param start the start
   * @param end the end
   */
  public TimeInterval(Calendar start, Calendar end) {
    setup(start, end);
  }

  /**
   * Gets the start.
   * 
   * @return the start
   */
  public Calendar getStart() {
    return start;
  }

  /**
   * Sets the start.
   * 
   * @param start the new start
   */
  public void setStart(Calendar start) {
    this.start = start;
  }

  /**
   * Gets the end.
   * 
   * @return the end
   */
  public Calendar getEnd() {
    return end;
  }

  /**
   * Sets the end.
   * 
   * @param end the new end
   */
  public void setEnd(Calendar end) {
    this.end = end;
  }

  // TODO override toString

}
