package dna.config.stock;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import dna.config.AppProperties;
import dna.core.service.DailyService;
import dna.core.service.DailyServiceImpl;
import dna.core.service.DataService;
import dna.core.service.DataServiceImpl;
import dna.core.service.MailService;
import dna.core.service.MailServiceImpl;
import dna.core.service.RestService;

/**
 * <pre>
 * RootConfig,
 * </pre>
 * 
 * .
 * 
 */
@Configuration
@Import({AppProperties.class})
@EnableTransactionManagement
@MapperScan(basePackages = {"dna.persistence.repository.stock"}, sqlSessionFactoryRef = "sqlSessionFactory")
@ComponentScan(basePackages = { "dna.core.schedule" })
@EnableScheduling
public class RootConfig extends CommonRootConfig {

//  /**
//   * Async Task executor.<br>
//   * 處理非同步作業<br>
//   * _列表頁搜尋searchProd<br>
//   * _cache mget<br>
//   * 
//   * @param corePoolSize
//   *          the core pool size
//   * @param maxPoolSize
//   *          the max pool size
//   * @return the thread pool task executor
//   */
//  @Bean(name = "asyncExecutor")
//  public ExecutorService aysncExecutor(@Value("${coupon.threadPoolSize.async}") int poolSize) {
//    logger.info("Async Pool size set: corePoolSize(" + poolSize + ")");
//
//    ExecutorService asyncExecutor = Executors.newFixedThreadPool(poolSize);
//
//    return asyncExecutor;
//  }
  
  /**
   * Rest Service.
   * 
   * @return the rest service
   */
  @Bean
  public RestService restService() {
    return new RestService(1000,10000,30000);
  }
  
  /**
   * Daily Service.
   * 
   * @return the Daily service
   */
  @Bean
  public DailyService dailyService() {
    return new DailyServiceImpl();
  }
  
  /**
   * Data Service.
   * 
   * @return the Data service
   */
  @Bean
  public DataService dataService() {
    return new DataServiceImpl();
  }
  
//  /**
//   * redis cache service.
//   * 
//   * @return the redis cache service
//   * @throws Exception
//   *           the exception
//   */
//  @Bean
//  public CacheService cacheService(@Value("${cache.ip}") String address, @Value("${cache.port}") String port, @Value("${cache.pwd}") String pwd, @Value("${cache.db}") String dbIndex, @Value("${cache.timeout}") String timeout, @Value("${app.env}") String env) throws Exception {
//    return new CacheServiceImpl(address, port, pwd, dbIndex, timeout, env);
//  }
  
  /**
   * MailService.
   * 
   * @return MailServiceImpl
   * @throws Exception
   *           the exception
   */
  @Bean
  public MailService mailService() throws Exception {
    return new MailServiceImpl();
  }
  
//  /**
//   * Mail sender.
//   * 
//   * @return the java mail sender
//   */
//  @Bean
//  public JavaMailSender mailSender() {
//    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//    mailSender.setHost("smtp.gmail.com");
//    mailSender.setPort(587);
//    mailSender.setUsername("pcdd05@gmail.com");
//    mailSender.setPassword("05150103");
//    return mailSender;
//  }

  /**
   * Add a comment to this line Rest template.
   * 
   * @return the rest template
   */
  @Bean
  public RestTemplate restTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
    messageConverters.add(new MappingJackson2HttpMessageConverter());
    restTemplate.setMessageConverters(messageConverters);
    return restTemplate;
  }

//
//  /**
//   * Add a comment to this line Rest template.
//   * 
//   * @return the rest template
//   */
//  @Bean
//  public RestTemplate utRestTemplate() {
//    RestTemplate restTemplate = new RestTemplate();
//    return restTemplate;
//  }
  
}
