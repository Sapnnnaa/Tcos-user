package com.telco.telco_co_app.model;

public enum Paymenttype {
	NONE,
    ADVICE,
    NEFT,
    CASH,
    CHEQUE,
    ONLINE,
    CREDIT,
    DEBIT,
    NA,
    Recovery,
    Receipt,
    Guarantor;
    public static Paymenttype getDefault() {
        return NONE;
    }

        
}
