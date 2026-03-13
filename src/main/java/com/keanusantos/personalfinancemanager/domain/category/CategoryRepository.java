package com.keanusantos.personalfinancemanager.domain.category;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameAndUserIdAndIdNot(String name, Long userId, Long id);
    boolean existsByColorAndUserIdAndIdNot(String color, Long userId, Long id);
    boolean existsByNameAndUserId(String name,  Long id);
    boolean existsByColorAndUserId(String color, Long id);
    Set<Category> findAllByIdInAndUser_id(Set<Long> ids, Long user);
    Set<Category> findAllByUser_id(Long id);
    Optional<Category> findByIdAndUser_Id(Long id, Long user);
    boolean existsByIdAndUserId(Long id, Long userId);
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM transaction_category WHERE category_id = :id", nativeQuery = true)
    void deleteReferencesById(@Param("id") Long id);
}
