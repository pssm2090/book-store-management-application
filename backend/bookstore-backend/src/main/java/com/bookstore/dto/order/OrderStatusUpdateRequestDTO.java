package com.bookstore.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class OrderStatusUpdateRequestDTO {

	@Pattern(regexp = "PENDING|SHIPPED|DELIVERED|CANCELLED", message = "Invalid status")
    @NotBlank(message = "Status must not be blank")
    private String status;

    // Constructors
    public OrderStatusUpdateRequestDTO() {
    }

    public OrderStatusUpdateRequestDTO(String status) {
        this.status = status;
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
