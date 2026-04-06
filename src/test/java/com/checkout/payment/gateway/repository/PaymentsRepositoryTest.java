package com.checkout.payment.gateway.repository;

import com.checkout.payment.gateway.model.PostPaymentResponse;
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
        PostPaymentResponse response = new PostPaymentResponse();
        response.setId(id);

        repository.add(response);
        Optional<PostPaymentResponse> retrieved = repository.get(id);

        assertTrue(retrieved.isPresent());
        assertEquals(response, retrieved.get());
    }

    @Test
    void whenGetNonExistentPaymentThenReturnEmpty() {
        Optional<PostPaymentResponse> retrieved = repository.get(UUID.randomUUID());
        assertTrue(retrieved.isEmpty());
    }
}
