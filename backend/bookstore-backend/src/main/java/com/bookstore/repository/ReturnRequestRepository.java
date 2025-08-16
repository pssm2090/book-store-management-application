package com.bookstore.repository;

import com.bookstore.entity.ReturnRequest;
import com.bookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Long> {
    List<ReturnRequest> findByUser(User user);
}