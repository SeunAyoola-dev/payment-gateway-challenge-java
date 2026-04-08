package com.checkout.payment.gateway.repository;

import com.checkout.payment.gateway.model.PaymentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PaymentsRepositoryTest {

    private PaymentsRepository repository;

    @BeforeEach
    void setUp() {
        repository = new PaymentsRepository();
    }

    @Test
    void whenAddPaymentThenItCanBeRetrieved() {
        UUID id = UUID.randomUUID();
        PaymentResponse response = new PaymentResponse();
        response.setId(id);

        repository.add(response);
        Optional<PaymentResponse> retrieved = repository.get(id);

        assertTrue(retrieved.isPresent());
        assertEquals(response, retrieved.get());
    }

    @Test
    void whenGetNonExistentPaymentThenReturnEmpty() {
        Optional<PaymentResponse> retrieved = repository.get(UUID.randomUUID());
        assertTrue(retrieved.isEmpty());
    }
}
