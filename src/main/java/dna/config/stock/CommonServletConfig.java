package dna.config.stock;


import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import dna.rest.common.TimeInterval;
import dna.rest.common.TimeIntervalFormatter;

/**
 * The Common ServletConfig Class.
 * 
 * <pre>
 * 所有Rest Server ServletConfig的Super Class, 共用性的設定都
 * 應該放在這裡
 * </pre>
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"dna.rest.common"})
public abstract class CommonServletConfig extends WebMvcConfigurerAdapter {

  /** The packages to scan. */
  String[] packagesToScan;

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter#addFormatters(org
   * .springframework.format.FormatterRegistry)
   */
  @Override
  public void addFormatters(FormatterRegistry registry) {
    TimeIntervalFormatter t = new TimeIntervalFormatter();
    registry.addFormatterForFieldType(TimeInterval.class, t);
    super.addFormatters(registry);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter#
   * configureMessageConverters(java.util.List)
   */
  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> c) {

    // JSON Message Converter
    MappingJackson2HttpMessageConverter json = new MappingJackson2HttpMessageConverter();
    json.setObjectMapper(objectMapper());
    c.add(json);

    // XML JAXB Message Converter
    c.add(new MarshallingHttpMessageConverter(jaxb2Marshaller(), jaxb2Marshaller()));
    
    // String Message Converter, support Content-Type: text/*
    c.add(new StringHttpMessageConverter());

    super.configureMessageConverters(c);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter#
   * configureContentNegotiation
   * (org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer)
   */
  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    configurer.favorPathExtension(true).ignoreAcceptHeader(true).useJaf(false)
        .defaultContentType(MediaType.APPLICATION_JSON)
        // .mediaType("xml", MediaType.APPLICATION_XML)
        .mediaType("json", MediaType.APPLICATION_JSON);

    super.configureContentNegotiation(configurer);
  }

  /**
   * Object mapper.
   * 
   * @return the object mapper
   */
  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.setSerializationInclusion(Include.NON_NULL);
    mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"));
    return mapper;
  }

  /**
   * Jaxb2 marshaller.
   * 
   * @return the jaxb2 marshaller
   */
  @Bean
  public Jaxb2Marshaller jaxb2Marshaller() {
    addPackagesToScan();
    this.packagesToScan =
        (String[]) ArrayUtils.addAll(this.packagesToScan, new String[] {
            "dna.rest.common", "dna.rest.pojo"});
    Jaxb2Marshaller m = new Jaxb2Marshaller();
    m.setPackagesToScan(this.packagesToScan);
    return m;
  }

  /**
   * Override this method to add custom packages to scan.
   * 
   * @param packagesToScan the packages to scan
   */
  public void addPackagesToScan(String... packagesToScan) {
    this.packagesToScan = packagesToScan;
  }

}
