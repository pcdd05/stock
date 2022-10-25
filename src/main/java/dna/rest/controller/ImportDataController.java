package dna.rest.controller;

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
import dna.rest.common.RestException;
import dna.rest.common.RestResource;
import dna.rest.common.RestResourceFactory;

@Controller
@Import({AppProperties.class})
@RequestMapping(value = "/rest/v1")
public class ImportDataController {
  
  /** The env. */
  @Value("${app.env}")
  private String env;
  
  /** The daily service. */
  @Autowired
  private DailyService dailyService;
  /** The data service. */
  @Autowired
  private DataService dataService;
  
  /**
   * 匯入自2010.01.01以來的訂單資料
   * 
   * @param stockNo the stockNo
   * 
   * @return the DailyInfo
   */
  @RequestMapping(value = "/initImport", method = RequestMethod.GET)
  public ResponseEntity<RestResource<String>> initImport(@RequestParam(required = true) String stockNo) {
    long s = System.currentTimeMillis();

    System.out.println("/rest/v1/initImport start");

    RestResource<String> body = RestResourceFactory.newInstance();
    try {
      System.out.println("importAllData start");
      // 匯入自2010.01起至今單一個股所有股價
      List<String> resultList = dataService.importAllData(stockNo);
      System.out.println("manulInsert20100113KDValue start");
      // 匯入2010.01.13之歷史KD值, 以便往後計算      
      resultList.addAll(dataService.manulInsert20100113KDValue(stockNo));
      // 逐一計算每日KD值並存入
      System.out.println("updateKDValue start");
      resultList.addAll(dataService.updateKDValue(stockNo));
      
      body.setItems(resultList);
    } catch (Exception e) {
      System.err.println(e.getMessage() + e);
      throw new RestException(e.getMessage(), body);
    } finally {
      System.out.println("/rest/v1/initImport complete, " + (System.currentTimeMillis() - s) + "ms spent.");
    }
    return new ResponseEntity<RestResource<String>>(body, HttpStatus.OK);
  }
  
  /**
   * 匯入假日行事曆
   * 
   * @param stockNo the stockNo
   * 
   * @return the DailyInfo
   */
  @RequestMapping(value = "/importHolidayCalendar", method = RequestMethod.GET)
  public ResponseEntity<RestResource<String>> importHolidayCalendar(@RequestParam(required = true) boolean isInit) {
    long s = System.currentTimeMillis();
    
    System.out.println("/rest/v1/importHolidayCalendar start");

    RestResource<String> body = RestResourceFactory.newInstance();
    try {
      System.out.println("importHolidayCalendar start");
      // 匯入假日行事曆
      List<String> resultList = dataService.importHolidayCalendar(isInit);
      
      body.setItems(resultList);
    } catch (Exception e) {
      System.err.println(e.getMessage() + e);
      throw new RestException(e.getMessage(), body);
    } finally {
      System.out.println("/rest/v1/importHolidayCalendar complete, " + (System.currentTimeMillis() - s) + "ms spent.");
    }
    return new ResponseEntity<RestResource<String>>(body, HttpStatus.OK);
  }
  
}
