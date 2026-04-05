package com.checkout.payment.gateway.mappers;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.BankPaymentResponse;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import jakarta.annotation.Nonnull;
import java.util.UUID;

public class PaymentResponseMapper {

  public static PostPaymentResponse mapRejected(
      PostPaymentRequest paymentRequest
  ) {
    return buildResponse(paymentRequest, PaymentStatus.REJECTED);
  }
  public static PostPaymentResponse mapToResponse(
      BankPaymentResponse bankResponse,
      PostPaymentRequest paymentRequest) {

    PaymentStatus status = bankResponse.isAuthorized() ? PaymentStatus.AUTHORIZED : PaymentStatus.DECLINED;

    return buildResponse(paymentRequest, status);
  }

  @Nonnull
  private static PostPaymentResponse buildResponse(
      PostPaymentRequest paymentRequest,
      PaymentStatus status) {
    int lastFourDigits = Integer.parseInt(lastFourDigits(paymentRequest.getCardNumber()));
    PostPaymentResponse response = new PostPaymentResponse();

    response.setId(UUID.randomUUID());
    response.setStatus(status);
    response.setCardNumberLastFour(lastFourDigits);
    response.setExpiryMonth(paymentRequest.getExpiryMonth());
    response.setExpiryYear(paymentRequest.getExpiryYear());
    response.setCurrency(paymentRequest.getCurrency());
    response.setAmount(paymentRequest.getAmount());

    return response;
  }

  private static String lastFourDigits(String cardNumber) {
    return cardNumber.substring(cardNumber.length() - 4);
  }

}
