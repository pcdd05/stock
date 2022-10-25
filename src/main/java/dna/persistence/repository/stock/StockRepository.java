package dna.persistence.repository.stock;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import dna.rest.pojo.CalendarDayInfo;
import dna.rest.pojo.DailyInfo;

/**
 * <pre>
 * CouponRepository: add Class Javadoc here.
 * </pre>
 * 
 */
public interface StockRepository {
  
  /**
   * 匯入List<dailyInfo>
   * @param params
   * @return
   */
  public int insertDailyInfoList(Map<String, Object> params);
  
  /**
   * 匯入List<CalendarDayInfo>
   * @param params
   * @return
   */
  public int insertHolidayCalendar(Map<String, Object> params);
  
  /**
   * 取得當日日期九日內資料-For Today Use
   * @param String tblName
   * @return
   */
  public List<DailyInfo> get9daysInfosFromToday(@Param("TBLNAME") String tblName);
  
  /**
   * 取得特定日期九日內資料-For Specific Date Use
   * @param params
   * @return
   */
  public List<DailyInfo> get9daysInfosFromDate(Map<String, Object> params);
  
  /**
   * 更新dailyInfo KD值
   * @param params
   * @return
   * */
  public int updateKDValue(Map<String, Object> params);
  
  /**
   * 取得單年份資料
   * @param params
   * @return
   */
  public List<DailyInfo> get1yearInfos(Map<String, Object> params);
  
  /**
   * 取得今日是否為假日之行事曆資料
   * @return
   */
  public CalendarDayInfo getTodayIsHolidayInfo();  
  

  
}
