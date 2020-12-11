package com.deavensoft.springintegrationtraining.service;

public class InventoryServiceImpl implements InventoryService {

  @Override
  public boolean isItemAvailable(String itemNumber) {
    return !"555".equals(itemNumber);
  }
}
