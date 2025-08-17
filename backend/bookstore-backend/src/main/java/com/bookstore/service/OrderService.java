package com.bookstore.service;

import com.bookstore.dto.order.*;
import com.bookstore.entity.*;
import com.bookstore.exception.*;
import com.bookstore.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private UserService userService;
    @Autowired private BookService bookService;

    // Added PENDING to transition rules
    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = Map.of(
        OrderStatus.PENDING, EnumSet.of(OrderStatus.CANCELLED), // Added
        OrderStatus.PLACED, EnumSet.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED),
        OrderStatus.SHIPPED, EnumSet.of(OrderStatus.DELIVERED),
        OrderStatus.DELIVERED, EnumSet.of(OrderStatus.RETURNED),
        OrderStatus.RETURNED, EnumSet.noneOf(OrderStatus.class),
        OrderStatus.CANCELLED, EnumSet.noneOf(OrderStatus.class)
    );

    @Transactional
    public OrderResponseDTO placeOrder(OrderRequestDTO orderRequestDTO) {
        User user = userService.getCurrentUser();

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new CartNotFoundException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new EmptyCartException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentMethod(orderRequestDTO.getPaymentMethod());
        order.setPaymentStatus(PaymentStatus.UNPAID);
        order.setPlacedAt(LocalDateTime.now());

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            Book book = cartItem.getBook();
            int quantity = cartItem.getQuantity();

            if (book.getStockQuantity() < quantity) {
                throw new InsufficientStockException("Insufficient stock for book: " + book.getTitle());
            }

            bookService.deductStockAfterOrder(book.getBookId(), quantity, false);

            OrderItem orderItem = new OrderItem();
            orderItem.setBook(book);
            orderItem.setQuantity(quantity);
            orderItem.setPrice(book.getPrice());
            orderItem.setOrder(order);

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(book.getPrice().multiply(BigDecimal.valueOf(quantity)));
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);

        orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);
        cartItemRepository.deleteAll(cart.getItems());

        return mapToOrderResponseDTO(order);
    }

    public List<OrderResponseDTO> getMyOrders() {
        User user = userService.getCurrentUser();
        List<Order> orders = orderRepository.findByUser(user);
        return orders.stream().map(this::mapToOrderResponseDTO).collect(Collectors.toList());
    }

    public OrderResponseDTO getOrderById(Long orderId) {
        Order order = findOrderById(orderId);
        return mapToOrderResponseDTO(order);
    }

    public Order getOrderEntityById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
    }
    
    public List<OrderResponseForAdminDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(this::mapToOrderResponseForAdminDTO).collect(Collectors.toList());
    }

    @Transactional
    public OrderResponseDTO updateOrderStatus(Long orderId, OrderStatusUpdateRequestDTO statusUpdateDTO) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        OrderStatus newStatus = parseOrderStatus(statusUpdateDTO.getStatus());
        OrderStatus currentStatus = order.getStatus();

        Set<OrderStatus> allowedNextStatuses = ALLOWED_TRANSITIONS.getOrDefault(currentStatus, Set.of());
        if (!allowedNextStatuses.contains(newStatus)) {
            throw new InvalidOrderStatusException(
                "Invalid status transition from " + currentStatus + " to " + newStatus +
                ". Allowed: " + allowedNextStatuses
            );
        }

        order.setStatus(newStatus);
        updateTimestamps(order, newStatus);

        Order savedOrder = orderRepository.save(order);
        return mapToOrderResponseDTO(savedOrder);
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = findOrderById(orderId);
        User currentUser = userService.getCurrentUser();

        boolean isOwner = order.getUser().getUserId().equals(currentUser.getUserId());

        if (!isOwner) {
            throw new AccessDeniedException("You can only cancel your own order.");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStatusException("Only orders with status PENDING or PLACED can be cancelled.");
        }

        order.setStatus(OrderStatus.CANCELLED);
        updateTimestamps(order, OrderStatus.CANCELLED);

        orderRepository.save(order);
    }

    // ----------------------- Helper Methods -----------------------

    private Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));
    }

    private OrderStatus parseOrderStatus(String status) {
        try {
            return OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidOrderStatusException("Invalid order status: " + status);
        }
    }

    private void updateTimestamps(Order order, OrderStatus status) {
        switch (status) {
            case SHIPPED -> order.setShippedAt(LocalDateTime.now());
            case DELIVERED -> order.setDeliveredAt(LocalDateTime.now());
            case RETURNED -> {
                order.setReturnedAt(LocalDateTime.now());
                if (order.getOrderItems() != null) {
                    bookService.restoreStockAfterCancellationOrReturn(order.getOrderItems());
                }
            }
            case CANCELLED -> {
                order.setCancelledAt(LocalDateTime.now());
                if (order.getOrderItems() != null) {
                    bookService.restoreStockAfterCancellationOrReturn(order.getOrderItems());
                }
            }
            default -> {}
        }
    }

    private OrderResponseDTO mapToOrderResponseDTO(Order order) {
        List<OrderItemDTO> items = Optional.ofNullable(order.getOrderItems())
                .orElse(Collections.emptyList())
                .stream()
                .map(item -> {
                    OrderItemDTO dto = new OrderItemDTO();
                    dto.setBookId(item.getBook().getBookId());
                    dto.setTitle(item.getBook().getTitle());
                    dto.setPrice(item.getPrice());
                    dto.setQuantity(item.getQuantity());
                    return dto;
                })
                .collect(Collectors.toList());

        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setOrderId(order.getOrderId());
        dto.setStatus(order.getStatus().name());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setItems(items);
        dto.setPlacedAt(order.getPlacedAt());
        dto.setCancelledAt(order.getCancelledAt());
        dto.setShippedAt(order.getShippedAt());
        dto.setDeliveredAt(order.getDeliveredAt());
        dto.setReturnedAt(order.getReturnedAt());

        dto.setPaymentMethod(order.getPaymentMethod() != null ? order.getPaymentMethod().name() : null);
        dto.setPaymentStatus(order.getPaymentStatus() != null ? order.getPaymentStatus().name() : null);

        return dto;
    }
    
    private OrderResponseForAdminDTO mapToOrderResponseForAdminDTO(Order order) {
        List<OrderItemDTO> items = Optional.ofNullable(order.getOrderItems())
                .orElse(Collections.emptyList())
                .stream()
                .map(item -> {
                    OrderItemDTO dto = new OrderItemDTO();
                    dto.setBookId(item.getBook().getBookId());
                    dto.setTitle(item.getBook().getTitle());
                    dto.setPrice(item.getPrice());
                    dto.setQuantity(item.getQuantity());
                    return dto;
                })
                .collect(Collectors.toList());

        OrderResponseForAdminDTO dto = new OrderResponseForAdminDTO();
        dto.setOrderId(order.getOrderId());
        dto.setUserId(order.getUser().getUserId()); // ✅ only in admin DTO
        dto.setStatus(order.getStatus().name());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setItems(items);
        dto.setPlacedAt(order.getPlacedAt());
        dto.setCancelledAt(order.getCancelledAt());
        dto.setShippedAt(order.getShippedAt());
        dto.setDeliveredAt(order.getDeliveredAt());
        dto.setReturnedAt(order.getReturnedAt());
        dto.setPaymentMethod(order.getPaymentMethod() != null ? order.getPaymentMethod().name() : null);
        dto.setPaymentStatus(order.getPaymentStatus() != null ? order.getPaymentStatus().name() : null);

        return dto;
    }

}
