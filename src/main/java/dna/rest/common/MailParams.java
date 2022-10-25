package dna.rest.common;

import java.util.List;

public class MailParams {

  /** The machineIP 伺服器 IP. */
  private String machineIP;

  /** The hostName 伺服器 Domain Name(ex.3w). */
  private String hostName;

  /** The sender 寄件人. */
  private String sender;

  /** The senderMail 寄件人名稱. */
  private String senderName;

  /** The recipient 收件人. */
  private String recipient;

  /** The recipientMail 收件人名稱. */
  private String recipientName;

  /** The cc 副本. */
  private String cc;

  /** The bcc 密件副本. */
  private String bcc;

  /** The subject 郵件主旨. */
  private String subject;

  /** The message 郵件內容. */
  private String message;

  /** The attachmentPath 郵件附件(路徑). */
  private List<String> attachmentPath;

  /** The mailType 郵件分類. */
  private Integer mailType;
  
  public MailParams() {
    machineIP = "";
    hostName = "";
    sender = "pcdd05@gmail.com.tw";
    senderName = "DnA Stock System（此信件由系統自動發送，請勿直接回信）";
    recipient = "";
    recipientName = "";
    cc = "";
    bcc = "";
    subject = "";
    message = "";
    attachmentPath = null;
  }

  /**
   * @return the machineIP
   */
  public String getMachineIP() {
    return machineIP;
  }

  /**
   * @param machineIP
   *          the machineIP to set
   */
  public void setMachineIP(String machineIP) {
    this.machineIP = machineIP;
  }

  /**
   * @return the hostName
   */
  public String getHostName() {
    return hostName;
  }

  /**
   * @param hostName
   *          the hostName to set
   */
  public void setHostName(String hostName) {
    this.hostName = hostName;
  }

  /**
   * @return the sender
   */
  public String getSender() {
    return sender;
  }

  /**
   * @param sender
   *          the sender to set
   */
  public void setSender(String sender) {
    this.sender = sender;
  }

  /**
   * @return the sender name
   */
  public String getSenderName() {
    return senderName;
  }

  /**
   * @param senderName
   *          the sender name to set
   */
  public void setSenderName(String senderName) {
    this.senderName = senderName;
  }

  /**
   * @return the recipient
   */
  public String getRecipient() {
    return recipient;
  }

  /**
   * @param recipient
   *          the recipient to set
   */
  public void setRecipient(String recipient) {
    this.recipient = recipient;
  }

  /**
   * @return the recipient name
   */
  public String getRecipientName() {
    return recipientName;
  }

  /**
   * @param recipientName
   *          the recipient name to set
   */
  public void setRecipientName(String recipientName) {
    this.recipientName = recipientName;
  }

  /**
   * @return the cc
   */
  public String getCc() {
    return cc;
  }

  /**
   * @param cc
   *          the cc to set
   */
  public void setCc(String cc) {
    this.cc = cc;
  }

  /**
   * @return the bcc
   */
  public String getBcc() {
    return bcc;
  }

  /**
   * @param bcc
   *          the bcc to set
   */
  public void setBcc(String bcc) {
    this.bcc = bcc;
  }

  /**
   * @return the subject
   */
  public String getSubject() {
    return subject;
  }

  /**
   * @param subject
   *          the subject to set
   */
  public void setSubject(String subject) {
    this.subject = subject;
  }

  /**
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  /**
   * @param message
   *          the message to set
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * @return the attachmentPath
   */
  public List<String> getAttachmentPath() {
    return attachmentPath;
  }

  /**
   * @param attachmentPath
   *          the attachmentPath to set
   */
  public void setAttachmentPath(List<String> attachmentPath) {
    this.attachmentPath = attachmentPath;
  }

  /**
   * @return the mailType
   */
  public Integer getMailType() {
    return mailType;
  }

  /**
   * @param mailType the mailType to set
   */
  public void setMailType(Integer mailType) {
    this.mailType = mailType;
  }
  
}
