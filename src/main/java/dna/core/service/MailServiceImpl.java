package dna.core.service;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import dna.core.util.StockUtil;
import dna.rest.common.MailParams;
import dna.rest.common.MailUTF8;

public class MailServiceImpl implements MailService {
  
  private static String CHARSET = "UTF-8";
  
  private String machineIP = ""; // 伺服器 IP
  
  private String hostName = ""; // 伺服器 Domain Name
  
  private MailParams initMailParams(ConcurrentHashMap<String, Object> mailMap, int mailType) {
    StringBuffer subjectInfo = new StringBuffer();

    switch (mailType) {
    // 內部信件
    case 0:
      subjectInfo.append("【DnA Stock System】");
      subjectInfo.append(StockUtil.fp_isNull(mailMap.get("TITLE")));
      break;

    default:
      return null;
    }

    MailParams mailParams = new MailParams();
    mailParams.setMachineIP(machineIP);
    mailParams.setHostName(hostName);
    mailParams.setSubject(subjectInfo.toString());

    return mailParams;
  }

  /**
   * Get ServerInfo
   * 
   * 內部信件一律掛上，除非有特殊需求
   * 
   */
  private static String getServerInfo(MailParams params) {
    String serverName = params.getHostName().replace("http://", "").replace("https://", "");

    StringBuffer message = new StringBuffer();
    message.append("伺服器 IP: ").append(params.getMachineIP()).append("<br/>");
    message.append("伺服器 Domain Name: ").append(serverName).append("<br/>");
    message.append("<font color='red'>系統寄送時間: ").append(StockUtil.getToday("yyyy/MM/dd HH:mm:ss")).append("</font><br/>");
    message.append("----------------------------------------------------------------------------------------<br/>");

    return message.toString();
  }
  
  @Override
  public boolean sentMailInfo(ConcurrentHashMap<String, Object> mailMap, int mailType) throws Exception {
    MailParams mailParams = initMailParams(mailMap, mailType);
    if (mailParams == null) {
      System.out.println("MailType= " + mailType +", 發信失敗");
      System.out.println(StockUtil.object2Json(mailMap));
      return false;
    }

    switch (mailType) {
    case 0: // 郵件內容: 寄送Ctrip未定義身分篩選之ID
      mailParams.setSender(StockUtil.fp_isNull(mailMap.get("FROM")));
      mailParams.setRecipient(StockUtil.fp_isNull(mailMap.get("TO")));
      mailParams.setCc(StockUtil.fp_isNull(mailMap.get("CC")));
      mailParams.setMessage(getServerInfo(mailParams) + StockUtil.fp_isNull(mailMap.get("BODY")));
      mailParams.setAttachmentPath((mailMap.get("FILEPATH") != null) ? Arrays.asList(StockUtil.fp_isNull(mailMap.get("FILEPATH")).split(",")) : null);
      break;

    default:
      return false;
    }

    System.out.println("mailType: " + mailType);
    System.out.println("from: " + mailParams.getSender() + ", fromName: " + mailParams.getSenderName());
    System.out.println("to: " +  mailParams.getRecipient() + "Name: " + mailParams.getRecipientName());
    System.out.println("cc: " + mailParams.getCc());
    System.out.println("bcc: " + mailParams.getBcc());
    System.out.println("subject: " + mailParams.getSubject());

    // logger.info("mailParams: {}", StringUtilsHtf.object2Json(mailParams)); // 測試用

    return sendMail(mailParams);
  }
  
  private synchronized boolean sendMail(MailParams params) throws Exception {
    boolean isSend = false;

    MailUTF8 m = new MailUTF8();
    m.setFrom(params.getSender(), params.getSenderName(), CHARSET);

    // 收件者
    if (!"".equals(params.getRecipientName()) && !"".equals(params.getRecipient())) {
      m.setTo(params.getRecipient(), params.getRecipientName());
    } else if (!"".equals(params.getRecipient())) {
      m.setTo(params.getRecipient());
    }

    // 副本
    if (!"".equals(params.getCc())) {
      m.setCc(params.getCc());
    }

    // 密件副本
    if (!"".equals(params.getBcc())) {
      m.setBcc(params.getBcc());
    }

    // 郵件主旨
    m.setSubject(params.getSubject(), CHARSET);

    // 郵件內容 + 附件
    m.setBody(params.getMessage(), params.getAttachmentPath(), CHARSET);

    if (params.getRecipient() == null || params.getRecipient().replace(",", "").trim().isEmpty()) {
      if (params.getCc() == null || params.getCc().replace(",", "").trim().isEmpty()) {
        if (params.getBcc() == null || params.getBcc().replace(",", "").trim().isEmpty()) {
          return false;
        }
      }
    }

    if (params.getSender() == null || params.getSender().replace(",", "").trim().isEmpty()) {
      return false;
    } else {
      isSend = m.mailSend();
    }

    System.out.println("Send mail: " + isSend);

    return isSend;
  }
}
