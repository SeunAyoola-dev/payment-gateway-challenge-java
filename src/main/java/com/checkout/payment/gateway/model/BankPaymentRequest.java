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

  public BankPaymentRequest(PostPaymentRequest paymentRequest) {
    this.cardNumber = paymentRequest.getCardNumber();
    this.expiryDate = paymentRequest.getExpiryDate();
    this.currency = paymentRequest.getCurrency();
    this.amount = paymentRequest.getAmount();
    this.cvv = paymentRequest.getCvv();
  }
}
