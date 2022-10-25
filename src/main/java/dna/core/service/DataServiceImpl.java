package dna.core.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import dna.core.util.StockUtil;
import dna.persistence.repository.stock.StockRepository;
import dna.rest.pojo.CalendarDayInfo;
import dna.rest.pojo.DailyInfo;

public class DataServiceImpl implements DataService {
  
  /** The stock repository. */
  @Autowired
  private StockRepository stockRepository;
  /** The daily service. */
  @Autowired
  private DailyService dailyService;
  
  @Autowired
  private RestService restService;
  
  private final static String HOLIDAYCALENDARURL = "https://data.ntpc.gov.tw/api/datasets/308DCD75-6434-45BC-A95F-584DA4FED251/json?";


  /**
   * 匯入自2010.01起至今單一個股所有股價
   *  
   * @param String stockNo
   * @return List<String>
   * @throws InterruptedException 
   * */
  @Override
  public List<String> importAllData(String stockNo) throws InterruptedException {
    List<String> resultList = new ArrayList<>();
    
    boolean hasNext = true;
    int year = 2010;
    int month = 1;
    while (hasNext) {
      StringBuilder sbDate = new StringBuilder();
      sbDate.append(String.valueOf(year));
      if (month < 10) {
        sbDate.append("0");
      }
      sbDate.append(String.valueOf(month));
      sbDate.append("01"); // day = 01
      
      // getData
      List<DailyInfo> dataList = new ArrayList<DailyInfo>();
      dataList = dailyService.getMonthlyInfo(sbDate.toString(), stockNo);
      resultList.add("import data of " + sbDate.substring(0, 6));
      if (dataList != null && !dataList.isEmpty()) {
        // insert to db
        resultList.addAll(this.insertDailyInfoList(stockNo, dataList));
      } else { // dataList為空時, 表示查無資料, 將hasNext設為false停止迴圈
        hasNext = false;
      }
      
      // 接著下一個迴圈設定date
      if (month < 12) {
        month++;
        Thread.sleep(3000);
        System.out.println("sleep 3 secs");
      } else {
        year++;
        month = 1;
        Thread.sleep(15000);
        System.out.println("sleep 15 secs");
      }
    }
    return resultList;
    
  }
  
  
  /**
   * 匯入dailyInfoList資料 into db
   * 
   * @param String stockNo
   * @param List<DailyInfo> dataList
   * @return List<String>
   * */
  @Override
  public List<String> insertDailyInfoList(String stockNo, List<DailyInfo> dataList) {
    List<String> resultList = new ArrayList<>();
    try {
      Map<String, Object> params = new HashMap<>();
      String tblName = "TBL".concat(stockNo);
      System.out.println("tblName: " + tblName);
      params.put("TBLNAME", tblName);
      params.put("DATALIST", dataList);

      // insert into db
      int rc = stockRepository.insertDailyInfoList(params);
      if (rc > 0) {
        resultList.add("insert dailyInfoList 筆數: " + rc);
      } else {
        resultList.add("insert failed.");
      }
    } catch (Exception e) {
      System.err.println(e.getMessage() + e);
      resultList.add(e.getMessage());
    }

    return resultList;
  }
  
  /**
   * 更新dailyInfo KD資料 into db
   * 
   * @param String stockNo
   * @param DailyInfo dailyInfo
   * @return List<String>
   * */
  @Override
  public List<String> updateDailyInfoKD(String stockNo, DailyInfo dailyInfo) {
    List<String> resultList = new ArrayList<>();
    try {
      Map<String, Object> params = new HashMap<>();
      String tblName = "TBL".concat(stockNo);
      System.out.println("updateDailyInfoKD tblName: " + tblName);
      params.put("TBLNAME", tblName);
      params.put("TRANSACTIONDATE", dailyInfo.getTransactionDate().toString());
      params.put("RSVVALUE", dailyInfo.getRsvValue());
      params.put("KVALUE", dailyInfo.getkValue());
      params.put("DVALUE", dailyInfo.getdValue());
      params.put("KDDIFFVALUE", dailyInfo.getKdDiffValue());

      // update into db
      int rc = stockRepository.updateKDValue(params);
      if (rc > 0) {
        resultList.add("updateKDValue succeed, 交易日: " + params.get("TRANSACTIONDATE"));
        System.out.println("updateKDValue succeed, 交易日: " + params.get("TRANSACTIONDATE"));
      } else {
        resultList.add("updateKDValue failed.");
      }
    } catch (Exception e) {
      System.err.println(e.getMessage() + e);
      resultList.add(e.getMessage());
    }

    return resultList;
  }
  
  /**
   * 匯入2010.01.13之歷史KD值, 以便往後計算
   * 
   * @param String stockNo
   * @return List<String>
   * */
  @Override
  public List<String> manulInsert20100113KDValue(String stockNo){
    List<String> resultList = new ArrayList<>();
    // 輸入2010.01.13 KD值
    try {
      Map<String, Object> params = new HashMap<>();
      String tblName = "TBL".concat(stockNo);
      System.out.println("manulInsert20100113KDValue tblName: " + tblName);
      params.put("TBLNAME", tblName);
      params.put("TRANSACTIONDATE", "2010-01-13");
      if ("0050".equals(stockNo)) {
        params.put("KVALUE", 71.43);
        params.put("DVALUE", 80.50);
      } else if ("0056".equals(stockNo)) {
        params.put("KVALUE", 76.06);
        params.put("DVALUE", 83.53);
      } 
      // 做update
      int rc = stockRepository.updateKDValue(params);
      if (rc > 0) {
        resultList.add("manulInsert20100113KDValue data筆數: " + rc);
        System.out.println("manulInsert20100113KDValue data筆數: " + rc);
      } else {
        resultList.add("manulInsert20100113KDValue failed.");
      }
    } catch (Exception e) {
      System.err.println(e.getMessage() + e);
      resultList.add(e.getMessage());
    }
    return resultList;
  }
  
  /** 
   * 逐一計算每日KD值並更新db 
   * 
   * @param String stockNo
   * @return List<String>
   * @throws Exception 
   * */
  @Override
  public List<String> updateKDValue(String stockNo) throws Exception{
    List<String> resultList = new ArrayList<>();
    Map<String, Object> params = new HashMap<>();
    String tblName = "TBL".concat(stockNo);
    System.out.println("updateKDValue tblName: " + tblName);
    params.put("TBLNAME", tblName);
    
    for (int year = 2010; year <= LocalDate.now().getYear(); year++) { // 資料起始年度為2010, 逐一計算至今年止
      // Step.1 先取得單一年份所有資料
      List<DailyInfo> dataList = new ArrayList<DailyInfo>();
      StringBuilder sbStartDate = new StringBuilder();
      StringBuilder sbEndDate = new StringBuilder();
      sbStartDate.append(String.valueOf(year)).append("-01-01");
      sbEndDate.append(String.valueOf(year)).append("-12-31");
      params.put("STARTDATE", sbStartDate.toString());
      params.put("ENDDATE", sbEndDate.toString());
      dataList = stockRepository.get1yearInfos(params);
      if (dataList != null && !dataList.isEmpty()) {
        // 按日期排序, 由遠排到近 ex:01/04, 01/05, 01/06...
        Collections.sort(dataList, Comparator.comparing(DailyInfo::getTransactionDate, (t1, t2) -> t2.compareTo(t1)).reversed());
        if (year == 2010) { // 第一年度資料, KD值自2010.01.14日起算, 故需處理dataList
          // 過濾資料List中交易日在2010.01.13日後的資料才保留使用
          dataList = dataList.stream().filter(item -> item.getTransactionDate().isAfter(StockUtil.transferMinguoDateToADLocalDate("99/01/13"))).collect(Collectors.toList());
        } else { 
          // 其餘年度計算所有資料, so do nothing...
        }
        
        // Step.2 將單一年份內每日交易資料做computeKD, 取得已存入KD值的dailyInfoKD
        for (DailyInfo dailyInfo : dataList) {
          DailyInfo dailyInfoKD = dailyService.computeKD(stockNo, dailyInfo, false);
          
          // Step.3 update to db
          resultList.addAll(this.updateDailyInfoKD(stockNo, dailyInfoKD));
        }
      }
    }

    return resultList;
  }
  
  /**
   * 匯入假日行事曆
   *  
   * @return List<String>
   * @throws InterruptedException 
   * */
  @Override
  public List<String> importHolidayCalendar(boolean isInit) throws InterruptedException {
    List<String> resultList = new ArrayList<>();
    List<CalendarDayInfo> dataList = new ArrayList<>();
    
    // API取得所有年度假日行事曆資料
    boolean hasNextPage = true;
    int page = 0;
    int size = 500;
    for ( ; hasNextPage; page++) {
      List<CalendarDayInfo> tempList = getHolidayCalendar(page, size);
      if (tempList != null && !tempList.isEmpty()) {
        dataList.addAll(tempList);
      } else {  // tempList為空時, 表示查無資料, 將hasNextPage設為false停止迴圈
        hasNextPage = false;
      }
    }
    
    if (!isInit) { // 如非首次執行(排程每年度1/1上午執行), 即僅匯入新一年度行事曆, 需先濾除API回傳之舊年度行事曆
      dataList = dataList.stream().filter(item -> LocalDate.now().getYear() == item.getDate().getYear()).collect(Collectors.toList());
    } else {
      // 首次執行匯入全部年度行事曆, so do nothing...
    }
    
    // insert into db
    if (dataList != null && !dataList.isEmpty()) {
      resultList = insertHolidayCalendar(dataList);  
    }
    
    return resultList;
  }
  
  /** 
   * 使用data.ntpc.gov.tw網站資料取得假日行事曆 
   * 
   * @return List<CalendarDayInfo>
   * */
  private List<CalendarDayInfo> getHolidayCalendar(int page, int size) {
    String url = HOLIDAYCALENDARURL + "page=" + page + "&size=" + size;
    String resJson = null;
    int retry = 0;
    
    while(retry < 4) {
        try {
          resJson = restService.getJson(url);
          if (StringUtils.isNotBlank(resJson)) {
            JsonNode response = StockUtil.getMapper().readValue(resJson, new TypeReference<JsonNode>() {});
            if (response != null) {
              return parseHolidayCalendarResponse(response);
            }
          } else {
            System.out.println("query url response isEmpty.");
            retry = 4;
          }
        } catch (Exception e) {
          if (e instanceof JsonParseException) {
            if (resJson.contains("503")) {
              // 503 Service Temporarily Unavailable, 睡15秒再執行一次
              try {
                Thread.sleep(15000);
                retry++;
                System.out.println("response 503, sleep 15 secs & retry time: " + retry);
              } catch (InterruptedException e1) {
                e1.printStackTrace();
              }
            } else {
              retry = 4;
            }
            e.printStackTrace();
          }
        }
      }
    
    return null;
  }
  
  /** 
   * 解析假日行事曆資料並以單日HolidayCalendar存入List 
   * 
   * @param JsonNode response
   * @return List<CalendarDayInfo>
   * */
  private List<CalendarDayInfo> parseHolidayCalendarResponse(JsonNode response) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
    List<CalendarDayInfo> resultList = new ArrayList<>();
    try {
      if (!response.isNull() && response.isArray() && response.size() > 0) { // 回傳有資料時
        for (JsonNode calendarDayData : response) {
          CalendarDayInfo calendarDayInfo = new CalendarDayInfo();
          calendarDayInfo.setDate(StockUtil.transferDateStringToADLocalDate(calendarDayData.path("date").asText()));
          calendarDayInfo.setName(calendarDayData.path("name").asText());
          calendarDayInfo.setHoliday(calendarDayData.path("isHoliday").asText());
          calendarDayInfo.setHolidayCategory(calendarDayData.path("holidayCategory").asText());
          calendarDayInfo.setDescription(calendarDayData.path("description").asText());
          
          resultList.add(calendarDayInfo);
        }
      } else { // 查無資料
        // do nothing, return一個空的resultList
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return resultList;
  }
  
  /**
   * 匯入匯入假日行事曆 into db
   * 
   * @param List<CalendarDayInfo> dataList
   * @return List<String>
   * */
  private List<String> insertHolidayCalendar(List<CalendarDayInfo> dataList) {
    List<String> resultList = new ArrayList<>();
    try {
      Map<String, Object> params = new HashMap<>();
      String tblName = "TBLCALENDAR";
      System.out.println("tblName: " + tblName);
      params.put("TBLNAME", tblName);
      params.put("DATALIST", dataList);

      // insert into db
      int rc = stockRepository.insertHolidayCalendar(params);
      if (rc > 0) {
        resultList.add("insert HolidayCalendar 筆數: " + rc);
      } else {
        resultList.add("insert failed.");
      }
    } catch (Exception e) {
      System.err.println(e.getMessage() + e);
      resultList.add(e.getMessage());
    }

    return resultList;
  }
  
//  private List<DailyInfo> manulInsertData() {
//    List<DailyInfo> resultList = new ArrayList<>();
//    DailyInfo dailyInfo0504 = new DailyInfo("0050", "");
//    dailyInfo0504.setTransactionDate(StockUtil.transferMinguoDateToADLocalDate("109/05/04"));
//    dailyInfo0504.setEndPrice(83);
//    dailyInfo0504.setMaxPrice(83.3);
//    dailyInfo0504.setMinPrice(82.2);
//    
//    DailyInfo dailyInfo0505 = new DailyInfo("0050", "");
//    dailyInfo0505.setTransactionDate(StockUtil.transferMinguoDateToADLocalDate("109/05/05"));
//    dailyInfo0505.setEndPrice(83.4);
//    dailyInfo0505.setMaxPrice(83.8);
//    dailyInfo0505.setMinPrice(83);
//    
//    DailyInfo dailyInfo0506 = new DailyInfo("0050", "");
//    dailyInfo0506.setTransactionDate(StockUtil.transferMinguoDateToADLocalDate("109/05/06"));
//    dailyInfo0506.setEndPrice(83.5);
//    dailyInfo0506.setMaxPrice(83.75);
//    dailyInfo0506.setMinPrice(82.6);
//    
//    DailyInfo dailyInfo0507 = new DailyInfo("0050", "");
//    dailyInfo0507.setTransactionDate(StockUtil.transferMinguoDateToADLocalDate("109/05/07"));
//    dailyInfo0507.setEndPrice(83.85);
//    dailyInfo0507.setMaxPrice(84.15);
//    dailyInfo0507.setMinPrice(83.3);
//    
//    DailyInfo dailyInfo0508 = new DailyInfo("0050", "");
//    dailyInfo0508.setTransactionDate(StockUtil.transferMinguoDateToADLocalDate("109/05/08"));
//    dailyInfo0508.setEndPrice(84.35);
//    dailyInfo0508.setMaxPrice(84.75);
//    dailyInfo0508.setMinPrice(84);
//    
//    DailyInfo dailyInfo0511 = new DailyInfo("0050", "");
//    dailyInfo0511.setTransactionDate(StockUtil.transferMinguoDateToADLocalDate("109/05/11"));
//    dailyInfo0511.setEndPrice(85.05);
//    dailyInfo0511.setMaxPrice(85.4);
//    dailyInfo0511.setMinPrice(84.7);
//    
//    DailyInfo dailyInfo0512 = new DailyInfo("0050", "");
//    dailyInfo0512.setTransactionDate(StockUtil.transferMinguoDateToADLocalDate("109/05/12"));
//    dailyInfo0512.setEndPrice(84.05);
//    dailyInfo0512.setMaxPrice(84.6);
//    dailyInfo0512.setMinPrice(83.65);
//    
//    DailyInfo dailyInfo0513 = new DailyInfo("0050", "");
//    dailyInfo0513.setTransactionDate(StockUtil.transferMinguoDateToADLocalDate("109/05/13"));
//    dailyInfo0513.setEndPrice(84.3);
//    dailyInfo0513.setMaxPrice(84.4);
//    dailyInfo0513.setMinPrice(83.6);
//    
//    DailyInfo dailyInfo0514 = new DailyInfo("0050", "");
//    dailyInfo0514.setTransactionDate(StockUtil.transferMinguoDateToADLocalDate("109/05/14"));
//    dailyInfo0514.setEndPrice(83.3);
//    dailyInfo0514.setMaxPrice(83.85);
//    dailyInfo0514.setMinPrice(83.2);
//    dailyInfo0514.setkValue(56);
//    dailyInfo0514.setdValue(64.56);
//    
//    DailyInfo dailyInfo0515 = new DailyInfo("0050", "");
//    dailyInfo0515.setTransactionDate(StockUtil.transferMinguoDateToADLocalDate("109/05/15"));
//    dailyInfo0515.setEndPrice(83.85);
//    dailyInfo0515.setMaxPrice(84.25);
//    dailyInfo0515.setMinPrice(83.05);
//    dailyInfo0515.setkValue(52.21428571428566);
//    dailyInfo0515.setdValue(60.44476190476189);
//    
//    DailyInfo dailyInfo0518 = new DailyInfo("0050", "");
//    dailyInfo0518.setTransactionDate(StockUtil.transferMinguoDateToADLocalDate("109/05/18"));
//    dailyInfo0518.setEndPrice(83.15);
//    dailyInfo0518.setMaxPrice(83.45);
//    dailyInfo0518.setMinPrice(82.8);
//    
////    resultList.add(dailyInfo0505);
//    resultList.add(dailyInfo0506);
//    resultList.add(dailyInfo0507);
//    resultList.add(dailyInfo0508);
//    resultList.add(dailyInfo0511);
//    resultList.add(dailyInfo0512);
//    resultList.add(dailyInfo0513);
//    resultList.add(dailyInfo0514);
//    resultList.add(dailyInfo0515);
//    resultList.add(dailyInfo0518);
//
//    return resultList;
//  }
  
}
