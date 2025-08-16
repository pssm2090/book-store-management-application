package com.bookstore.service;

import com.bookstore.dto.payment.PaymentRequestDTO;
import com.bookstore.dto.payment.PaymentResponseDTO;
import com.bookstore.entity.Order;
import com.bookstore.entity.Payment;
import com.bookstore.exception.*;
import com.bookstore.repository.OrderRepository;
import com.bookstore.repository.PaymentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    // 1. Create payment
    public PaymentResponseDTO createPayment(PaymentRequestDTO request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + request.getOrderId()));

        if (paymentRepository.existsByOrder(order)) {
            throw new OrderAlreadyPaidException("Payment already exists for order ID: " + order.getOrderId());
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(request.getAmount());
        payment.setMethod(request.getMethod());
        payment.setStatus(request.getStatus());
        payment.setTransactionId(request.getTransactionId());

        // ✅ Set payment date explicitly
        payment.setPaymentDate(LocalDateTime.now());

        Payment saved = paymentRepository.save(payment);
        return mapToDTO(saved);
    }

    // 2. Get payment by payment ID
    public PaymentResponseDTO getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + paymentId));
        return mapToDTO(payment);
    }

    // 3. Get payment by order ID
    public PaymentResponseDTO getPaymentByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

        Payment payment = paymentRepository.findByOrder(order)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for Order ID: " + orderId));

        return mapToDTO(payment);
    }

    private PaymentResponseDTO mapToDTO(Payment payment) {
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setPaymentId(payment.getPaymentId());
        dto.setOrderId(payment.getOrder().getOrderId());
        dto.setAmount(payment.getAmount());
        dto.setMethod(payment.getMethod());
        dto.setStatus(payment.getStatus());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setTransactionId(payment.getTransactionId());
        return dto;
    }
}
