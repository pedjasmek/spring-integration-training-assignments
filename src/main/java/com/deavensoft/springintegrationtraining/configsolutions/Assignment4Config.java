package com.deavensoft.springintegrationtraining.configsolutions;

import com.deavensoft.springintegrationtraining.domain.OrderItem;
import com.deavensoft.springintegrationtraining.service.InventoryService;
import com.deavensoft.springintegrationtraining.service.InventoryServiceImpl;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Filter;
import org.springframework.integration.annotation.Router;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.MessageChannel;

/**
 * Assignment 4. - Filter out OrderItems which are not available in the product inventory
 *                 and route messages to different channels based on the OrderItems category
 *
 * 1. Configure publish-subscribe message channel named "filteredOrderItemObjectChannel".
 *
 * 2. Configure Filter:
 *    - input channel "orderItemObjectChannel" and output channel "filteredOrderItemObjectChannel"
 *    - call InventoryService bean (defined in this config file) to check is item available,
 *      based on the OrderItem.itemNumber
 *
 * 3. Configure additional publish-subscribe message channels:
 *    - "apparelChannel", to route OrderItems with a category APPAREL
 *    - "electronicsChannel", to route OrderItems with a category ELECTRONICS
 *    - "uncategorizedChannel", to route all other OrderItems categories
 *
 * 4. Configure Router:
 *    - input channel "filteredOrderItemObjectChannel"
 *    - route Messages (with OrderItems) to appropriate channels, as described above
 *
 * 5. Run Assignment4Test. It should pass.
 */
//@Configuration // commented-out, so the config is not picked up by the Spring Boot
@Slf4j
public class Assignment4Config {

  @Bean
  public InventoryService inventoryService() {
    return new InventoryServiceImpl();
  }

  @Bean
  public MessageChannel filteredOrderItemObjectChannel() {
    return MessageChannels.publishSubscribe().get();
  }

  @Filter(inputChannel = "orderItemObjectChannel", outputChannel = "filteredOrderItemObjectChannel")
  public boolean filter(OrderItem orderItem) {
    return inventoryService().isItemAvailable(orderItem.getItemNumber());
  }

  @Bean
  public MessageChannel apparelChannel() {
    return MessageChannels.publishSubscribe().get();
  }

  @Bean
  public MessageChannel electronicsChannel() {
    return MessageChannels.publishSubscribe().get();
  }

  @Bean
  public MessageChannel uncategorizedChannel() {
    return MessageChannels.publishSubscribe().get();
  }

  @Router(inputChannel = "filteredOrderItemObjectChannel")
  public List<String> route(OrderItem orderItem) {
    String routingChannel;
    switch (orderItem.getCategory()) {
      case "APPAREL":
        routingChannel = "apparelChannel";
        break;
      case "ELECTRONICS":
        routingChannel = "electronicsChannel";
        break;
      default:
        routingChannel = "uncategorizedChannel";
    }
    return Collections.singletonList(routingChannel);
  }
}
