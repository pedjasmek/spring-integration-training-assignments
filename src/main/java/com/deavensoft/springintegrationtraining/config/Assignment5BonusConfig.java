package com.deavensoft.springintegrationtraining.config;

import com.deavensoft.springintegrationtraining.domain.OrderItem;
import java.io.File;
import java.io.IOException;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.mail.dsl.Mail;

/**
 * Bonus Assignment - sending Messages to Email and appending Messages to a file
 *
 * 1. Send all OrderItems from the "apparelChannel" to email address apparel@shop.com
 *
 * 2. Send all OrderItems from the "electronicsChannel" to email address electronics@shop.com
 *
 * 3. The OrderItems from the "uncategorizedChannel" log to a file in the temp folder
 *
 * 4. Start mock local SMTP server. Run Assignment5Test.
 *    - It will just start the spring context in order to process files and send emails.
 *    - There should be 2 emails sent
 *    - Inspect the "uncategorized" folder in the local java temp folder. It should have
 *      one file, containing OrderItems with categorie FOOD
 *
 */
@Configuration
@Slf4j
public class Assignment5BonusConfig {

  private static final File TEMP_FOLDER = new File(System.getProperty("java.io.tmpdir"), "uncategorized");

  @PostConstruct
  public void deleteFolder() throws IOException {
    FileUtils.cleanDirectory(TEMP_FOLDER);
  }

//  @Bean
//  public IntegrationFlow apparelSendMailFlow() {
//    return null; // TODO - Use IntegrationFlows
//  }
//
//  @Bean
//  public IntegrationFlow electronicsSendMailFlow() {
//    return null; // TODO - Use IntegrationFlows
//  }
//
//  @Bean
//  public IntegrationFlow uncategorizedLogging() {
//    return null; // TODO - Use IntegrationFlows
//  }
}
