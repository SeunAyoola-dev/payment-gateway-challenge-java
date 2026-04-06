package com.checkout.payment.gateway.controller;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentGatewayControllerTest {

  @Autowired
  private MockMvc mvc;
  @Autowired
  PaymentsRepository paymentsRepository;
  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void whenPaymentWithIdExistThenCorrectPaymentIsReturned() throws Exception {
    PostPaymentResponse payment = new PostPaymentResponse();
    payment.setId(UUID.randomUUID());
    payment.setAmount(10);
    payment.setCurrency("USD");
    payment.setStatus(PaymentStatus.AUTHORIZED);
    payment.setExpiryMonth(12);
    payment.setExpiryYear(2024);
    payment.setCardNumberLastFour("4321");

    paymentsRepository.add(payment);

    mvc.perform(MockMvcRequestBuilders.get("/payment/" + payment.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(payment.getStatus().getName()))
        .andExpect(jsonPath("$.cardNumberLastFour").value(payment.getCardNumberLastFour()))
        .andExpect(jsonPath("$.expiryMonth").value(payment.getExpiryMonth()))
        .andExpect(jsonPath("$.expiryYear").value(payment.getExpiryYear()))
        .andExpect(jsonPath("$.currency").value(payment.getCurrency()))
        .andExpect(jsonPath("$.amount").value(payment.getAmount()));
  }

  @Test
  void whenPaymentWithIdDoesNotExistThen404IsReturned() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/payment/" + UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Page not found"));
  }

  @Test
  void whenPaymentRequestIsInvalidThenRejectedStatusIsReturned() throws Exception {
    PostPaymentRequest request = new PostPaymentRequest();
    request.setCardNumber("12345678901234");
    request.setExpiryMonth(1);
    request.setExpiryYear(2020); // Expired
    request.setCurrency("USD");
    request.setAmount(100);
    request.setCvv("123");

    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value(PaymentStatus.REJECTED.getName()));
  }

  @Test
  void whenPaymentRequestIsValidThenAuthorizedStatusIsReturned() throws Exception {
    PostPaymentRequest request = new PostPaymentRequest();
    request.setCardNumber("2222405343248873");
    request.setExpiryMonth(12);
    request.setExpiryYear(2060);
    request.setCurrency("USD");
    request.setAmount(100);
    request.setCvv("123");

    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value(PaymentStatus.AUTHORIZED.getName()));
  }

  @Test
  void whenPaymentRequestIsValidThenDeclinedStatusIsReturned() throws Exception {
    PostPaymentRequest request = new PostPaymentRequest();
    request.setCardNumber("4000056655665556");
    request.setExpiryMonth(12);
    request.setExpiryYear(2030);
    request.setCurrency("USD");
    request.setAmount(100);
    request.setCvv("123");

    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value(PaymentStatus.DECLINED.getName()));
  }

  @Test
  void whenPaymentRequestHasMissingCardNumberThenRejectedStatusIsReturned() throws Exception {
    PostPaymentRequest request = new PostPaymentRequest();
    request.setExpiryMonth(12);
    request.setExpiryYear(2030);
    request.setCurrency("USD");
    request.setAmount(100);
    request.setCvv("123");

    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value(PaymentStatus.REJECTED.getName()));
  }

  @Test
  void whenPaymentRequestHasInvalidCardNumberThenRejectedStatusIsReturned() throws Exception {
    PostPaymentRequest request = new PostPaymentRequest();
    request.setCardNumber("123");
    request.setExpiryMonth(12);
    request.setExpiryYear(2030);
    request.setCurrency("USD");
    request.setAmount(100);
    request.setCvv("123");

    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value(PaymentStatus.REJECTED.getName()));
  }

  @Test
  void whenPaymentRequestHasInvalidExpiryMonthThenRejectedStatusIsReturned() throws Exception {
    PostPaymentRequest request = new PostPaymentRequest();
    request.setCardNumber("2222405343248873");
    request.setExpiryMonth(13);
    request.setExpiryYear(2030);
    request.setCurrency("USD");
    request.setAmount(100);
    request.setCvv("123");

    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value(PaymentStatus.REJECTED.getName()));
  }

  @Test
  void whenPaymentRequestHasNegativeAmountThenRejectedStatusIsReturned() throws Exception {
    PostPaymentRequest request = new PostPaymentRequest();
    request.setCardNumber("2222405343248873");
    request.setExpiryMonth(12);
    request.setExpiryYear(2030);
    request.setCurrency("USD");
    request.setAmount(-1);
    request.setCvv("123");

    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value(PaymentStatus.REJECTED.getName()));
  }

  @Test
  void whenPaymentRequestHasInvalidCvvThenRejectedStatusIsReturned() throws Exception {
    PostPaymentRequest request = new PostPaymentRequest();
    request.setCardNumber("2222405343248873");
    request.setExpiryMonth(12);
    request.setExpiryYear(2030);
    request.setCurrency("USD");
    request.setAmount(100);
    request.setCvv("12");

    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value(PaymentStatus.REJECTED.getName()));
  }

  @Test
  void whenPaymentRequestHasUnsupportedCurrencyThenRejectedStatusIsReturned() throws Exception {
    PostPaymentRequest request = new PostPaymentRequest();
    request.setCardNumber("2222405343248873");
    request.setExpiryMonth(12);
    request.setExpiryYear(2030);
    request.setCurrency("JPY");
    request.setAmount(100);
    request.setCvv("123");

    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value(PaymentStatus.REJECTED.getName()));
  }

  @Test
  void whenRetrievingPaymentWithInvalidUuidThen400IsReturned() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/payment/invalid-uuid"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void whenBankReturnsErrorThen404IsReturned() throws Exception {
    PostPaymentRequest request = new PostPaymentRequest();
    request.setCardNumber("2222405343248870");
    request.setExpiryMonth(12);
    request.setExpiryYear(2030);
    request.setCurrency("USD");
    request.setAmount(100);
    request.setCvv("123");

    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Page not found"));
  }

}

