package dna.core.service;

import java.util.List;

import dna.rest.pojo.DailyInfo;

public interface DataService {
  
  public List<String> importAllData(String stockNo) throws InterruptedException;
  
  public List<String> insertDailyInfoList(String stockNo, List<DailyInfo> dataList);
  
  public List<String> updateDailyInfoKD(String stockNo, DailyInfo dailyInfo);
  
  public List<String> manulInsert20100113KDValue(String stockNo);
  
  public List<String> updateKDValue(String stockNo) throws Exception;
  
  public List<String> importHolidayCalendar(boolean isInit) throws InterruptedException;
  
}
