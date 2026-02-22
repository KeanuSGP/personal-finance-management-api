package com.keanusantos.personalfinancemanager.domain.category.dto.mapper;

import com.keanusantos.personalfinancemanager.domain.category.Category;
import com.keanusantos.personalfinancemanager.domain.category.dto.request.CreateCategoryDTO;
import com.keanusantos.personalfinancemanager.domain.category.dto.response.CategoryResponseDTO;
import com.keanusantos.personalfinancemanager.domain.category.dto.summary.CategorySummaryDTO;
import com.keanusantos.personalfinancemanager.domain.user.User;

public class CategoryDTOMapper {
    public static CategoryResponseDTO toResponseDTO(Category category) {
      return new CategoryResponseDTO(
              category.getId(),
              category.getName(),
              category.getColor(),
              category.getUser().getId()
      );
    }

    public static CategorySummaryDTO toSummaryDTO(Category category) {
        return new CategorySummaryDTO(
                category.getId(), category.getName()
        );
    }

    public static Category toEntity(CreateCategoryDTO dto, User user) {
        return new Category(
                null,
                dto.name(),
                dto.color(),
                user
        );
    }
}
