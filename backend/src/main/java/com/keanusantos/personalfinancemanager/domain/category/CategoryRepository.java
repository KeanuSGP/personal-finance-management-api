package com.keanusantos.personalfinancemanager.domain.category;

import com.keanusantos.personalfinancemanager.domain.dashboard.summary.DashboardCategorySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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
    @Query(value ="select category_name AS categoryName, SUM(i.amount) AS total, color_hex AS color from categories c join transactions_categories tc on c.id = tc.category_id join transactions t on t.id = tc.transaction_id join installments i on i.transaction_id = t.id where c.user_id = :userId AND t.transaction_type = 'CREDIT' AND i.installment_status = 'PAID' GROUP BY c.category_name, c.color_hex LIMIT 5", nativeQuery = true)
    List<DashboardCategorySummary> sumTop5RevenueCategoryByUserId(@Param("userId") Long userId);
    @Query(value ="select category_name AS categoryName, SUM(i.amount) AS total, color_hex AS color from categories c join transactions_categories tc on c.id = tc.category_id join transactions t on t.id = tc.transaction_id join installments i on i.transaction_id = t.id where c.user_id = :userId AND t.transaction_type = 'DEBIT' AND i.installment_status = 'PAID' GROUP BY c.category_name, c.color_hex LIMIT 5", nativeQuery = true)
    List<DashboardCategorySummary> sumTop5ExpenseCategoryByUserId(@Param("userId") Long userId);
}
