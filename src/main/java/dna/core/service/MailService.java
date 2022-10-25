package dna.core.service;

import java.util.concurrent.ConcurrentHashMap;

public interface MailService {
  
  public boolean sentMailInfo(ConcurrentHashMap<String, Object> mailMap, int mailType) throws Exception;
  
}
