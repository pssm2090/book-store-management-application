package com.bookstore.controller;

import com.bookstore.dto.order.*;
import com.bookstore.entity.Order;
import com.bookstore.entity.Payment;
import com.bookstore.entity.Role;
import com.bookstore.entity.User;
import com.bookstore.exception.*;
import com.bookstore.service.InvoiceService;
import com.bookstore.service.OrderService;
import com.bookstore.service.PaymentService;
import com.bookstore.service.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	@Autowired
    private OrderService orderService;
	
	@Autowired
	private InvoiceService invoiceService;

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private UserService userService;


    @PostMapping("/place")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponseDTO> placeOrder(@Valid @RequestBody OrderRequestDTO orderRequestDTO) {
        OrderResponseDTO createdOrder = orderService.placeOrder(orderRequestDTO);
        return ResponseEntity.ok(createdOrder);
    }

    @GetMapping("/get/my-orders")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<OrderResponseDTO>> getMyOrders() {
        return ResponseEntity.ok(orderService.getMyOrders());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get-all")
    public ResponseEntity<List<OrderResponseForAdminDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderStatusUpdateRequestDTO statusUpdateDTO) {

        OrderResponseDTO updatedOrder = orderService.updateOrderStatus(orderId, statusUpdateDTO);
        return ResponseEntity.ok(updatedOrder);
    }

    
    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("/cancel/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    
    @GetMapping("/{orderId}/invoice")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long orderId) {
        Order order = orderService.getOrderEntityById(orderId);
        if (order == null) {
            throw new OrderNotFoundException("Order with ID " + orderId + " not found");
        }

        User currentUser = userService.getCurrentUser();
        if (!order.getUser().getUserId().equals(currentUser.getUserId()) &&
                currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("You are not allowed to access this invoice");
        }

        Payment payment = paymentService.getPaymentEntityByOrder(order);
        if (payment == null) {
            throw new PaymentNotFoundException("No payment found for order ID " + orderId);
        }

        byte[] pdfBytes = invoiceService.generateInvoice(order, payment);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "invoice_order_" + orderId + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }


}
