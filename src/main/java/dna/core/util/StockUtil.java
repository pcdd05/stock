package dna.core.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.MinguoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DecimalStyle;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;

public class StockUtil {
  
  private final static ObjectMapper singletonObjectMapper = new ObjectMapper();
  
  /**
   * Transfer minguo date String to AD LocalDate.
   * 民國年 yyy/MM/dd 轉 西元年 yyyy-MM-dd
   *
   * @param dateString the String dateString
   * @return the LocalDate
   */
  public static LocalDate transferMinguoDateToADLocalDate(String dateString) {
    Chronology minguoChrono = MinguoChronology.INSTANCE;
    DateTimeFormatter mingouDf = new DateTimeFormatterBuilder().parseLenient()
        .appendPattern("yyy/MM/dd")
        .toFormatter()
        .withChronology(minguoChrono)
        .withDecimalStyle(DecimalStyle.of(Locale.getDefault()));
    
    return LocalDate.parse(dateString.trim(), mingouDf);
  }
  
  /**
   * Transfer date String to AD LocalDate.
   * 字串年 yyyy/MM/dd 轉 LocalDate年 yyyy/MM/dd
   *
   * @param dateString the String dateString
   * @return the LocalDate
   */
  public static LocalDate transferDateStringToADLocalDate(String date) {
    String[] dateArray = date.split("/");
    StringBuilder sb =new StringBuilder();
    for (String dateString : dateArray) {
      if (dateString.length() == 1) {
        sb.append("0");
      }
      sb.append(dateString);
    }
    
    DateTimeFormatter formatter  = DateTimeFormatter.ofPattern("yyyyMMdd");
    return LocalDate.parse(sb.toString().trim(), formatter );
  }
  
  /**
   * 函式名稱：getToday(format); <br/> 
   * 功　　能：抓取今天的日期  <br/>
   * 傳　　入：欲輸出的格式  <br/>
   * 傳　　回：日期. <br/>
   * 
   * @param dFormat the d format
   * @return the today
   */
  public static String getToday(String dFormat) {
    if (dFormat.length() == 0 || dFormat == null) {
      dFormat = "yyyy/MM/dd HH:mm:ss";
    }
    SimpleDateFormat formatter = new SimpleDateFormat(dFormat);
    java.util.Date currentTime_1 = new java.util.Date();
    return formatter.format(currentTime_1);
  }
  
  /**
   * 將物件 null 或 空白 轉成 空字串.
   * 
   * @param s1 the s1
   * @return 空字串或原字串
   */
  public static String fp_isNull(Object s1) {
    int count = 0;
    String ss = "";
    if (s1 == null)
      return ss;
    else if (s1.equals(""))
      return ss;
    else if (s1.equals("null")) return ss;

    // 判斷字串是否全部都是空白
    String s = s1.toString();
    for (int i = 0; i < s.length(); i++) {
      if (s.charAt(i) == ' ') count++;
    }
    // 若全部為空白傳回一空字串
    if (count == s.length())
      return ss;
    else
      return s.trim();
  }
  
  /**
   * 將字串物件以某符號來切割成陣列.
   * 
   * @param s the s
   * @param v the v
   * @return 字串陣列
   */
  public static String[] split(String s, String v) {
    StringTokenizer parser = new StringTokenizer(s, v);
    String[] str = new String[parser.countTokens()];
    try {
      int i = 0;
      while (parser.hasMoreElements()) {
        str[i++] = (String) parser.nextToken();
      }
    } catch (NoSuchElementException e) {
      return null;
    }
    return str;
  }
  
  /**
   * Object to Json String
   * 
   * @param object
   * @return String
   */
  public static String object2Json(Object object) {
    ObjectWriter ow = singletonObjectMapper.writer().withDefaultPrettyPrinter();
    String json = "NO JSON";
    try {
      json = ow.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      json = e.getMessage();
    }

    return json;
  }
  
  public static ObjectMapper getMapper() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    ObjectMapper mapper = new ObjectMapper().setDateFormat(sdf);
    mapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    mapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, Boolean.FALSE);
    mapper.setSerializationInclusion(Include.NON_NULL);

    return mapper;
  }
  
}
  
