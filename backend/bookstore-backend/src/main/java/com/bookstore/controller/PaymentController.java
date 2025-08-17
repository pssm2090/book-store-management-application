package com.bookstore.controller;

import com.bookstore.dto.payment.PaymentRequestDTO;
import com.bookstore.dto.payment.PaymentResponseDTO;
import com.bookstore.service.PaymentService;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

	@Autowired
    private PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/pay")
    public ResponseEntity<?> createPayment(@Valid @RequestBody PaymentRequestDTO request) {
        try {
        	PaymentResponseDTO response = paymentService.processPayment(request);
            return ResponseEntity.ok(response);  // Handles both cash and card
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Payment error: " + e.getMessage());
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmStripePayment(@RequestParam String paymentIntentId,
                                                  @RequestParam Long orderId) {
        try {
            PaymentResponseDTO response = paymentService.confirmStripePayment(paymentIntentId, orderId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Payment confirmation failed: " + e.getMessage());
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("get-all")
    public ResponseEntity<List<PaymentResponseDTO>> getAllPayments() {
        List<PaymentResponseDTO> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }



    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable Long paymentId) {
        PaymentResponseDTO response = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponseDTO> getPaymentByOrderId(@PathVariable Long orderId) {
        PaymentResponseDTO response = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(response);
    }
}
