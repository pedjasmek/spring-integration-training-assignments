package com.deavensoft.springintegrationtraining;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.deavensoft.springintegrationtraining.domain.OrderItem;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;

@SpringBootTest
class Assignment4Test {

  @Autowired
  private List<Message<OrderItem>> filteredPayloadList;

  @Autowired
  private List<Message<OrderItem>> apparelPayloadList;

  @Autowired
  private List<Message<OrderItem>> uncategorizedPayloadList;

  @Autowired
  private List<Message<OrderItem>> electronicsPayloadList;

  @Test
  void whenProcessed_ShouldFilterUnavailableOrderItems() throws Exception {
    Thread.sleep(1000L);
    assertThat(filteredPayloadList, hasSize(4));
  }

  @Test
  void whenProcessed_ShouldRouteOrderItemsToAppropriateChannels() throws Exception {
    Thread.sleep(1000L);
    assertThat(apparelPayloadList, hasSize(1));
    assertThat(electronicsPayloadList, hasSize(1));
    assertThat(uncategorizedPayloadList, hasSize(2));

    assertFalse(apparelPayloadList.stream()
            .map(message -> message.getPayload().getCategory())
            .anyMatch(category -> !category.equals("APPAREL")));

    assertFalse(electronicsPayloadList.stream()
            .map(message -> message.getPayload().getCategory())
            .anyMatch(category -> !category.equals("ELECTRONICS")));
  }


  @TestConfiguration
  static class MyTestConfig {

    @Bean
    public List<Message<OrderItem>> filteredPayloadList() {
      return new ArrayList<>();
    }


    @Bean
    public List<Message<OrderItem>> apparelPayloadList() {
      return new ArrayList<>();
    }

    @Bean
    public List<Message<OrderItem>> uncategorizedPayloadList() {
      return new ArrayList<>();
    }

    @Bean
    public List<Message<OrderItem>> electronicsPayloadList() {
      return new ArrayList<>();
    }

    @Bean
    @ServiceActivator(inputChannel = "filteredOrderItemObjectChannel")
    @SuppressWarnings("unchecked")
    public MessageHandler filteredOrderItemHandler(List<Message<OrderItem>> filteredPayloadList) {
      return message -> filteredPayloadList.add((Message<OrderItem>) message);
    }

    @Bean
    @ServiceActivator(inputChannel = "apparelChannel")
    @SuppressWarnings("unchecked")
    public MessageHandler apparelOrderItemsHandler(List<Message<OrderItem>> apparelPayloadList) {
      return message -> apparelPayloadList.add((Message<OrderItem>) message);
    }

    @Bean
    @ServiceActivator(inputChannel = "uncategorizedChannel")
    @SuppressWarnings("unchecked")
    public MessageHandler uncategoriezedOrderItemsHandler(List<Message<OrderItem>> uncategorizedPayloadList) {
      return message -> uncategorizedPayloadList.add((Message<OrderItem>) message);
    }

    @Bean
    @ServiceActivator(inputChannel = "electronicsChannel")
    @SuppressWarnings("unchecked")
    public MessageHandler electronicsOrderItemsHandler(List<Message<OrderItem>> electronicsPayloadList) {
      return message -> electronicsPayloadList.add((Message<OrderItem>) message);
    }

  }


}
