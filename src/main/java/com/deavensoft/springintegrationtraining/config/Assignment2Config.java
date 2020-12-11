package com.deavensoft.springintegrationtraining.config;

import com.deavensoft.springintegrationtraining.domain.OrderItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.BridgeTo;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.interceptor.WireTap;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.xml.transformer.UnmarshallingTransformer;
import org.springframework.messaging.MessageChannel;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

/**
 * Assignment 2. - Convert the XML file into OrderItem Object
 *
 * 1. Configure publish-subscribe message channel named "orderItemObjectChannel",
 *    with the wiretap interceptor to the "logChannel", so that the received payload
 *    on this channel can be logged to console.
 *
 * 2. Configure publish-subscribe message channel named "xmlOrderItemObjectChannel"
 *    - Additionally, "bridge" channel "xmlOrderItemObjectChannel" to "orderItemObjectChannel"
 *
 * 3. Configure XML Unmarshalling transformer in order to convert XML input from "fileInputChannel"
 *    to the Object (OrderItem), and send it to the outputChannel "xmlOrderItemObjectChannel"
 *    - Note: Jaxb2Marshaller can be used for both marshalling and unmarshalling.
 *
 * 4. Run Assignment2Test. It should pass.
 */
@Configuration
@Slf4j
public class Assignment2Config {

  @Bean
  public MessageChannel orderItemObjectChannel(MessageChannel logChannel) {
    return MessageChannels.publishSubscribe()
        .interceptor(new WireTap(logChannel))
        .get();
  }

  @Bean
  @BridgeTo("orderItemObjectChannel")
  public MessageChannel xmlOrderItemObjectChannel() {
    return MessageChannels.publishSubscribe().get();
  }

  @Bean
  @Transformer(inputChannel = "fileInputChannel", outputChannel = "xmlOrderItemObjectChannel")
  public UnmarshallingTransformer marshallingTransformer() {
    Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
    jaxb2Marshaller.setClassesToBeBound(OrderItem.class);

    return new UnmarshallingTransformer(jaxb2Marshaller);
  }
}
