package com.keanusantos.personalfinancemanager.domain.payment.dto.mapper;

import com.keanusantos.personalfinancemanager.domain.payment.Payment;
import com.keanusantos.personalfinancemanager.domain.payment.dto.response.ResponsePaymentDTO;

public class PaymentDTOMapper {

    public static ResponsePaymentDTO toResponse(Payment payment) {
        return new ResponsePaymentDTO(
                payment.getId(),
                payment.getMoment(),
                payment.getFinancialAccount().getId(),
                payment.getInstallment().getId()
        );
    }
}
