package com.example.cachingexamples.controller;

import com.example.cachingexamples.domain.Category;
import com.example.cachingexamples.service.CategoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories(@RequestParam(name = "mode", required = false, defaultValue = "db") String mode) throws JsonProcessingException {
        switch (mode) {
            case "local":
                return ResponseEntity.ok(categoryService.getAllCategoriesWithLocalCache());
//            case "redis":
//                return ResponseEntity.ok(categoryService.getAllCategoriesWithRedis());
            default:
                return ResponseEntity.ok(categoryService.getAllCategories());
        }
    }
}
