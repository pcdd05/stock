package dna.core.service;

import java.util.List;

import dna.rest.pojo.DailyInfo;

public interface DailyService {
  
  public DailyInfo getTodayInfo(String stockNo);
  
  public List<DailyInfo> getMonthlyInfo(String date, String stockNo);
  
  public DailyInfo computeKD(String stockNo,  DailyInfo dailyInfo, boolean isComputeToday);
  
  public void sendTodayInfoMail(DailyInfo todayInfo) throws Exception;
  
}
