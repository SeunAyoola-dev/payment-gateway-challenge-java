package com.checkout.payment.gateway.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BankPaymentResponse {
  @JsonProperty("authorized")
  private boolean authorized;

  @JsonProperty("authorization_code")
  private String authorizationCode;

  public boolean isAuthorized() {
    return authorized;
  }

  public void setAuthorized(boolean authorized) {
    this.authorized = authorized;
  }

  @Override
  public String toString() {
    return "BankPaymentResponse{" +
        "authorized=" + authorized +
        ", authorizationCode='" + authorizationCode + '\'' +
        '}';
  }
}
