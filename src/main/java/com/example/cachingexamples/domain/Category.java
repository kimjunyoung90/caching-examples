package com.example.cachingexamples.domain;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private Date lastUpdate;
}
