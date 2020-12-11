package com.deavensoft.springintegrationtraining.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "itemNumber",
    "productName",
    "category",
    "price",
    "quantity"
})
@XmlRootElement(name = "orderItem")
public class OrderItem {
  private String itemNumber;
  private String productName;
  private String category;
  private Double price;
  private Integer quantity;
}
