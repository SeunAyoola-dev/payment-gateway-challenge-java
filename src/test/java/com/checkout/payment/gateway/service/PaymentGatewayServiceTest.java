package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.BankPaymentRequest;
import com.checkout.payment.gateway.model.BankPaymentResponse;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import com.checkout.payment.gateway.validation.PaymentRequestValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentGatewayServiceTest {

    private PaymentGatewayService paymentGatewayService;

    @Mock
    private PaymentsRepository paymentsRepository;

    @Mock
    private PaymentRequestValidator paymentRequestValidator;

    @Mock
    private RestTemplate restTemplate;

    private final String bankUrl = "http://localhost:8080/payments";

    @BeforeEach
    void setUp() {
        paymentGatewayService = new PaymentGatewayService(bankUrl, paymentsRepository, paymentRequestValidator, restTemplate);
    }

    @Test
    void whenGetPaymentByIdExistsThenReturnPayment() {
        UUID id = UUID.randomUUID();
        PostPaymentResponse expectedResponse = new PostPaymentResponse();
        expectedResponse.setId(id);
        when(paymentsRepository.get(id)).thenReturn(Optional.of(expectedResponse));

        PostPaymentResponse actualResponse = paymentGatewayService.getPaymentById(id);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void whenGetPaymentByIdDoesNotExistThenThrowException() {
        UUID id = UUID.randomUUID();
        when(paymentsRepository.get(id)).thenReturn(Optional.empty());

        assertThrows(EventProcessingException.class, () -> paymentGatewayService.getPaymentById(id));
    }

    @Test
    void whenProcessPaymentInvalidThenReturnRejected() {
        PostPaymentRequest request = new PostPaymentRequest();
        when(paymentRequestValidator.validate(request)).thenReturn(false);

        PostPaymentResponse response = paymentGatewayService.processPayment(request);

        assertEquals(PaymentStatus.REJECTED, response.getStatus());
        verify(restTemplate, never()).postForEntity(anyString(), any(), any());
    }

    @Test
    void whenProcessPaymentValidAndBankAuthorizedThenReturnAuthorized() {
        PostPaymentRequest request = new PostPaymentRequest();
        request.setCardNumber("12345678901234");
        when(paymentRequestValidator.validate(request)).thenReturn(true);

        BankPaymentResponse bankResponse = new BankPaymentResponse();
        bankResponse.setAuthorized(true);
        when(restTemplate.postForEntity(eq(bankUrl), any(BankPaymentRequest.class), eq(BankPaymentResponse.class)))
                .thenReturn(ResponseEntity.ok(bankResponse));

        PostPaymentResponse response = paymentGatewayService.processPayment(request);

        assertEquals(PaymentStatus.AUTHORIZED, response.getStatus());
        verify(paymentsRepository).add(response);
    }

    @Test
    void whenProcessPaymentValidAndBankDeclinedThenReturnDeclined() {
        PostPaymentRequest request = new PostPaymentRequest();
        request.setCardNumber("12345678901234");
        when(paymentRequestValidator.validate(request)).thenReturn(true);

        BankPaymentResponse bankResponse = new BankPaymentResponse();
        bankResponse.setAuthorized(false);
        when(restTemplate.postForEntity(eq(bankUrl), any(BankPaymentRequest.class), eq(BankPaymentResponse.class)))
                .thenReturn(ResponseEntity.ok(bankResponse));

        PostPaymentResponse response = paymentGatewayService.processPayment(request);

        assertEquals(PaymentStatus.DECLINED, response.getStatus());
        verify(paymentsRepository).add(response);
    }

    @Test
    void whenBankCallFailsThenThrowException() {
        PostPaymentRequest request = new PostPaymentRequest();
        when(paymentRequestValidator.validate(request)).thenReturn(true);
        when(restTemplate.postForEntity(anyString(), any(), any())).thenThrow(new RuntimeException("Bank down"));

        assertThrows(EventProcessingException.class, () -> paymentGatewayService.processPayment(request));
    }
}
