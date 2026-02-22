package com.keanusantos.personalfinancemanager.domain.counterparty.dto.mapper;

import com.keanusantos.personalfinancemanager.domain.counterparty.CounterParty;
import com.keanusantos.personalfinancemanager.domain.counterparty.dto.request.CreateCounterPartyDTO;
import com.keanusantos.personalfinancemanager.domain.counterparty.dto.request.PutCounterPartyDTO;
import com.keanusantos.personalfinancemanager.domain.counterparty.dto.response.CounterPartyResponseDTO;
import com.keanusantos.personalfinancemanager.domain.user.User;

public class CounterPartyResponseDTOMapper {

    public static CounterPartyResponseDTO toResponse(CounterParty counterParty) {
        return new CounterPartyResponseDTO(
          counterParty.getId(), counterParty.getName(), counterParty.getTaxId(), counterParty.getUser().getId()
        );
    }

    public static CounterParty toEntity(CreateCounterPartyDTO dto, User user) {
        return new CounterParty(
          null,
          dto.name(),
          dto.taxId(),
          user
        );
    }
}
