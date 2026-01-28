package com.keanusantos.personalfinancemanager.domain.financialaccount.exception;

public class FinancialAccountAlreadyExists extends RuntimeException{
    public FinancialAccountAlreadyExists() {
        super("An account with that name already exists");
    }
}
