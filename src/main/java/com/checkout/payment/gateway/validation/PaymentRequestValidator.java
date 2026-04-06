package com.checkout.payment.gateway.validation;

import com.checkout.payment.gateway.model.PostPaymentRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.time.YearMonth;
import java.util.Set;

@Component
public class PaymentRequestValidator {

  private final Set<String> supportedCurrencies;

  public PaymentRequestValidator(
      @Value("${payment.supported-currencies}") Set<String> supportedCurrencies
  ) {
    this.supportedCurrencies = supportedCurrencies;
  }


  public boolean validate(PostPaymentRequest postPaymentRequest) {
    return isCardNumberValid(postPaymentRequest.getCardNumber()) &&
        isCvvValid(postPaymentRequest.getCvv()) &&
        !isCardExpired(postPaymentRequest) &&
        isCurrencyCodeSupported(postPaymentRequest) &&
        isAmountValid(postPaymentRequest.getAmount());
  }

  private boolean isCardNumberValid(String cardNumber) {
    return cardNumber != null && cardNumber.matches("\\d{14,19}");
  }

  private boolean isCvvValid(String cvv) {
    return cvv != null && cvv.matches("\\d{3,4}");
  }

  private boolean isAmountValid(int amount) {
    return amount > 0;
  }

  private boolean isCardExpired(PostPaymentRequest postPaymentRequest) {
    try {
      YearMonth expiryDate = YearMonth.of(postPaymentRequest.getExpiryYear(),
          postPaymentRequest.getExpiryMonth());
      return expiryDate.isBefore(YearMonth.now());

    } catch (Exception e) {
      return true;
    }
  }

  private boolean isCurrencyCodeSupported(PostPaymentRequest postPaymentRequest) {
    return supportedCurrencies.contains(postPaymentRequest.getCurrency());
  }

}
