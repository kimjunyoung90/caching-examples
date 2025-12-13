package com.example.cachingexamples.common.repository;

import com.example.cachingexamples.common.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
