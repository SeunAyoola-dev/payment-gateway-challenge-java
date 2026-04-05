package com.checkout.payment.gateway.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BankPaymentRequest {
  @JsonProperty("card_number")
  private String cardNumber;

  @JsonProperty("expiry_date")
  private String expiryDate;

  @JsonProperty("currency")
  private String currency;

  @JsonProperty("amount")
  private int amount;

  @JsonProperty("cvv")
  private String cvv;

  public BankPaymentRequest(String cardNumber, String expiryDate, String currency, int amount, String cvv) {
    this.cardNumber = cardNumber;
    this.expiryDate = expiryDate;
    this.currency = currency;
    this.amount = amount;
    this.cvv = cvv;
  }
}
