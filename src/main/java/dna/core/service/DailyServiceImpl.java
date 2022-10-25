package dna.core.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;

import dna.core.util.StockUtil;
import dna.persistence.repository.stock.StockRepository;
import dna.rest.pojo.DailyInfo;

public class DailyServiceImpl implements DailyService {
  
  @Autowired
  private RestService restService;
  
  @Autowired
  private MailService mailService;
  
  /** The stock repository. */
  @Autowired
  private StockRepository stockRepository;
  
  private final static String QUERYSTOCKURL = "https://www.twse.com.tw/exchangeReport/STOCK_DAY?";
  
  /** 
   * 取得單一個股今日股價資料
   * 
   * @param String stockNo
   * */
  @Override
  public DailyInfo getTodayInfo(String stockNo) {
    String todayDate = StockUtil.getToday("yyyyMMdd");
    List<DailyInfo> monthlyInfo = getMonthlyInfo(todayDate, stockNo);
    if (monthlyInfo != null && !monthlyInfo.isEmpty()) {
      // 過濾當月資料中有交易日 = 今日的資料
      Optional<DailyInfo> todayInfo = monthlyInfo.stream().filter(item -> LocalDate.now().equals(item.getTransactionDate())).findAny();
      if (todayInfo.isPresent()) {  // 確認今日是否有交易資料
        return todayInfo.get();
      }
    }
    return null;
  }
  
  /** 
   * 使用twse.com.tw網站資料取得單一個股特定日期當月份所有股價資料 
   * 
   * @param String queryDate
   * @param String queryStockNo
   * @return List<DailyInfo>
   * */
  @Override
  public List<DailyInfo> getMonthlyInfo(String queryDate, String queryStockNo) {
    String url = QUERYSTOCKURL + "response=json&date=" + queryDate + "&stockNo=" + queryStockNo;
    String resJson = null;
    int retry = 0;
    
    while(retry < 4) {
      try {
        resJson = restService.getJson(url);
        if (StringUtils.isNotBlank(resJson)) {
          JsonNode response = StockUtil.getMapper().readValue(resJson, new TypeReference<JsonNode>() {});
          if (response != null) {
            return parseDailyInfoForMonthResponse(response);      
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
   * 解析當月股價資料並以單日DailyInfo存入List 
   * 
   * @param JsonNode response
   * @return List<DailyInfo>
   * */
  private List<DailyInfo> parseDailyInfoForMonthResponse(JsonNode response) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
    List<DailyInfo> resultList = new ArrayList<>();
    String stockNo = null;
    String stockName = null;
    try {
      if ("OK".equals(response.path("stat").asText())) { // 回傳有資料時
        // stockNo, stockName
        String title = response.path("title").asText();

        if (StringUtils.isNotBlank(title)) {
          String[] titleArray = title.split(" ");
          if (titleArray != null && titleArray.length > 0) {
            stockNo = titleArray[1];
            stockName = titleArray[2];
          }
        }
        
        // other fields
        JsonNode monthDataList = response.path("data");
        if (!monthDataList.isNull()) {
          ObjectMapper mapper = StockUtil.getMapper();
          for (JsonNode dailyData : monthDataList) {
            DailyInfo dailyInfo = new DailyInfo(stockNo, stockName);
            List<String> dailyDataList = mapper.readValue(mapper.writeValueAsString(dailyData), new TypeReference<List<String>>() {});
            dailyInfo.setTransactionDate(StockUtil.transferMinguoDateToADLocalDate(dailyDataList.get(0)));
            dailyInfo.setStartPrice(Double.valueOf(dailyDataList.get(3)));
            dailyInfo.setMaxPrice(Double.valueOf(dailyDataList.get(4)));
            dailyInfo.setMinPrice(Double.valueOf(dailyDataList.get(5)));
            dailyInfo.setEndPrice(Double.valueOf(dailyDataList.get(6)));
            dailyInfo.setOrderNum(Integer.valueOf(dailyDataList.get(8).replaceAll(",", "")));
            
            resultList.add(dailyInfo);
          }
        }
      } else { // stat非OK, 查無資料
        // do nothing, return一個空的resultList
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return resultList;
  }
  
  /**
   * 計算KD值 <br/>
   * KD公式 :  K = (1/3)RSV + (2/3)前日K ; D = (1/3)當日K + (2/3)前日D <br/>
   * RSV公式: RSV = 100 * (當日收盤價 - 9日內最低價) / (9日內最高價 - 9日內最低價) <br/>
   * K-D值 : K-D
   * 
   * @param String stockNo
   * @param DailyInfo dailyInfo
   * @param boolean isComputeToday
   * @return DailyInfo
   * */
  @Override
  public DailyInfo computeKD(String stockNo, DailyInfo dailyInfo, boolean isComputeToday) {
      System.out.println("transactionDate: " + dailyInfo.getTransactionDate());
      // 取得9日內資料
      List<DailyInfo> nineDaysInfoList = new ArrayList<>();
      String tblName = "TBL".concat(stockNo);
      if (isComputeToday) { // 依是否計算今日KD值/或計算特定日期KD值, 使用不同SQL
        nineDaysInfoList = stockRepository.get9daysInfosFromToday(tblName);
      } else {
        Map<String, Object> params = new HashMap<>();
        params.put("TBLNAME", tblName);
        params.put("TRANSACTIONDATE", dailyInfo.getTransactionDate().toString());
        nineDaysInfoList = stockRepository.get9daysInfosFromDate(params);
      }
      // 按日期排序, 由近排到遠 ex:06/15, 06/14, 06/13...
      Collections.sort(nineDaysInfoList, Comparator.comparing(DailyInfo::getTransactionDate, (t1, t2) -> t2.compareTo(t1)));
      
      // 計算RSV, K, D
      List<Double> allPriceList = new ArrayList<>();
      allPriceList.addAll(nineDaysInfoList.stream().map(DailyInfo::getMinPrice).collect(Collectors.toList()));
      allPriceList.addAll(nineDaysInfoList.stream().map(DailyInfo::getMaxPrice).collect(Collectors.toList()));
      if (dailyInfo.getTransactionDate().equals(nineDaysInfoList.get(0).getTransactionDate())) { // 檢核確定為有交易日的資料
        // RSV = 100 * (當日收盤價 - 9日內最低價) / (9日內最高價 - 9日內最低價)
        double rsvValue = 100 * (nineDaysInfoList.get(0).getEndPrice() - Collections.min(allPriceList)) / (Collections.max(allPriceList) - Collections.min(allPriceList));
        // K = (1/3)RSV + (2/3)前日K
        double kValue = rsvValue * 1 / 3 + nineDaysInfoList.get(1).getkValue() * 2 / 3;
        // D = (1/3)當日K + (2/3)前日D
        double dValue = kValue * 1 / 3 + nineDaysInfoList.get(1).getdValue() * 2 / 3;
        
        System.out.println("rsvValue: " + rsvValue + ", kValue: " + kValue + ", dValue: " + dValue);
        
        dailyInfo.setRsvValue(rsvValue);
        dailyInfo.setkValue(kValue);
        dailyInfo.setdValue(dValue);
        dailyInfo.setKdDiffValue(kValue - dValue);
        
        // 寄信
//        try {
//          sendTodayInfoMail(dailyInfoList.get(0));
//        } catch (Exception e) {
//          e.printStackTrace();
//        }
      } else {
        System.out.println("9 days data don't include today's, something wrong.");
      }
    
    return dailyInfo;
  }
  
  /** send mail about today's stock Info */
  @Override
  public void sendTodayInfoMail(DailyInfo todayInfo) throws Exception {
    String dateStr = StockUtil.getToday("yyyyMMdd");
    boolean sentResult = false;

    ConcurrentHashMap<String, Object> mailMap = new ConcurrentHashMap<String, Object>();
    mailMap.put("TITLE", dateStr + "通知今日「" + todayInfo.getStockNo() + "-" + todayInfo.getStockName() + "」股價資訊");
    String title = dateStr + "通知今日「" + todayInfo.getStockNo() + "-" + todayInfo.getStockName() + "」股價資訊";
    mailMap.put("FROM", "pcdd05java@gmail.com");
    mailMap.put("TO", "pcdd05@gmail.com");
    mailMap.put("CC", "pcdd05java@gmail.com");
    mailMap.put("MAIL_TYPE", 0);

    StringBuffer message = new StringBuffer();
    message
    .append("<font color='blue'>").append("股票代號名稱：")
    .append(todayInfo.getStockNo()).append("-").append(todayInfo.getStockName()).append("</font><br>");
    message
    .append("今日收盤價：")
    .append("<font color='red'>").append(todayInfo.getEndPrice()).append("元").append("</font>")
    .append("　/ 最高：").append(todayInfo.getMaxPrice()).append("元").append("　/ 最低：").append(todayInfo.getMinPrice()).append("元").append("<br>");
    message
    .append("今日KD值：").append("<br>")
    .append("K值： ").append(todayInfo.getkValue()).append(" , D值： ").append(todayInfo.getdValue()).append("<br>");
    
    mailMap.put("BODY", message);
    try {
      JMail.Send_mail("pcdd05@gmail.com", title, message.toString());
//      sentResult = mailService.sentMailInfo(mailMap, 0);

      // if sentResult is true, remove the CtripUndefApplicativeIdTable from excel.
      if (sentResult) {
        try {
          System.out.println("mail sent");
        } catch (Exception e1) {
          System.err.println("Exception INFO e1 :" + e1);
          e1.printStackTrace();
        }
      }
    } catch (Exception e) {
      System.err.println("Exception INFO e :" + e);
      e.printStackTrace();
    }
  }
  



}
