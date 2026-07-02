package com.telco.telco_co_app.model;

public enum AccountSettlement {

    PENDING,APPLIED,APPROVED,REJECTED;

    public static AccountSettlement getDefault() {
        return PENDING;
    }
}
