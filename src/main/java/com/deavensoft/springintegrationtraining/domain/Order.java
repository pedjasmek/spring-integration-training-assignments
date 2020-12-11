package com.deavensoft.springintegrationtraining.domain;

import java.util.List;
import lombok.Data;

@Data
public class Order {
  private String orderId;
  private List<OrderItem> items;
}
