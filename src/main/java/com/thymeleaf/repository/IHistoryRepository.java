package com.thymeleaf.repository;

import com.thymeleaf.entity.HistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IHistoryRepository extends JpaRepository<HistoryEntity, Integer> {
    @Query("SELECT h FROM HistoryEntity h INNER JOIN ProductInfoEntity p ON h.product.id = p.id WHERE p.name LIKE :keyword")
    Page<HistoryEntity> findByProduct_NameContaining(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT h FROM HistoryEntity h INNER JOIN ProductInfoEntity p ON h.product.id = p.id INNER JOIN CategoryEntity c ON p.category.id = c.id" +
            " WHERE (:cateCode IS NULL OR c.code = :cateCode) AND (:keyword IS NULL OR p.name LIKE :keyword)" +
            " AND (:type IS NULL OR h.type = :type)")
    Page<HistoryEntity> findWithDynamicFilters(@Param("keyword") String keyword, @Param("cateCode") String cateCode,
                                               @Param("type") Integer type ,Pageable pageable);

}
