package com.example.marketproject.domain.entity;

import org.hibernate.query.sql.internal.ParameterRecognizerImpl;

public enum PostStatus {
    ON_SALE("판매중"),
    RESERVED("예약중"),
    SOLD_OUT("판매완료");

    private final String description;

    PostStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
