package com.checkout.payment.gateway.validation;

import com.checkout.payment.gateway.model.PostPaymentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentRequestValidatorTest {

    private PaymentRequestValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PaymentRequestValidator(Set.of("USD", "GBP", "EUR"));
    }

    @Test
    void whenValidRequestThenReturnsTrue() {
        PostPaymentRequest request = createValidRequest();
        assertTrue(validator.validate(request));
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345678901234", "1234567890123456789"}) // 14 and 19 digits
    void whenCardNumberIsValidThenReturnsTrue(String cardNumber) {
        PostPaymentRequest request = createValidRequest();
        request.setCardNumber(cardNumber);
        assertTrue(validator.validate(request));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234567890123", "12345678901234567890", "abc", "1234567890123a"})
    void whenCardNumberIsInvalidThenReturnsFalse(String cardNumber) {
        PostPaymentRequest request = createValidRequest();
        request.setCardNumber(cardNumber);
        assertFalse(validator.validate(request));
    }

    @Test
    void whenCardNumberIsNullThenReturnsFalse() {
        PostPaymentRequest request = createValidRequest();
        request.setCardNumber(null);
        assertFalse(validator.validate(request));
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "1234"})
    void whenCvvIsValidThenReturnsTrue(String cvv) {
        PostPaymentRequest request = createValidRequest();
        request.setCvv(cvv);
        assertTrue(validator.validate(request));
    }

    @ParameterizedTest
    @ValueSource(strings = {"12", "12345", "abc"})
    void whenCvvIsInvalidThenReturnsFalse(String cvv) {
        PostPaymentRequest request = createValidRequest();
        request.setCvv(cvv);
        assertFalse(validator.validate(request));
    }

    @Test
    void whenCvvIsNullThenReturnsFalse() {
        PostPaymentRequest request = createValidRequest();
        request.setCvv(null);
        assertFalse(validator.validate(request));
    }

    @Test
    void whenAmountIsZeroThenReturnsFalse() {
        PostPaymentRequest request = createValidRequest();
        request.setAmount(0);
        assertFalse(validator.validate(request));
    }

    @Test
    void whenAmountIsNegativeThenReturnsFalse() {
        PostPaymentRequest request = createValidRequest();
        request.setAmount(-1);
        assertFalse(validator.validate(request));
    }

    @Test
    void whenCurrencyIsUnsupportedThenReturnsFalse() {
        PostPaymentRequest request = createValidRequest();
        request.setCurrency("JPY");
        assertFalse(validator.validate(request));
    }

    @Test
    void whenCardIsExpiredThenReturnsFalse() {
        PostPaymentRequest request = createValidRequest();
        request.setExpiryMonth(1);
        request.setExpiryYear(2020);
        assertFalse(validator.validate(request));
    }

    @Test
    void whenExpiryIsInvalidThenReturnsFalse() {
        PostPaymentRequest request = createValidRequest();
        request.setExpiryMonth(13); // Invalid month
        assertFalse(validator.validate(request));
    }

    private PostPaymentRequest createValidRequest() {
        PostPaymentRequest request = new PostPaymentRequest();
        request.setCardNumber("123456789012345");
        request.setExpiryMonth(12);
        request.setExpiryYear(2099);
        request.setCurrency("USD");
        request.setAmount(100);
        request.setCvv("123");
        return request;
    }
}
