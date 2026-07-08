package com.keanusantos.personalfinancemanager.domain.payment.dto.mapper;

import com.keanusantos.personalfinancemanager.domain.financialaccount.dto.mapper.FinancialAccountDTOMapper;
import com.keanusantos.personalfinancemanager.domain.payment.Payment;
import com.keanusantos.personalfinancemanager.domain.payment.dto.response.ResponsePaymentDTO;
import com.keanusantos.personalfinancemanager.domain.payment.dto.summary.FinancialAccountPaymentSummaryDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.summary.FinancialAccountSummaryDTO;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class PaymentDTOMapper {

    public static ResponsePaymentDTO toResponse(Payment payment) {
        ZoneId fuso = ZoneId.of("America/Sao_Paulo");
        ZonedDateTime correctMoment = ZonedDateTime.ofInstant(payment.getMoment(), fuso);
        String formatted = correctMoment.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return new ResponsePaymentDTO(
                payment.getId(),
                formatted,
                new FinancialAccountPaymentSummaryDTO(payment.getFinancialAccount().getId(), payment.getFinancialAccount().getName()),
                payment.getInstallment().getId()
        );
    }
}
