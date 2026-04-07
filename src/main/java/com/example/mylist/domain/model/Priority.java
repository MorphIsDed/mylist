package com.example.mylist.domain.model;

public enum Priority {
    LOW,
    MEDIUM,
    HIGH;

    public static Priority from(String value) {
        if (value == null || value.isBlank()) {
            return MEDIUM;
        }
        return Priority.valueOf(value.trim().toUpperCase());
    }

    public String toApiValue() {
        return name().toLowerCase();
    }
}
