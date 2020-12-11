package com.deavensoft.springintegrationtraining.configsolutions;

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
//@Configuration // commented-out, so the config is not picked up by the Spring Boot
@Slf4j
public class Assignment5BonusConfig {

  private static final File TEMP_FOLDER = new File(System.getProperty("java.io.tmpdir"), "uncategorized");

  @PostConstruct
  public void deleteFolder() throws IOException {
    FileUtils.cleanDirectory(TEMP_FOLDER);
  }

  @Bean
  public IntegrationFlow apparelSendMailFlow() {
    return IntegrationFlows.from("apparelChannel")
        .enrichHeaders(Mail.headers()
            .subject("New Apparel Order")
            .from("robot@shop.com")
            .toFunction(m -> new String[] { "apparel@shop.com" }))
        .transform(OrderItem::toString)
        .handle(Mail.outboundAdapter("localhost")
            .port(25)
            .protocol("smtp"),
            e -> e.id("apparelSendMailEndpoint"))
        .get();
  }

  @Bean
  public IntegrationFlow electronicsSendMailFlow() {
    return IntegrationFlows.from("electronicsChannel")
        .enrichHeaders(Mail.headers()
            .subjectFunction(m -> "New Electronics Order: " + ((OrderItem)m.getPayload()).getProductName())
            .from("robot@shop.com")
            .toFunction(m -> new String[] { "electronics@shop.com" }))
        .transform(OrderItem::toString)
        .handle(Mail.outboundAdapter("localhost")
            .port(25)
            .protocol("smtp"),
            e -> e.id("electronicsSendMailEndpoint"))
        .get();
  }

  @Bean
  public IntegrationFlow uncategorizedLogging() {
    log.info("Temp folder: {}", TEMP_FOLDER);
    return IntegrationFlows.from("uncategorizedChannel")
        .transform(OrderItem::toString)
        .handle(Files.outboundAdapter(TEMP_FOLDER)
            .fileExistsMode(FileExistsMode.APPEND).appendNewLine(true))
        .get();
  }
}
