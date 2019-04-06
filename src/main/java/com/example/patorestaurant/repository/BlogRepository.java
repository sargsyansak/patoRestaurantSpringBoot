package com.example.patorestaurant.repository;

import com.example.patorestaurant.model.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogRepository extends JpaRepository<Blog, Integer> {
    List<Blog> findAllByNameContains(String name);

}


