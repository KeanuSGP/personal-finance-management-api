package com.keanusantos.personalfinancemanager.domain.payment.dto.mapper;

import com.keanusantos.personalfinancemanager.domain.payment.Payment;
import com.keanusantos.personalfinancemanager.domain.payment.dto.response.ResponsePaymentDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.Transaction;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class PaymentDTOMapper {

    public static ResponsePaymentDTO toResponse(Payment payment, Transaction transaction) {
        ZoneId fuso = ZoneId.of("America/Sao_Paulo");
        ZonedDateTime correctMoment = ZonedDateTime.ofInstant(payment.getMoment(), fuso);
        String formatted = correctMoment.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return new ResponsePaymentDTO(
                payment.getId(),
                formatted,
                transaction.getId(),
                transaction.getDoc(),
                payment.getFinancialAccount().getId(),
                payment.getFinancialAccount().getName(),
                payment.getInstallment().getId(),
                payment.getInstallment().getInstallmentNumber(),
                payment.getInstallment().getAmount()
        );
    }
}
