package org.pos.model;

import org.pos.Money;

public class ForSale extends InventoryItem {
  public ForSale() {
    super();
  }

  public ForSale(String sku, String name, int quantity, Money price, Money taxRate, boolean unlimited) {
    super();
    this.sku = sku;
    this.name = name;
    this.quantity = quantity;
    this.price = price;
    this.taxRate = taxRate;
    this.unlimited = unlimited;
  }
}
