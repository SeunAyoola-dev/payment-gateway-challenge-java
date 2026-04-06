package com.checkout.payment.gateway.mappers;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.BankPaymentResponse;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentResponseMapperTest {

    @Test
    void whenMapRejectedThenCorrectStatusIsSet() {
        PostPaymentRequest request = createRequest();
        PostPaymentResponse response = PaymentResponseMapper.mapRejected(request);

        assertEquals(PaymentStatus.REJECTED, response.getStatus());
        verifyCommonFields(request, response);
    }

    @Test
    void whenMapToResponseAndAuthorizedThenCorrectStatusIsSet() {
        PostPaymentRequest request = createRequest();
        BankPaymentResponse bankResponse = new BankPaymentResponse();
        bankResponse.setAuthorized(true);

        PostPaymentResponse response = PaymentResponseMapper.mapToResponse(bankResponse, request);

        assertEquals(PaymentStatus.AUTHORIZED, response.getStatus());
        verifyCommonFields(request, response);
    }

    @Test
    void whenMapToResponseAndDeclinedThenCorrectStatusIsSet() {
        PostPaymentRequest request = createRequest();
        BankPaymentResponse bankResponse = new BankPaymentResponse();
        bankResponse.setAuthorized(false);

        PostPaymentResponse response = PaymentResponseMapper.mapToResponse(bankResponse, request);

        assertEquals(PaymentStatus.DECLINED, response.getStatus());
        verifyCommonFields(request, response);
    }

    @Test
    void whenCardNumberIsShortThenItIsNotMasked() {
        PostPaymentRequest request = createRequest();
        request.setCardNumber("123");
        
        PostPaymentResponse response = PaymentResponseMapper.mapRejected(request);
        
        assertEquals("123", response.getCardNumberLastFour());
    }

    @Test
    void whenCardNumberIsNullThenItHandlesItGracefully() {
        PostPaymentRequest request = createRequest();
        request.setCardNumber(null);

        PostPaymentResponse response = PaymentResponseMapper.mapRejected(request);

        assertNull(response.getCardNumberLastFour());
    }

    private void verifyCommonFields(PostPaymentRequest request, PostPaymentResponse response) {
        assertNotNull(response.getId());
        assertEquals("4567", response.getCardNumberLastFour());
        assertEquals(request.getExpiryMonth(), response.getExpiryMonth());
        assertEquals(request.getExpiryYear(), response.getExpiryYear());
        assertEquals(request.getCurrency(), response.getCurrency());
        assertEquals(request.getAmount(), response.getAmount());
    }

    private PostPaymentRequest createRequest() {
        PostPaymentRequest request = new PostPaymentRequest();
        request.setCardNumber("12345678901234567");
        request.setExpiryMonth(12);
        request.setExpiryYear(2025);
        request.setCurrency("USD");
        request.setAmount(100);
        return request;
    }
}
