package com.keanusantos.personalfinancemanager.domain.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameAndIdNot(String name, Long id);
    boolean existsByColorAndIdNot(String color, Long id);
    boolean existsByName(String name);
    boolean existsByColor(String color);
}
