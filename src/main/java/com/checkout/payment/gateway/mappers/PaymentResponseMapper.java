package com.checkout.payment.gateway.mappers;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.BankPaymentResponse;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PaymentResponse;
import jakarta.annotation.Nonnull;
import java.util.UUID;

public class PaymentResponseMapper {

  public static PaymentResponse mapRejected(
      PostPaymentRequest paymentRequest
  ) {
    return buildResponse(paymentRequest, PaymentStatus.REJECTED);
  }
  public static PaymentResponse mapToResponse(
      BankPaymentResponse bankResponse,
      PostPaymentRequest paymentRequest) {

    PaymentStatus status = bankResponse.isAuthorized() ? PaymentStatus.AUTHORIZED : PaymentStatus.DECLINED;

    return buildResponse(paymentRequest, status);
  }

  @Nonnull
  private static PaymentResponse buildResponse(
      PostPaymentRequest paymentRequest,
      PaymentStatus status) {
    PaymentResponse response = new PaymentResponse();

    response.setId(UUID.randomUUID());
    response.setStatus(status);
    response.setCardNumberLastFour(lastFourDigits(paymentRequest.getCardNumber()));
    response.setExpiryMonth(paymentRequest.getExpiryMonth());
    response.setExpiryYear(paymentRequest.getExpiryYear());
    response.setCurrency(paymentRequest.getCurrency());
    response.setAmount(paymentRequest.getAmount());

    return response;
  }

  private static String lastFourDigits(String cardNumber) {
    if (cardNumber == null || cardNumber.length() < 4) {
      return cardNumber;
    }
    return cardNumber.substring(cardNumber.length() - 4);
  }

}
