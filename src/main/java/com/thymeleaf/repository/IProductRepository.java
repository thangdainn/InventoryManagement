package com.thymeleaf.repository;

import com.thymeleaf.entity.ProductInfoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface IProductRepository extends JpaRepository<ProductInfoEntity, Integer> {
    Page<ProductInfoEntity> findAllByActiveFlag(Integer active_flag, Pageable pageable);

    List<ProductInfoEntity> findAllByActiveFlag(Integer active_flag);
    @Modifying
    @Transactional
    @Query("UPDATE ProductInfoEntity p SET p.activeFlag = 0 WHERE p.id IN :ids")
    void deleteAllByIdInBatchCustom(@Param("ids") List<Integer> ids);
    List<ProductInfoEntity> findByNameContaining(String keyword);
    Page<ProductInfoEntity> findByNameContainingAndActiveFlag(String keyword, Integer active_flag, Pageable pageable);
    Optional<ProductInfoEntity> findByCode(String code);

    @Query("SELECT p FROM ProductInfoEntity p INNER JOIN CategoryEntity c ON p.category.id = c.id" +
            " WHERE (:cateCode IS NULL OR c.code = :cateCode) AND (:keyword IS NULL OR p.name LIKE :keyword) AND p.activeFlag = :activeFlag")
    Page<ProductInfoEntity> findWithDynamicFilters(@Param("keyword") String keyword, @Param("cateCode") String cateCode, @Param("activeFlag") Integer activeFlag, Pageable pageable);
    @Query("SELECT p FROM ProductInfoEntity p INNER JOIN ProductInStokeEntity ps ON p.id = ps.product.id")
    List<ProductInfoEntity> findByProductInStoke();
}
