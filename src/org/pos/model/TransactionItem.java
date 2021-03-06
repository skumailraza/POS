package org.pos.model;


import java.util.HashMap;

import org.apache.log4j.Logger;
import org.pos.Money;

public class TransactionItem extends Base {
  protected String sku;
  protected String name;
  protected Money price;
  protected Money taxRate;
  protected int quantity;

  private static HashMap<String, String> specialItemSkus = null;
  private static Logger logger = Logger.getLogger(TransactionItem.class);

  //sku codes
  public static final String CORRECTION_SKU = "correction";
  public static final String PARTIALCASH_CREDITHALF_SKU = "partialcredithalf";
  public static final String PARTIALCASH_CASHHALF_SKU = "partialcashhalf";

  public TransactionItem() {
    super();
    this.sku = "";
    this.name = "";
    this.price = Money.ZERO;
    this.taxRate = Money.ZERO;
    this.quantity = 1;
  }

  public TransactionItem(InventoryItem item, int quantity) {
    super();
    this.sku = item.getSku();
    this.name = item.getName();
    this.price = item.getPrice();
    this.taxRate = item.getTaxRate();
    this.quantity = quantity;
  }

  public static final TransactionItem createSpecialItem(String sku, Money price) {
    TransactionItem specialItem = null;

    if(TransactionItem.getSpecialItemName(sku) != null) {
      specialItem = new TransactionItem();
      specialItem.setSku(sku);
      specialItem.setName(TransactionItem.getSpecialItemName(sku));
      specialItem.setPrice(price);
    }

    return specialItem;
  }

  public static final String getSpecialItemName(String sku) {
    if(specialItemSkus == null) {
      specialItemSkus = new HashMap<String, String>(5);
      specialItemSkus.put(TransactionItem.CORRECTION_SKU, "Correction Account");
      specialItemSkus.put(TransactionItem.PARTIALCASH_CREDITHALF_SKU, "Credit Adjustment for Partial Cash");
      specialItemSkus.put(TransactionItem.PARTIALCASH_CASHHALF_SKU, "Cash Adjustment for Partial Cash");
    }

    return specialItemSkus.get(sku);
  }

  public static boolean isSpecial(String sku) {
    return TransactionItem.getSpecialItemName(sku) != null;
  }

  public String toString() {
    return name;
  }

  public String getSku() {
    return sku;
  }

  public void setSku(String sku) {
    this.sku = sku;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public Money getPrice() {
    return price;
  }

  public void setPrice(Money price) {
    this.price = price;
  }

  public Money getTotal() {
    return price.times(quantity);
  }

  public Money getTaxRate() {
    return taxRate;
  }

  public void setTaxRate(Money taxRate) {
    this.taxRate = taxRate;
  }
}
