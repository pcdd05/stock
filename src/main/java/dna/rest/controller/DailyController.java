package dna.rest.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import dna.config.AppProperties;
import dna.core.service.DailyService;
import dna.core.service.DataService;
import dna.persistence.repository.stock.StockRepository;
import dna.rest.common.RestException;
import dna.rest.common.RestResource;
import dna.rest.common.RestResourceFactory;
import dna.rest.pojo.CalendarDayInfo;
import dna.rest.pojo.DailyInfo;


@Controller
@Import({AppProperties.class})
@RequestMapping(value = "/rest/v1")
public class DailyController {
  
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
  
//  /**
//   * 取得單支股票當月資訊
//   * 
//   * @param date the date
//   * @param stockNo the stockNo
//   * @return the DailyInfo
//   */
//  @RequestMapping(value = "/getDailyInfoForMonth", method = RequestMethod.GET)
//  public ResponseEntity<RestResource<String>> getDailyInfoForMonth(@RequestParam(required = false) String date, @RequestParam(required = true) String stockNo) {
//    long s = System.currentTimeMillis();
//
//    System.out.println("/rest/v1/getDailyInfoForMonth start");
//
//    RestResource<String> body = RestResourceFactory.newInstance();
//    try {
//      if (StringUtils.isBlank(date)) {
//        date = StockUtil.getToday("yyyyMMdd");
//        System.out.println("queryDate: " + date);
//      } else {
//        // do nothing, 使用輸入日期
//        System.out.println("queryDate: " + date);
//      }
//      
//      // getData
//      List<DailyInfo> dataList = new ArrayList<DailyInfo>();
//      dataList = dailyService.getMonthlyInfo(date, stockNo);
//      // importData
//      List<String> resultList = null;
//      
//      body.setItems(resultList);
//    } catch (Exception e) {
//      System.err.println(e.getMessage() + e);
//      throw new RestException(e.getMessage(), body);
//    } finally {
//      System.out.println("/rest/v1/getDailyInfoForMonth complete, " + (System.currentTimeMillis() - s) + "ms spent.");
//    }
//    return new ResponseEntity<RestResource<String>>(body, HttpStatus.OK);
//  }
  
  /**
   * 更新單支股票當日交易資料
   * 
   * @param stockNo the stockNo
   * @return the DailyInfo
   */
  @RequestMapping(value = "/updateTodayInfo", method = RequestMethod.GET)
  public ResponseEntity<RestResource<String>> updateTodayInfo(@RequestParam(required = false) String stockNo) {
    long s = System.currentTimeMillis();

    System.out.println("/rest/v1/updateTodayInfo start");

    RestResource<String> body = RestResourceFactory.newInstance();
    List<String> resultList = new ArrayList<String>();
    try {
      // 取得今日交易資料
      DailyInfo todayInfo = dailyService.getTodayInfo(stockNo);
      if (todayInfo != null) {
        // 匯入資料庫
        List<DailyInfo> dailyInfoList = new ArrayList<>();
        dailyInfoList.add(todayInfo);
        resultList.addAll(dataService.insertDailyInfoList(stockNo, dailyInfoList));
        // 計算並更新KD值
        DailyInfo todayInfoKD = dailyService.computeKD(stockNo, todayInfo, true);
        resultList.addAll(dataService.updateDailyInfoKD(stockNo, todayInfoKD));
      } else {
        resultList.add("no data today.");
      }
      
      body.setItems(resultList);
    } catch (Exception e) {
      System.err.println(e.getMessage() + e);
      throw new RestException(e.getMessage(), body);
    } finally {
      System.out.println("/rest/v1/updateTodayInfo complete, " + (System.currentTimeMillis() - s) + "ms spent.");
    }
    return new ResponseEntity<RestResource<String>>(body, HttpStatus.OK);
  }
  
  /**
   * 檢核單支股票今日交易建議
   * 
   * @param stockNo the stockNo
   * @return the DailyInfo
   */
  @RequestMapping(value = "/checkKD", method = RequestMethod.GET)
  public ResponseEntity<RestResource<String>> checkKD(@RequestParam(required = false) String stockNo) {

    long s = System.currentTimeMillis();

    RestResource<String> body = RestResourceFactory.newInstance();
    List<String> resultList = new ArrayList<String>();

    CalendarDayInfo todayCalendarInfo = stockRepository.getTodayIsHolidayInfo();
    if (todayCalendarInfo != null && "是".equals(todayCalendarInfo.getIsHoliday())) {
      resultList.add("是假日就不用檢核今日交易建議啦, so do nothing...");
      // 是假日就不用檢核今日交易建議啦, so do nothing...
    } else {
      // 1.檢核KD值 (K<20 買 ; K>80 賣)
      System.out.println("/rest/v1/checkKD start");
      try {
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
            resultList.add(LocalDate.now() + "【" + stockNo + stockName + "】 前一交易日(" + lastDate + ") 收盤價 = " + lastEndPrice + ", K值 = " + lastKValue + ", K<20 建議買進!!");
            System.out.println(LocalDate.now() + "【" + stockNo + stockName + "】 前一交易日(" + lastDate + ") 收盤價 = " + lastEndPrice + ", K值 = " + lastKValue + ", K<20 建議買進!!");
          } else if (lastKValue > 80) {
            resultList.add(LocalDate.now() + "【" + stockNo + stockName + "】 前一交易日(" + lastDate + ") 收盤價 = " + lastEndPrice + ", K值 = " + lastKValue + ", K>80 建議賣出!!");
            System.out.println(LocalDate.now() + "【" + stockNo + stockName + "】 前一交易日(" + lastDate + ") 收盤價 = " + lastEndPrice + ", K值 = " + lastKValue + ", K>80 建議賣出!!");
          }
        } else {
          resultList.add("no lastday data, something must go wrong...");
        }
        
      } catch (Exception e) {
        System.err.println(e.getMessage() + e);
      } finally {
        System.out.println("/rest/v1/checkKD complete, " + (System.currentTimeMillis() - s) + "ms spent.");
      }
    }
    
    body.setItems(resultList);
    return new ResponseEntity<RestResource<String>>(body, HttpStatus.OK);
  }

}
