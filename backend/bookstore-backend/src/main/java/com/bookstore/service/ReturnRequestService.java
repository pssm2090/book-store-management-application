package com.bookstore.service;

import com.bookstore.dto.order.ReturnRequestDTO;
import com.bookstore.dto.order.ReturnResponseDTO;
import com.bookstore.entity.*;
import com.bookstore.exception.*;
import com.bookstore.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReturnRequestService {

    @Autowired
    private ReturnRequestRepository returnRequestRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Transactional
    public ReturnResponseDTO createReturnRequest(ReturnRequestDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new UserNotFoundException("Order not found"));

        if (!order.getUser().getUserId().equals(user.getUserId())) {
            throw new InvalidReturnRequestException("You can only request return for your own orders.");
        }

        if (!order.getStatus().equals(OrderStatus.DELIVERED)) {
            throw new InvalidReturnRequestException("Only delivered orders can be returned.");
        }

        if (order.getDeliveredAt() == null ||
            order.getDeliveredAt().plusDays(7).isBefore(LocalDateTime.now())) {
            throw new InvalidReturnRequestException("Return window (7 days) has expired.");
        }

        Map<Long, Integer> orderedBookQuantities = order.getOrderItems().stream()
                .collect(Collectors.toMap(
                        item -> item.getBook().getBookId(),
                        OrderItem::getQuantity
                ));

        for (Map.Entry<Long, Integer> entry : dto.getBookIdToQuantity().entrySet()) {
            Long bookId = entry.getKey();
            Integer returnQty = entry.getValue();

            if (!orderedBookQuantities.containsKey(bookId)) {
                throw new InvalidReturnRequestException("Book with ID " + bookId + " is not in this order.");
            }

            if (returnQty == null || returnQty <= 0) {
                throw new InvalidReturnRequestException("Invalid return quantity for book ID " + bookId);
            }

            if (returnQty > orderedBookQuantities.get(bookId)) {
                throw new InvalidReturnRequestException("Return quantity for book ID " + bookId + " exceeds ordered quantity.");
            }
        }

        ReturnRequest request = new ReturnRequest();
        request.setUser(user);
        request.setOrder(order);
        request.setBookIdToQuantity(dto.getBookIdToQuantity());
        request.setReason(dto.getReason());
        request.setStatus(ReturnStatus.PENDING);

        return toDTO(returnRequestRepository.save(request));
    }


    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ReturnResponseDTO processReturn(Long requestId, boolean approve, String adminResponse) {
        ReturnRequest request = returnRequestRepository.findById(requestId)
                .orElseThrow(() -> new UserNotFoundException("Return request not found"));

        if (!request.getStatus().equals(ReturnStatus.PENDING)) {
            throw new InvalidReturnRequestException("Return already processed.");
        }

        if (approve) {
            request.setStatus(ReturnStatus.APPROVED);

            for (Map.Entry<Long, Integer> entry : request.getBookIdToQuantity().entrySet()) {
                Long bookId = entry.getKey();
                Integer returnQty = entry.getValue();

                bookRepository.findById(bookId).ifPresent(book -> {
                    book.setStockQuantity(book.getStockQuantity() + returnQty);
                });
            }
        } else {
            request.setStatus(ReturnStatus.REJECTED);
        }

        request.setProcessedAt(LocalDateTime.now());
        request.setAdminResponse(adminResponse);

        return toDTO(returnRequestRepository.save(request));
    }


    public List<ReturnResponseDTO> getMyReturns() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return returnRequestRepository.findByUser(user)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<ReturnResponseDTO> getAllReturns() {
        return returnRequestRepository.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    
    
    private ReturnResponseDTO toDTO(ReturnRequest request) {
        ReturnResponseDTO dto = new ReturnResponseDTO();
        dto.setId(request.getReturnRequestId());
        dto.setOrderId(request.getOrder().getOrderId());
        dto.setUserId(request.getUser().getUserId());
        dto.setBookIdToQuantity(request.getBookIdToQuantity());
        dto.setReason(request.getReason());
        dto.setStatus(request.getStatus());
        dto.setRequestedAt(request.getRequestedAt());
        dto.setProcessedAt(request.getProcessedAt());
        dto.setAdminResponse(request.getAdminResponse());
        return dto;
    }
}
