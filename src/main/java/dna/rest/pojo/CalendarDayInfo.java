package dna.rest.pojo;

import java.time.LocalDate;

public class CalendarDayInfo {
  
  public CalendarDayInfo() {
    super();
  }

  /** 日期 */
  private LocalDate date;
  
  /** 節日名稱 */
  private String name;

  /** 是否為假日 */
  private String isHoliday;
  
  /** 假日類型 */
  private String holidayCategory;
  
  /** 敘述 */
  private String description;

  /**
   * @return the date
   */
  public LocalDate getDate() {
    return date;
  }

  /**
   * @param date the date to set
   */
  public void setDate(LocalDate date) {
    this.date = date;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the isHoliday
   */
  public String getIsHoliday() {
    return isHoliday;
  }

  /**
   * @param isHoliday the isHoliday to set
   */
  public void setHoliday(String isHoliday) {
    this.isHoliday = isHoliday;
  }

  /**
   * @return the holidayCategory
   */
  public String getHolidayCategory() {
    return holidayCategory;
  }

  /**
   * @param holidayCategory the holidayCategory to set
   */
  public void setHolidayCategory(String holidayCategory) {
    this.holidayCategory = holidayCategory;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  
}
