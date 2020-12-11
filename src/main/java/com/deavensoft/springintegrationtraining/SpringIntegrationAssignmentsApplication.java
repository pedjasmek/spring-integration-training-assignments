package com.deavensoft.springintegrationtraining;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.annotation.IntegrationComponentScan;

@SpringBootApplication
@IntegrationComponentScan
public class SpringIntegrationAssignmentsApplication {
  public static void main(String[] args) {
    SpringApplication application = new SpringApplication(SpringIntegrationAssignmentsApplication.class);
//    application.setWebApplicationType(WebApplicationType.NONE);
    application.run(args);
  }
}
