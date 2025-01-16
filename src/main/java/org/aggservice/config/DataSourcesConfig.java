package org.aggservice.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class DataSourcesConfig {

  private List<DataSourceProperties> dataSources = new ArrayList<>();

  @Getter
  @Setter
  public static class DataSourceProperties {
    private String name;
    private String strategy;
    private String url;
    private String table;
    private String user;
    private String password;
    private Map<String, String> mapping = new HashMap<>();
  }

  @PostConstruct
  public void logConfig() {
    if (dataSources.isEmpty()) {
      System.err.println("DataSources are empty. Check configuration.");
    } else {
      dataSources.forEach(ds -> System.out.println("Loaded DataSource: " + ds.getName()));
    }
  }

}
