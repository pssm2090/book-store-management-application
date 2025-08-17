package com.bookstore.service;

import com.bookstore.dto.payment.PaymentRequestDTO;
import com.bookstore.dto.payment.PaymentResponseDTO;
import com.bookstore.entity.Order;
import com.bookstore.entity.Payment;
import com.bookstore.entity.PaymentMethod;
import com.bookstore.entity.PaymentStatus;
import com.bookstore.exception.*;
import com.bookstore.repository.OrderRepository;
import com.bookstore.repository.PaymentRepository;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;
    
    @Autowired
    private InvoiceService invoiceService;
    

    public PaymentResponseDTO processPayment(PaymentRequestDTO request) throws Exception {
        Order order = getOrderById(request.getOrderId());

        if (paymentRepository.existsByOrder(order)) {
            throw new OrderAlreadyPaidException("Payment already exists for order ID: " + order.getOrderId());
        }

        validateAmount(request.getAmount());

        if ("CASH".equalsIgnoreCase(request.getMethod().toString())) {
            request.setStatus(PaymentStatus.PAID);
            Payment payment = buildPaymentFromRequest(order, request);
            Payment savedPayment = paymentRepository.save(payment);
            updateOrderAfterPayment(order, request);
            return mapToDTO(savedPayment);
        } else if ("CARD".equalsIgnoreCase(request.getMethod().toString())) {
            long amountInPaise = request.getAmount().multiply(BigDecimal.valueOf(100)).longValueExact();

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInPaise)
                    .setCurrency("inr")
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            PaymentResponseDTO response = new PaymentResponseDTO();
            response.setClientSecret(intent.getClientSecret());
            response.setMessage("Stripe PaymentIntent created successfully");
            return response;
        } else {
            throw new InvalidPaymentException("Unsupported payment method: " + request.getMethod());
        }
    }

    public PaymentResponseDTO confirmStripePayment(String paymentIntentId, Long orderId) throws Exception {
        PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);

        if ("succeeded".equals(intent.getStatus())) {
            Order order = getOrderById(orderId);

            if (paymentRepository.existsByOrder(order)) {
                throw new OrderAlreadyPaidException("Payment already exists for order ID: " + order.getOrderId());
            }

            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setAmount(BigDecimal.valueOf(intent.getAmount()).divide(BigDecimal.valueOf(100)));
            payment.setMethod(PaymentMethod.CARD);
            payment.setStatus(PaymentStatus.PAID);
            payment.setTransactionId(intent.getId());
            payment.setPaymentDate(LocalDateTime.now());

            Payment saved = paymentRepository.save(payment);

            updateOrderAfterPayment(order, new PaymentRequestDTO(
                    order.getOrderId(),
                    payment.getAmount(),
                    PaymentMethod.CARD,
                    PaymentStatus.PAID,
                    intent.getId()
            ));

            return mapToDTO(saved);
        } else {
            throw new InvalidPaymentException("Stripe payment not successful. Status: " + intent.getStatus());
        }
    }

    
    public List<PaymentResponseDTO> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(this::mapToDTO)  // ✅ use your existing mapper
                .toList();
    }

    
    public PaymentResponseDTO getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + paymentId));
        return mapToDTO(payment);
    }

    public PaymentResponseDTO getPaymentByOrderId(Long orderId) {
        Order order = getOrderById(orderId);
        Payment payment = getPaymentEntityByOrder(order);
        return mapToDTO(payment);
    }

    public Payment getPaymentEntityByOrder(Order order) {
        return paymentRepository.findByOrder(order)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for Order ID: " + order.getOrderId()));
    }


    
    
    private Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));
    }

    private void validateAmount(BigDecimal bigDecimal) {
        if (bigDecimal == null || bigDecimal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPaymentException("Amount must be greater than 0");
        }
    }

    private Payment buildPaymentFromRequest(Order order, PaymentRequestDTO request) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(request.getAmount());
        payment.setMethod(request.getMethod());
        payment.setStatus(request.getStatus());
        payment.setTransactionId(request.getTransactionId());
        payment.setPaymentDate(LocalDateTime.now());
        return payment;
    }

    private void updateOrderAfterPayment(Order order, PaymentRequestDTO request) {
        order.setPaymentMethod(request.getMethod());
        order.setPaymentStatus(request.getStatus());

        if (request.getStatus() == PaymentStatus.PAID) {
            order.setStatus(com.bookstore.entity.OrderStatus.PLACED);
            cartService.clearCart();

            Payment payment = getPaymentEntityByOrder(order);
            invoiceService.generateInvoice(order, payment);
        }


        orderRepository.save(order);
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
