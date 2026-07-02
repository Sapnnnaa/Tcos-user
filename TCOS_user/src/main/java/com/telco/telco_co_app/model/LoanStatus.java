package com.telco.telco_co_app.model;

    public enum LoanStatus {

        PENDING,CHECKED,VERIFIED,APPROVED,REJECTED,PAID,FORWARD;
        public static LoanStatus getDefault() {
            return PENDING;
        }
    }
