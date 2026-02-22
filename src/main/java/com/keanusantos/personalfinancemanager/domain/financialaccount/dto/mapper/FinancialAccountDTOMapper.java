package com.keanusantos.personalfinancemanager.domain.financialaccount.dto.mapper;

import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccount;
import com.keanusantos.personalfinancemanager.domain.financialaccount.dto.request.CreateAccountDTO;
import com.keanusantos.personalfinancemanager.domain.financialaccount.dto.request.PutAccountDTO;
import com.keanusantos.personalfinancemanager.domain.financialaccount.dto.response.FinancialAccountResponseDTO;
import com.keanusantos.personalfinancemanager.domain.user.User;
import com.keanusantos.personalfinancemanager.domain.user.dto.mapper.UserDTOMapper;

public class FinancialAccountDTOMapper {

    public static FinancialAccountResponseDTO toFinancialAccountResponseDTO(FinancialAccount financialAccount) {
        return new FinancialAccountResponseDTO(
                financialAccount.getId(), financialAccount.getName(), financialAccount.getBalance(), financialAccount.getUser().getId()
        );
    }

    public static FinancialAccount toEntity(CreateAccountDTO dto, User user) {
        return new FinancialAccount(
                null,
                dto.name(),
                dto.balance(),
                user
        );
    }

}
