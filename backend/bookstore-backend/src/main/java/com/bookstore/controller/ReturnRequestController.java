package com.bookstore.controller;

import com.bookstore.dto.order.ReturnRequestDTO;
import com.bookstore.dto.order.ReturnResponseDTO;
import com.bookstore.service.ReturnRequestService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/returns")
public class ReturnRequestController {

    @Autowired
    private ReturnRequestService returnRequestService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ReturnResponseDTO> requestReturn(@Valid @RequestBody ReturnRequestDTO dto) {
        return ResponseEntity.ok(returnRequestService.createReturnRequest(dto));
    }

    @PostMapping("/{id}/process")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReturnResponseDTO> processReturn(
            @PathVariable Long id,
            @RequestParam boolean approve,
            @RequestParam(required = false) String response) {
        return ResponseEntity.ok(returnRequestService.processReturn(id, approve, response));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<ReturnResponseDTO>> getMyReturns() {
        return ResponseEntity.ok(returnRequestService.getMyReturns());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReturnResponseDTO>> getAllReturns() {
        return ResponseEntity.ok(returnRequestService.getAllReturns());
    }
}
