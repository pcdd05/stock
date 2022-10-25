package dna.config.stock;


import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;

/**
 * The Class CommonRootConfig.
 * 
 * <pre>
 * 
 * 所有Rest Server RootConfig的Super Class, 共用性的設定都應該放在這裡
 * </pre>
 */
public abstract class CommonRootConfig {

  /**
   * Oracle data source.
   * 
   * @return the data source
   * @throws Exception the exception
   */
  @Bean
  protected DataSource oracleDataSource() throws Exception {
    Context ctx = new InitialContext();
    DataSource ds = (DataSource) ctx.lookup("stockdb");
    ctx.close();
    return ds;
  }

  /**
   * Sql session factory.
   * 
   * @return the sql session factory
   * @throws Exception the exception
   */
  @Bean
  public SqlSessionFactory sqlSessionFactory() throws Exception {
    SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
    factoryBean.setDataSource(oracleDataSource());
    return factoryBean.getObject();
  }

}
