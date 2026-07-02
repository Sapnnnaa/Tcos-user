package com.telco.telco_co_app.model;

public enum AccountStatus {
    PENDING,
    ACTIVE,
    CLOSED;

    public static AccountStatus getDefault() {
        return PENDING;
    }
}