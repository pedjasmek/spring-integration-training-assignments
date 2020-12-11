package com.deavensoft.springintegrationtraining.config;

import java.io.File;
import java.net.URL;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.channel.interceptor.WireTap;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.file.transformer.FileToStringTransformer;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

/**
 * The PROJECT
 *
 * The goal of this project is to implement the Orders processing system for different shops.
 * - The physical shop is delivering each Order Item as an XML file and place those to "inbound/xml" orders.
 * - The web shop is delivering Orders with the grouped Order Items (based on the shopping basket).
 *   For each Order the new JSON file is created and placed in "inbound/json" folder.
 * - Order Items have different categories (like APPAREL, ELECTRONICS, FOOD).
 *   After the files are ingested and processed, they should be send to appropriate email address,
 *   so the Order Items can be shipped.
 *
 *
 * Assignment 1. - Ingest XML files and log the payload to the console
 *
 * 1. Build the Maven project:
 *    mvn clean package -DskipTests
 *
 * 2. Configure the PublishSubscribe message channel with the name "fileInputChannel".
 *
 * 3. From the resource folder "inbound/xml" load XML files containing OrderItems.
 *    - Configure file inbound message source to the channel "fileInputChannel".
 *    - Use getDirectory() method to read files from.
 *    - Use AcceptOnceFileListFilter to load files only once.
 *    - "inbound/xml" folder can contain other files, so filter only XML files.
 *
 * 4. Configure Transformer bean to convert file from the "fileInputChannel"
 *    to String and send it to "fileToStringChannel"
 *
 * 5. Run Assignment1Test. It should pass.
 */
@Configuration
@Slf4j
public class Assignment1Config {

  private static final String XML_INBOUND_PATH = "inbound/xml";

  @Bean(name = PollerMetadata.DEFAULT_POLLER)
  public PollerMetadata poller() {
    return Pollers.fixedRate(500).get();
  }

  @Bean
  public MessageChannel fileToStringChannel() {
    return MessageChannels.publishSubscribe()
        .interceptor(new WireTap(logChannel()))
        .get();
  }

  @Bean
  public MessageChannel logChannel() {
    return new PublishSubscribeChannel();
  }

  private File getDirectory() {
    try {
      URL pathUrl = getClass().getClassLoader().getResource(XML_INBOUND_PATH);
      return new File(pathUrl.toURI());
    } catch (Exception e) {
      log.error("Problem reading inbound directory!", e);
      throw new RuntimeException(e);
    }
  }

  @Bean
  @ServiceActivator(inputChannel = "logChannel")
  public MessageHandler logger() {
    LoggingHandler loggingHandler =  new LoggingHandler(LoggingHandler.Level.INFO.name());
    loggingHandler.setLoggerName("CHANNEL_LOGGER");
    return loggingHandler;
  }


  @Bean
  public MessageChannel fileInputChannel() {
    return new PublishSubscribeChannel();
  }

  @Bean
  @InboundChannelAdapter(value = "fileInputChannel", poller = @Poller(fixedDelay = "100"))
  public MessageSource<File> xmlFileReadingMessageSource() {
    FileReadingMessageSource source = new FileReadingMessageSource();
    source.setDirectory(getDirectory());
    CompositeFileListFilter<File> compositeFileListFilter= new CompositeFileListFilter<>();
    compositeFileListFilter.addFilter(new AcceptOnceFileListFilter<>());
    compositeFileListFilter.addFilter(new SimplePatternFileListFilter("*.xml"));
    source.setFilter(compositeFileListFilter);
    return source;
  }

  @Bean
  @Transformer(inputChannel = "fileInputChannel", outputChannel = "fileToStringChannel")
  public FileToStringTransformer fileToStringTransformer() {
    return new FileToStringTransformer();
  }

}
