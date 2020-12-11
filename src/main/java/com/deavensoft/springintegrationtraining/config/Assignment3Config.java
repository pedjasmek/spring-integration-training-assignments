package com.deavensoft.springintegrationtraining.config;

import com.deavensoft.springintegrationtraining.domain.Order;
import com.deavensoft.springintegrationtraining.domain.OrderItem;
import java.io.File;
import java.net.URL;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.BridgeTo;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.Splitter;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.json.JsonToObjectTransformer;
import org.springframework.messaging.MessageChannel;

/**
 * Assignment 3. - Ingest JSON files having Order with OrderItems
 *
 * 1. Configure publish-subscribe message channel named "jsonOrderItemObjectChannel"
 *    - Additionally, "bridge" channel "jsonOrderItemObjectChannel" to "orderItemObjectChannel"
 *
 * 2. From the resource folder "inbound/json" load XML files containing OrderItems.
 *    - Configure file inbound message source to the channel "jsonFileInputChannel".
 *    - Use getDirectory() method to read files from.
 *    - Use AcceptOnceFileListFilter to load files only once.
 *    - "inbound/json" folder can contain other files, so filter only JSON files.
 *
 * 3. Transform JSON to Order object and send it to the output channel "jsonOrderObjectChannel"
 *
 * 4. Split the Order object into the list of OrderItem objects and
 *    send them to output channel "jsonOrderItemObjectChannel"
 *
 * 5. Run Assignment3Test. It should pass.
 */
@Configuration
@Slf4j
public class Assignment3Config {
  private static final String JSON_INBOUND_PATH = "inbound/json";


  private File getDirectory() {
    try {
      URL pathUrl = getClass().getClassLoader().getResource(JSON_INBOUND_PATH);
      return new File(pathUrl.toURI());
    } catch (Exception e) {
      log.error("Problem reading inbound directory!", e);
      throw new RuntimeException(e);
    }
  }

  @Bean
  public MessageChannel jsonFileInputChannel() {
    return MessageChannels.publishSubscribe().get();
  }

  @Bean
  @BridgeTo("orderItemObjectChannel")
  public MessageChannel jsonOrderItemObjectChannel() {
    return MessageChannels.publishSubscribe().get();
  }

  @Bean
  @InboundChannelAdapter(value = "jsonFileInputChannel", poller = @Poller(fixedDelay = "100"))
  public MessageSource<File> fileReadingMessageSource() {
    FileReadingMessageSource source = new FileReadingMessageSource();
    source.setDirectory(getDirectory());
    CompositeFileListFilter<File> compositeFileListFilter= new CompositeFileListFilter<>();
    compositeFileListFilter.addFilter(new AcceptOnceFileListFilter<>());
    compositeFileListFilter.addFilter(new SimplePatternFileListFilter("*.json"));
    source.setFilter(compositeFileListFilter);
    return source;
  }

  @Bean
  @Transformer(inputChannel = "jsonFileInputChannel", outputChannel = "jsonOrderObjectChannel")
  public JsonToObjectTransformer jsonToObjectTransformer() {
    return new JsonToObjectTransformer(Order.class);
  }

  @Bean
  public MessageChannel jsonOrderObjectChannel() {
    return MessageChannels.publishSubscribe().get();
  }

  @Splitter(inputChannel = "jsonOrderObjectChannel", outputChannel = "jsonOrderItemObjectChannel")
  public List<OrderItem> extractOrderItemsFromOrder(Order order) {
    return order.getItems();
  }

}
