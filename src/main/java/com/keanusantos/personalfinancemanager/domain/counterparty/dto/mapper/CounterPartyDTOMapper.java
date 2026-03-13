package com.keanusantos.personalfinancemanager.domain.counterparty.dto.mapper;

import com.keanusantos.personalfinancemanager.domain.counterparty.Counterparty;
import com.keanusantos.personalfinancemanager.domain.counterparty.dto.request.CreateCounterPartyDTO;
import com.keanusantos.personalfinancemanager.domain.counterparty.dto.response.CounterPartyResponseDTO;
import com.keanusantos.personalfinancemanager.domain.user.User;

public class CounterPartyDTOMapper {

    public static CounterPartyResponseDTO toResponse(Counterparty counterParty) {
        return new CounterPartyResponseDTO(
          counterParty.getId(), counterParty.getName(), counterParty.getTaxId(), counterParty.getUser().getId()
        );
    }

    public static Counterparty toEntity(CreateCounterPartyDTO dto, User user) {
        return new Counterparty(
          null,
          dto.name(),
          dto.taxId(),
          user
        );
    }
}
