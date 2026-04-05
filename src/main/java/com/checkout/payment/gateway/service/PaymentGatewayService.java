package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.BankPaymentRequest;
import com.checkout.payment.gateway.model.BankPaymentResponse;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.UUID;
import com.checkout.payment.gateway.validation.PaymentRequestValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.checkout.payment.gateway.mappers.PaymentResponseMapper.mapToResponse;

@Service
public class PaymentGatewayService {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayService.class);
  private static final String BANK_URL = "http://localhost:8080/payments";

  private final PaymentsRepository paymentsRepository;
  private final PaymentRequestValidator paymentRequestValidator;
  private final RestTemplate restTemplate;

  public PaymentGatewayService(
      PaymentsRepository paymentsRepository,
      PaymentRequestValidator paymentRequestValidator,
      RestTemplate restTemplate
  ) {
    this.paymentsRepository = paymentsRepository;
    this.paymentRequestValidator = paymentRequestValidator;
    this.restTemplate = restTemplate;
  }

  public PostPaymentResponse getPaymentById(UUID id) {
    LOG.debug("Requesting access to payment with ID {}", id);
    return paymentsRepository.get(id).orElseThrow(() -> new EventProcessingException("Invalid ID"));
  }

  public PostPaymentResponse processPayment(PostPaymentRequest paymentRequest) {
    paymentRequestValidator.validate(paymentRequest);

    var bankRequest = new BankPaymentRequest(
        paymentRequest.getCardNumber(),
        paymentRequest.getExpiryDate(),
        paymentRequest.getCurrency(),
        paymentRequest.getAmount(),
        paymentRequest.getCvv()
    );

    LOG.debug("Sending payment request for currency {}", paymentRequest.getCurrency());

    try {
      var bankResponse = restTemplate.postForEntity(BANK_URL, bankRequest, BankPaymentResponse.class);
      LOG.debug("Received response from bank {}", bankResponse.getBody().toString());

      PostPaymentResponse response = mapToResponse(bankResponse.getBody(), paymentRequest);
      paymentsRepository.add(response);

      LOG.debug("Payment saved with ID {}", response.getId());
      return response;

    } catch (Exception e) {
      LOG.error("Error communicating with the bank", e);
      throw new EventProcessingException("Failed to process payment");
    }
  }
}
