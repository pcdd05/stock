package dna.rest.common;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import dna.core.util.StockUtil;

/**
 * 這是主要的郵件 class
 * 目前有二種寄法,一是以執行緒的方式,一是普通的寄件方法須等待回應
 */
public class MailUTF8 {

  /** Properties for this class */
  private Properties props = new Properties();
  
  private static final String HOST = "smtp.gmail.com";
  private static final String PORT = "587";
  private static final String USERNAME = "pcdd05java@gmail.com";
  private static final String PASSWORD = "05150303";

  /** Session for this class */
  private Session session  = Session.getInstance(props, new Authenticator() {
    protected PasswordAuthentication getPasswordAuthentication() {
      return new PasswordAuthentication(USERNAME, PASSWORD);
     }
    });

  /** MimeMessage for this class */
  private MimeMessage mMessage = new MimeMessage(session);

  /** 預設之合法E-mail, 避免遭到hotmail退信 */
  public static final String IT_MAIL = "pcdd05java@gmail.com";

  /**
   * 建立 Mail的實體(MailServer目前固定設定為smtp.gmail.com)
   */
  public MailUTF8() {
    props.put("mail.smtp.host", HOST);
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.port", PORT);
    props.put("mail.smtp.connectiontimeout", 20000);
    props.put("mail.smtp.timeout", 40000);
  }

  /**
   * 設定收件者email;若有多位收件者可以用","或是";"隔開
   * 
   * @param to
   *          收件者email
   * @throws MessagingException
   *           例外
   */
  public final void setTo(String to) throws MessagingException {

    setTo(to, to);
  }

  /**
   * 設定收件者email,收件者名稱;若有多位收件者可以用","或是";"隔開
   * 
   * @param to
   *          收件者email
   * @param name
   *          收件者名稱
   * @throws MessagingException
   *           例外
   */
  public final void setTo(String to, String name) throws MessagingException {

    setTo(to, name, "UTF8");
  }

  /**
   * 設定收件者email,收件者名稱,編碼;若有多位收件者可以用","或是";"隔開
   * 
   * @param to
   *          收件者email
   * @param name
   *          收件者名稱
   * @param charset
   *          編碼
   * @throws MessagingException
   *           例外
   */
  public final void setTo(String to, String name, String charset) throws MessagingException {

    String delimiter = ";"; // 分隔符號預設為";"

    try {
      // 將字串中的","替換為";"
      to = to.replace(",", delimiter);
      name = name.replace(",", delimiter);

      String[] toList = StockUtil.split(to, delimiter);
      String[] nameList = StockUtil.split(name, delimiter);
      Address[] toAddress = new Address[toList.length];

      for (int i = 0; i < toList.length; i++) {
        if (nameList.length == 0 || nameList.length != toList.length) {
          toAddress[i] = new InternetAddress(toList[i].replace(" ", ""), toList[i].replace(" ", ""), charset);
        } else {
          toAddress[i] = new InternetAddress(toList[i].replace(" ", ""), nameList[i].replace(" ", ""), charset);
        }
      }

      mMessage.setRecipients(Message.RecipientType.TO, toAddress);
    } catch (Exception e) {
      System.err.println(e.getMessage() + e);
    }
  }

  /**
   * 設定寄件者email
   * 
   * @param from
   *          寄件者email
   * @throws MessagingException
   *           例外
   */
  public final void setFrom(String from) throws MessagingException {

    try {
      setFrom(from, "", "UTF8");
    } catch (Exception e) {
      System.err.println(e.getMessage() + e);
    }
  }

  /**
   * 設定寄件者email,寄件者名稱
   * 
   * @param from
   *          寄件者email
   * @param name
   *          寄件者名稱
   * @throws MessagingException
   *           例外
   */
  public final void setFrom(String from, String name) throws MessagingException {

    try {
      setFrom(from, name, "UTF8");
    } catch (Exception e) {
      System.err.println(e.getMessage() + e);

    }
  }

  /**
   * 設定寄件者email,寄件者名稱,編碼
   * 
   * @param from
   *          寄件者email, 預設為pcdd05@gmail.com
   * @param name
   *          寄件者名稱
   * @param charset
   *          編碼
   * @throws MessagingException
   *           例外
   */
  public final void setFrom(String from, String name, String charset) throws MessagingException {

    try {
      // 此from(寄件者)欄位必須輸入正確合法email信箱，才能避免mail遭到第三方mail server退信(hotmail, yahoo)
      // 鑑於專案內多處仍使用EzTravel為寄件者，故暫且於本處改正，待日後更進一步修改。-Trey Lin
      if ("EzTravel".equals(from)) { // 規則待確認
        from = IT_MAIL;
      }
      Address fromAddress = new InternetAddress(from, name, charset);
      mMessage.setFrom(fromAddress);
    } catch (Exception e) {
      System.err.println(e.getMessage() + e);
    }
  }

  /**
   * 設定副本email,若有多位副本可以用","或是";"隔開
   * 
   * @param cc
   *          副本email
   * @throws MessagingException
   *           例外
   */
  public final void setCc(String cc) throws MessagingException {

    try {
      cc = cc.replace(";", ",").replace(" ", ""); // InternetAddress.parse()以","做為分隔符號
      mMessage.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));
    } catch (Exception e) {
      System.err.println(e.getMessage() + e);
    }
  }

  /**
   * 設定密件副本email,若有多位密件副本可以用","或是";"隔開
   * 
   * @param bcc
   *          密件副本email
   * @throws MessagingException
   *           例外
   */
  public final void setBcc(String bcc) throws MessagingException {

    try {
      bcc = bcc.replace(";", ",").replace(" ", ""); // InternetAddress.parse()以","做為分隔符號
      mMessage.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc));
    } catch (Exception e) {
      System.err.println(e.getMessage() + e);
    }
  }

  /**
   * 設定信件主題
   * 
   * @param subject
   *          信件主題
   * @throws MessagingException
   *           例外
   */
  public final void setSubject(String subject) throws MessagingException {

    try {
      setSubject(subject, "UTF8");
    } catch (Exception e) {
      System.err.println(e.getMessage() + e);
    }
  }

  /**
   * 設定信件主題與編碼
   * 
   * @param subject
   *          信件主題
   * @param charset
   *          編碼
   * @throws MessagingException
   *           例外
   */
  public final void setSubject(String subject, String charset) throws MessagingException {

    try {
      mMessage.setSubject(subject, charset);
    } catch (Exception e) {
      System.err.println(e.getMessage() + e);
    }
  }

  /**
   * 設定信件內容
   * 
   * @param body
   *          信件內容
   * @throws MessagingException
   *           例外
   */
  public final void setBody(String body) throws MessagingException {

    try {
      setBody(body, "", "UTF8", "text/html");
    } catch (Exception e) {
      System.err.println(e.getMessage() + e);
    }
  }

  /**
   * 設定信件內容與附加檔案的路徑
   * 
   * @param body
   *          信件內容
   * @param filename
   *          附加檔案的路徑
   * @throws MessagingException
   *           例外
   */
  public final void setBody(String body, String filename) throws MessagingException {

    try {
      setBody(body, filename, "UTF8", "text/html");
    } catch (Exception e) {
      System.err.println(e.getMessage() + e);
    }
  }

  /**
   * 設定信件內容,附加檔案的路徑與編碼
   * 
   * @param body
   *          信件內容
   * @param filename
   *          附加檔案的路徑
   * @param charset
   *          編碼
   * @throws MessagingException
   *           例外
   */
  public final void setBody(String body, String filename, String charset) throws MessagingException {

    try {
      setBody(body, filename, charset, "text/html");
    } catch (Exception e) {
      System.err.println(e.getMessage() + e);
    }
  }

  /**
   * 設定信件內容,附加檔案的路徑,編碼與信件類型
   * 
   * @param body
   *          信件內容
   * @param filename
   *          附加檔案的路徑
   * @param charset
   *          編碼
   * @param type
   *          信件類型
   * @throws MessagingException
   *           例外
   */
  public final void setBody(String body, String filename, String charset, String type) throws MessagingException {

    try {
      if (filename != null && filename.length() != 0) {
        MimeBodyPart mbp1 = new MimeBodyPart();
        MimeBodyPart mbp2 = new MimeBodyPart();
        mbp1.setContent(body.toString(), type + ";charset=" + charset);

        FileDataSource fds = new FileDataSource(filename);
        mbp2.setDataHandler(new DataHandler(fds));
        mbp2.setFileName(MimeUtility.encodeText(fds.getName(), "UTF-8", null));

        Multipart mp = new MimeMultipart();
        mp.addBodyPart(mbp1);
        mp.addBodyPart(mbp2);
        mMessage.setContent(mp);
      } else {
        mMessage.setContent(body.toString(), "text/html;charset=" + charset);
      }
    } catch (Exception e) {
      System.err.println(e.getMessage() + e);
    }
  }

  /**
   * 以執行緒的方式將信件寄出
   * 
   * @throws SendFailedException
   *           例外
   */
  public final void mailSendThread() throws SendFailedException {

    try {
      mMessage.setSentDate(new Date());
      Transport t = session.getTransport("smtp");
      t.connect(USERNAME, PASSWORD);
      t.sendMessage(mMessage, mMessage.getAllRecipients());
      t.close();
//      Transport.send(mMessage);
    } catch (Exception e) {
      System.err.println(e.getMessage() + e);
    }
  }

  /**
   * 以一般的方式將信件寄出
   * 
   * @throws SendFailedException
   *           例外
   */
  public final boolean mailSend() throws Exception {

    boolean isSend = false;

    mMessage.setSentDate(new Date());
    
    Transport t = session.getTransport("smtp");
    t.connect(USERNAME, PASSWORD);
    t.sendMessage(mMessage, mMessage.getAllRecipients());
    t.close();
//    Transport.send(mMessage);
    isSend = true;

    return isSend;
  }

  /**
   * 執行緒的啟動區
   */
  public final void run() {

    try {

      mMessage.setSentDate(new Date());
      Transport t = session.getTransport("smtp");
      t.connect(USERNAME, PASSWORD);
      t.sendMessage(mMessage, mMessage.getAllRecipients());
      t.close();
//      Transport.send(mMessage);
    } catch (Exception e) {
      System.err.println("Send error:" + e.getMessage() + e);
    }
  }

  /**
   * 設定信件內容與附加檔案的路徑
   * 
   * @param body
   *          信件內容
   * @param filenameList
   *          附加檔案的路徑清單
   * @throws MessagingException
   *           例外
   */
  public final void setBody(String body, List<String> filenameList) throws MessagingException {

    try {
      setBody(body, filenameList, "UTF8", "text/html");
    } catch (Exception e) {
      System.err.println(e.getMessage() + e);
    }
  }

  /**
   * 設定信件內容,附加檔案的路徑與編碼
   * 
   * @param body
   *          信件內容
   * @param filenameList
   *          附加檔案的路徑清單
   * @param charset
   *          編碼
   * @throws MessagingException
   *           例外
   */
  public final void setBody(String body, List<String> filenameList, String charset) throws MessagingException {

    try {
      setBody(body, filenameList, charset, "text/html");
    } catch (Exception e) {
      System.err.println(e.getMessage() + e);
    }
  }

  /**
   * 設定信件內容,附加檔案的路徑,編碼與信件類型
   * 
   * @param body
   *          信件內容
   * @param filenameList
   *          附加檔案的路徑清單
   * @param charset
   *          編碼
   * @param type
   *          信件類型
   * @throws MessagingException
   *           例外
   */
  public final void setBody(String body, List<String> filenameList, String charset, String type) throws MessagingException {

    try {
      if (filenameList != null && filenameList.size() > 0) {
        Multipart mp = new MimeMultipart();
        MimeBodyPart mbp1 = new MimeBodyPart();
        mbp1.setContent(body.toString(), type + ";charset=" + charset);  // 信件內容
        mp.addBodyPart(mbp1);

        for (String filename : filenameList) {  // 附件檔案
          try {
            MimeBodyPart mbp2 = new MimeBodyPart();

            FileDataSource fds = new FileDataSource(filename);
            mbp2.setDataHandler(new DataHandler(fds));
            mbp2.setFileName(MimeUtility.encodeText(fds.getName(), "UTF-8", null));

            mp.addBodyPart(mbp2);
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        }

        mMessage.setContent(mp);  // 信件內容+附件檔案
      } else {
        mMessage.setContent(body.toString(), type + ";charset=" + charset);  // 信件內容
      }
    } catch (Exception e) {
      System.err.println(e.getMessage() + e);
    }
  }

}