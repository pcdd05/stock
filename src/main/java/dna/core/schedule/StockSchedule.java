package dna.core.schedule;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import dna.core.service.DailyService;
import dna.core.service.DataService;
import dna.persistence.repository.stock.StockRepository;
import dna.rest.pojo.CalendarDayInfo;
import dna.rest.pojo.DailyInfo;


@Component
public class StockSchedule {
  
  private static final String[] stockNoArray = {"0050", "0056"};

  /** The env. */
  @Value("${app.env}")
  private String env;
  
  /** The daily service. */
  @Autowired
  private DailyService dailyService;
  /** The data service. */
  @Autowired
  private DataService dataService;
  /** The stock repository. */
  @Autowired
  private StockRepository stockRepository;

  /** 
   * 每日下午2點30分執行更新股票當日交易資料
   **/
  @Scheduled(cron = "0 30 14 ? * MON-SAT") // 每週一~週六的下午2點30分執行 (如週六為補班日仍會有交易資料)
  private void updateTodayInfoSchedule() {
    
    long s = System.currentTimeMillis();

    System.out.println("updateTodayInfoSchedule start.");
    
    for (String stockNo : stockNoArray) {
      try {
        // 取得今日交易資料(包含KD值)
        DailyInfo todayInfo = dailyService.getTodayInfo(stockNo);
        if (todayInfo != null) {
          // 匯入資料庫
          List<DailyInfo> dailyInfoList = new ArrayList<>();
          dailyInfoList.add(todayInfo);
          dataService.insertDailyInfoList(stockNo, dailyInfoList);
          // 計算並更新KD值
          DailyInfo todayInfoKD = dailyService.computeKD(stockNo, todayInfo, true);
          dataService.updateDailyInfoKD(stockNo, todayInfoKD);
        } else {
          System.out.println("no data today.");
        }
      } catch (Exception e) {
        System.err.println(e.getMessage() + e);
      } finally {
        System.out.println("updateTodayInfoSchedule complete, " + (System.currentTimeMillis() - s) + "ms spent.");
      }
    }
  }

  /** 
   * 每週一~週六的上午9點30分檢核今日交易建議
   * 
   * 1.檢核KD值 (K<20 買 ; K>80 賣)
   * TODO
   * 2.評估季線修正建議
   * 3.寄信
   * 4.除息填息監測功能
   **/
  @Scheduled(cron = "0 30 9 ? * MON-SAT") // 每週一~週六的上午9點30分執行 (如週六為補班日仍為交易日)
  private void checkKDSchedule() {
    
    long s = System.currentTimeMillis();

    CalendarDayInfo todayCalendarInfo = stockRepository.getTodayIsHolidayInfo();
    if (todayCalendarInfo != null && "是".equals(todayCalendarInfo.getIsHoliday())) {
      System.out.println("是假日就不用檢核今日交易建議啦, so do nothing...");
      // 是假日就不用檢核今日交易建議啦, so do nothing...
    } else {
      // 1.檢核KD值 (K<20 買 ; K>80 賣)
      System.out.println("checkKDSchedule start: 1. checkKDValue");
      try {
        for (String stockNo : stockNoArray) {
          String tblName = "TBL".concat(stockNo);
          List<DailyInfo> nineDaysInfoList = stockRepository.get9daysInfosFromToday(tblName);
          // 按日期排序, 由近排到遠 ex:06/15, 06/14, 06/13...
          Collections.sort(nineDaysInfoList, Comparator.comparing(DailyInfo::getTransactionDate, (t1, t2) -> t2.compareTo(t1)));
          // 取得前一個交易日的交易資料(包含KD值)
          DailyInfo lastdayInfo = nineDaysInfoList.get(0); // 因上午9:30執行時, 當日尚無交易資料, 因此第一筆即為最近一日交易日資料
          if (lastdayInfo != null) {
            String stockName = lastdayInfo.getStockName();
            LocalDate lastDate = lastdayInfo.getTransactionDate();
            double lastEndPrice = lastdayInfo.getEndPrice();
            double lastKValue = lastdayInfo.getkValue();
            // TODO ++寄信功能
            if (lastKValue < 20) {
              System.out.println(LocalDate.now() + "【" + stockNo + stockName + "】 前一交易日(" + lastDate + ") 收盤價 = " + lastEndPrice + ", K值 = " + lastKValue + ", K<20 建議買進!!");
            } else if (lastKValue > 80) {
              System.out.println(LocalDate.now() + "【" + stockNo + stockName + "】 前一交易日(" + lastDate + ") 收盤價 = " + lastEndPrice + ", K值 = " + lastKValue + ", K>80 建議賣出!!");
            }
          } else {
            System.out.println("no lastday data, something must go wrong...");
          }
        }
      } catch (Exception e) {
        System.err.println(e.getMessage() + e);
      } finally {
        System.out.println("1. checkKDValue complete, " + (System.currentTimeMillis() - s) + "ms spent.");
      }
      
      // 2.其他檢核
//      System.out.println("checkKDSchedule start: 1. checkKDValue");
//      try {
//        for (String stockNo : stockNoArray) {
//          String tblName = "TBL".concat(stockNo);
//          List<DailyInfo> nineDaysInfoList = stockRepository.get9daysInfosFromToday(tblName);
//          // 按日期排序, 由近排到遠 ex:06/15, 06/14, 06/13...
//          Collections.sort(nineDaysInfoList, Comparator.comparing(DailyInfo::getTransactionDate, (t1, t2) -> t2.compareTo(t1)));
//          // 取得前一個交易日的交易資料(包含KD值)
//          DailyInfo lastdayInfo = nineDaysInfoList.get(1);
//          if (lastdayInfo != null) {
//            double lastKValue = lastdayInfo.getkValue();
//            // TODO 寄信功能
//            if (lastKValue < 20) {
//              System.out.println(stockNo + ", K<20 買");
//            } else if (lastKValue > 80) {
//              System.out.println(stockNo + ", K>80 賣");
//            }
//          } else {
//            System.out.println("no lastday data, something must go wrong...");
//          }
//        }
//      } catch (Exception e) {
//        System.err.println(e.getMessage() + e);
//      } finally {
//        System.out.println("1. checkKDValue complete, " + (System.currentTimeMillis() - s) + "ms spent.");
//      }
        
    }
  }
  
  /** 
   * 每年1月1日上午9點0分執行匯入年度行事曆
   **/
  @Scheduled(cron = "0 0 9 1 1 ?") // 每年1月1日上午9點0分執行
  private void importHolidayCalendar() {

    long s = System.currentTimeMillis();

    System.out.println("importHolidayCalendar start.");

    try {
      
      dataService.importHolidayCalendar(false);
      
    } catch (Exception e) {
      System.err.println(e.getMessage() + e);
    } finally {
      System.out.println("importHolidayCalendar complete, " + (System.currentTimeMillis() - s) + "ms spent.");
    }
  }
  
}
