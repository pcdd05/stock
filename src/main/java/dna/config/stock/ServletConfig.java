package dna.config.stock;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;

/**
 * <pre>
 * ServletConfig,
 * </pre>
 * 
 * .
 */
@Configuration
@ComponentScan(basePackages = {"dna.rest.controller"})
public class ServletConfig extends CommonServletConfig {

  /*
   * (non-Javadoc)
   * 
   * @see eztravel.config.server.CommonServletConfig#addFormatters(org.springframework
   * .format.FormatterRegistry)
   */
  @Override
  public void addFormatters(FormatterRegistry registry) {
    super.addFormatters(registry);
  }

  /*
   * (non-Javadoc)
   * 
   * @see eztravel.config.server.CommonServletConfig#addPackagesToScan(java.lang .String[])
   */
  @Override
  public void addPackagesToScan(String... packagesToScan) {
    super.addPackagesToScan(new String[] {"dna.rest.pojo", "dna.rest.common"});
  }

}
