package com.example.patorestaurant.repository;

import com.example.patorestaurant.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> {
    MenuItem findByName(String name);
}