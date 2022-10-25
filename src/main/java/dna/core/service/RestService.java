package dna.core.service;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.http.MediaType;


public class RestService {
  
  private HttpClient httpClient;
  
  public RestService(int conn, int connect_timeout, int readtimeout) {

    RequestConfig requestConfig = RequestConfig.custom()
        .setConnectTimeout(connect_timeout)
        .setSocketTimeout(readtimeout)
        .setConnectionRequestTimeout(20000)
        .build();

    httpClient = HttpClientBuilder.create()
        .setDefaultRequestConfig(requestConfig)
        .setMaxConnTotal(conn)
        .setMaxConnPerRoute(conn)
        .build();
  }
  
  public String getJson(String url) {
    String result = null;
    HttpResponse resp = null;
    HttpGet get = new HttpGet(url);
    try {
      System.out.println("url:" + url);
      get.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
      get.setHeader("Accept", MediaType.APPLICATION_JSON_VALUE);
      get.setHeader("Connection", "close");
      resp = httpClient.execute(get);
      result = EntityUtils.toString(resp.getEntity());
    } catch (Exception e) {
      System.err.println("getJson error : " + e);
      e.printStackTrace();
    } finally {
      get.abort();
      get.releaseConnection();
      if (resp != null) {
        try {
          EntityUtils.consume(resp.getEntity());
        } catch (IOException e) {
          System.err.println("關閉response失敗" + e);
        }
      }
    }
    return result;
  }

}
