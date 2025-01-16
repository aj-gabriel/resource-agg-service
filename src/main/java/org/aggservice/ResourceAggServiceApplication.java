package org.aggservice;

import org.aggservice.config.DataSourcesConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})//disable spring datasource autoconfig
@EnableConfigurationProperties({DataSourcesConfig.class})
public class ResourceAggServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(ResourceAggServiceApplication.class, args);
  }

}

