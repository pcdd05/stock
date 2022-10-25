package dna.rest.pojo;

import java.time.LocalDate;

public class DailyInfo {
  
  public DailyInfo() {
    super();
  }

  public DailyInfo(String stockNo, String stockName) {
    super();
    this.stockNo = stockNo;
    this.stockName = stockName;
  }

  /** 股票代號 */
  private String stockNo;
  
  /** 股票名稱 */
  private String stockName;

  /** 交易日期 */
  private LocalDate transactionDate;
  
  /** 當日開盤價 */
  private double startPrice;
  
  /** 當日最高價 */
  private double maxPrice;
  
  /** 當日最低價 */
  private double minPrice;
  
  /** 當日收盤價 */
  private double endPrice;
  
  /** 當日成交筆數 */
  private int orderNum;
  
  /** 當日rsv值, RSV = 100 * (當日收盤價 - 9日內最低價) / (9日內最高價 - 9日內最低價) */
  private double rsvValue;
  
  /** 當日K值, K = (1/3)RSV + (2/3)前日K */
  private double kValue;
  
  /** 當日V值, D = (1/3)當日K + (2/3)前日D */
  private double dValue;
  
  /** 當日K-D差值 */
  private double kdDiffValue;

  /**
   * @return the stockNo
   */
  public String getStockNo() {
    return stockNo;
  }

  /**
   * @param stockNo the stockNo to set
   */
  public void setStockNo(String stockNo) {
    this.stockNo = stockNo;
  }

  /**
   * @return the stockName
   */
  public String getStockName() {
    return stockName;
  }

  /**
   * @param stockName the stockName to set
   */
  public void setStockName(String stockName) {
    this.stockName = stockName;
  }

  /**
   * @return the transactionDate
   */
  public LocalDate getTransactionDate() {
    return transactionDate;
  }

  /**
   * @param transactionDate the transactionDate to set
   */
  public void setTransactionDate(LocalDate transactionDate) {
    this.transactionDate = transactionDate;
  }

  /**
   * @return the startPrice
   */
  public double getStartPrice() {
    return startPrice;
  }

  /**
   * @param startPrice the startPrice to set
   */
  public void setStartPrice(double startPrice) {
    this.startPrice = startPrice;
  }

  /**
   * @return the maxPrice
   */
  public double getMaxPrice() {
    return maxPrice;
  }

  /**
   * @param maxPrice the maxPrice to set
   */
  public void setMaxPrice(double maxPrice) {
    this.maxPrice = maxPrice;
  }

  /**
   * @return the minPrice
   */
  public double getMinPrice() {
    return minPrice;
  }

  /**
   * @param minPrice the minPrice to set
   */
  public void setMinPrice(double minPrice) {
    this.minPrice = minPrice;
  }

  /**
   * @return the endPrice
   */
  public double getEndPrice() {
    return endPrice;
  }

  /**
   * @param endPrice the endPrice to set
   */
  public void setEndPrice(double endPrice) {
    this.endPrice = endPrice;
  }

  /**
   * @return the orderNum
   */
  public int getOrderNum() {
    return orderNum;
  }

  /**
   * @param orderNum the orderNum to set
   */
  public void setOrderNum(int orderNum) {
    this.orderNum = orderNum;
  }

  /**
   * @return the rsvValue
   */
  public double getRsvValue() {
    return rsvValue;
  }

  /**
   * @param rsvValue the rsvValue to set
   */
  public void setRsvValue(double rsvValue) {
    this.rsvValue = rsvValue;
  }

  /**
   * @return the kValue
   */
  public double getkValue() {
    return kValue;
  }

  /**
   * @param kValue the kValue to set
   */
  public void setkValue(double kValue) {
    this.kValue = kValue;
  }

  /**
   * @return the dValue
   */
  public double getdValue() {
    return dValue;
  }

  /**
   * @param dValue the dValue to set
   */
  public void setdValue(double dValue) {
    this.dValue = dValue;
  }

  /**
   * @return the kdDiffValue
   */
  public double getKdDiffValue() {
    return kdDiffValue;
  }

  /**
   * @param kdDiffValue the kdDiffValue to set
   */
  public void setKdDiffValue(double kdDiffValue) {
    this.kdDiffValue = kdDiffValue;
  }

  
}
