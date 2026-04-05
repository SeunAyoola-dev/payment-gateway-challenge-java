package com.checkout.payment.gateway.validation;

import com.checkout.payment.gateway.model.PostPaymentRequest;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
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
    List<String> errors = new ArrayList<>();

    if (isCardExpired(postPaymentRequest)) {
      errors.add("Card has expired");
    }

    if (!isCurrencyCodeSupported(postPaymentRequest)) {
      errors.add("Currency code not supported");
    }

    return errors.isEmpty();
  }

  private boolean isCardExpired(PostPaymentRequest postPaymentRequest) {
    YearMonth expiryDate = YearMonth.of(postPaymentRequest.getExpiryYear(),  postPaymentRequest.getExpiryMonth());
    return expiryDate.isBefore(YearMonth.now());
  }

  private boolean isCurrencyCodeSupported(PostPaymentRequest postPaymentRequest) {
    return supportedCurrencies.contains(postPaymentRequest.getCurrency());
  }

}
