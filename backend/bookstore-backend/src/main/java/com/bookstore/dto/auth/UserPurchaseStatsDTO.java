package com.bookstore.dto.auth;

import java.math.BigDecimal;

public class UserPurchaseStatsDTO {
    private String email;
    private Long totalOrders;
    private BigDecimal totalSpent;

    public UserPurchaseStatsDTO() {
    }

    public UserPurchaseStatsDTO(String email, Long totalOrders, BigDecimal totalSpent) {
        this.email = email;
        this.totalOrders = totalOrders;
        this.totalSpent = totalSpent;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }

    @Override
    public String toString() {
        return "UserPurchaseStatsDTO{" +
                "email='" + email + '\'' +
                ", totalOrders=" + totalOrders +
                ", totalSpent=" + totalSpent +
                '}';
    }
}
