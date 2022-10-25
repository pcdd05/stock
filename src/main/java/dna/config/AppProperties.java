package dna.config;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * <pre>
 * AppProperties, TODO: add Class Javadoc here.
 * </pre>
 * 
 */
@Configuration
public class AppProperties {

  /**
   * Property placeholder configurer.
   * 
   * @return the property placeholder configurer
   */
  @Bean
  public PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
    PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
    String location = System.getenv("STOCK_CONF");
    Resource[] resource = new Resource[1];

    // 若未設定環境變數也不直接讀取專案內的，避免讀取到錯誤的設定檔
//    if (location == null) {
//      location = "config.properties";
//      resource[0] = new ClassPathResource(location);
//      logger.info("Use config file: classpath:" + location);
//    } else {
      resource[0] = new FileSystemResource(location + "/jboss/config.properties");
      System.out.println("Use config file: " + location + "/jboss/config.properties");
//    }
    configurer.setLocations(resource);
    return configurer;
  }
}
