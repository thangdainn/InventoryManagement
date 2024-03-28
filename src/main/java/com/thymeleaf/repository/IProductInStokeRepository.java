package com.thymeleaf.repository;

import com.thymeleaf.entity.ProductInStokeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IProductInStokeRepository extends JpaRepository<ProductInStokeEntity, Integer> {
    @Query("SELECT ps FROM ProductInStokeEntity ps INNER JOIN ProductInfoEntity p ON ps.product.id = p.id WHERE p.name LIKE :keyword")
    Page<ProductInStokeEntity> findByProduct_NameContaining(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT ps FROM ProductInStokeEntity ps INNER JOIN ProductInfoEntity p ON ps.product.id = p.id INNER JOIN CategoryEntity c ON p.category.id = c.id" +
            " WHERE (:cateCode IS NULL OR c.code = :cateCode) AND (:keyword IS NULL OR p.name LIKE :keyword)")
    Page<ProductInStokeEntity> findWithDynamicFilters(@Param("keyword") String keyword, @Param("cateCode") String cateCode, Pageable pageable);

    Optional<ProductInStokeEntity> findByProduct_Id(Integer id);
}
