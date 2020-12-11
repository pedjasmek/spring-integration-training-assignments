package com.deavensoft.springintegrationtraining;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

import com.deavensoft.springintegrationtraining.domain.OrderItem;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;

@SpringBootTest
class Assignment3Test {

  @Autowired
  private List<Message<OrderItem>> payloadList;

  @Test
  void whenProcessed_ShouldProcessJsonFilesAndSplit() throws Exception {
    Thread.sleep(1000L);
    assertThat(payloadList, hasSize(3));
    assertThat(payloadList.get(0).getPayload(), instanceOf(OrderItem.class));
    assertThat(payloadList.get(1).getPayload(), instanceOf(OrderItem.class));
    assertThat(payloadList.get(2).getPayload(), instanceOf(OrderItem.class));
    assertThat(payloadList.stream()
            .map(message -> message.getPayload().getItemNumber())
            .collect(Collectors.toSet()),
        containsInAnyOrder("555", "777", "888")
    );
  }


  @TestConfiguration
  static class MyTestConfig {

    @Bean
    public List<Message<OrderItem>> payloadList() {
      return new ArrayList<>();
    }

    @Bean
    @ServiceActivator(inputChannel = "jsonOrderItemObjectChannel")
    @SuppressWarnings("unchecked")
    public MessageHandler testHandler(List<Message<OrderItem>> payloadList) {
      return message -> payloadList.add((Message<OrderItem>) message);
    }
  }


}
