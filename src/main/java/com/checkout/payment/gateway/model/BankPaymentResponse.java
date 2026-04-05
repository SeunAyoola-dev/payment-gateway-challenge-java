package com.checkout.payment.gateway.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BankPaymentResponse {
  @JsonProperty("authorized")
  private boolean authorized;

  @JsonProperty("authorization_code")
  private String authorizationCode;

  private boolean isValidated = true;

  public boolean isAuthorized() {
    return authorized;
  }

  public void setValidated(boolean validated) {
    this.isValidated = validated;
  }

  public boolean isValidated() {
    return isValidated;
  }

  @Override
  public String toString() {
    return "BankPaymentResponse{" +
        "authorized=" + authorized +
        ", authorizationCode='" + authorizationCode + '\'' +
        '}';
  }
}
