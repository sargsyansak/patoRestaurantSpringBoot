package com.example.patorestaurant.repository;

import com.example.patorestaurant.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<Email, Integer> {
}
