package dna.rest.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.springframework.format.Formatter;

/**
 * The Class TimeIntervalFormatter.
 * 
 * <pre>
 * 
 * </pre>
 */
public class TimeIntervalFormatter implements Formatter<TimeInterval> {

  /** The separator. */
  private String separator = "~";

  /** The format. */
  private String format = "HHmm";

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.format.Printer#print(java.lang.Object, java.util.Locale)
   */
  @Override
  public String print(TimeInterval object, Locale locale) {
    return object.getStart().get(Calendar.HOUR_OF_DAY) + object.getStart().get(Calendar.MINUTE)
        + getSeparator() + object.getEnd().get(Calendar.HOUR_OF_DAY)
        + object.getEnd().get(Calendar.MINUTE);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.format.Parser#parse(java.lang.String, java.util.Locale)
   */
  @Override
  public TimeInterval parse(String text, Locale locale) throws ParseException {
    // expected value: 0000~2359
    SimpleDateFormat parser = new SimpleDateFormat(format);
    String[] times = text.split(getSeparator());
    if (times.length == 2) {
      Calendar start = Calendar.getInstance();
      Calendar end = Calendar.getInstance();
      start.setTime(parser.parse(times[0]));
      end.setTime(parser.parse(times[1]));
      return new TimeInterval(start, end);
    }
    throw new IllegalArgumentException(text);
  }

  /**
   * Gets the separator.
   * 
   * @return the separator
   */
  public String getSeparator() {
    return separator;
  }

  /**
   * Sets the separator.
   * 
   * @param separator the new separator
   */
  public void setSeparator(String separator) {
    this.separator = separator;
  }

  /**
   * Gets the format.
   * 
   * @return the format
   */
  public String getFormat() {
    return format;
  }

  /**
   * Sets the format.
   * 
   * @param format the new format
   */
  public void setFormat(String format) {
    this.format = format;
  }

}