package com.deavensoft.springintegrationtraining;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
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
class Assignment1Test {

  @Autowired
  private List<Message<String>> payloadList;

  @Test
  void whenProcessed_ShouldProcessOnlyXmlFiles() throws Exception {
    Thread.sleep(1000L);
    assertThat(payloadList, hasSize(2));
    assertThat(payloadList.stream()
            .flatMap(message -> message.getHeaders().entrySet().stream())
            .filter(header -> header.getKey().equals("file_name"))
            .map(Entry::getValue)
            .collect(Collectors.toSet()),
        containsInAnyOrder("OrderItem1.xml", "OrderItem2.xml")
    );
  }


  @TestConfiguration
  static class MyTestConfig {

    @Bean
    public List<Message<String>> payloadList() {
      return new ArrayList<>();
    }

    @Bean
    @ServiceActivator(inputChannel = "fileToStringChannel")
    @SuppressWarnings("unchecked")
    public MessageHandler testHandler(List<Message<String>> payloadList) {
      return message -> payloadList.add((Message<String>) message);
    }
  }


}
