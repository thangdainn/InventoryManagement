package com.thymeleaf.repository;

import com.thymeleaf.entity.InvoiceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface IInvoiceRepository extends JpaRepository<InvoiceEntity, Integer> {
    @Query("SELECT i FROM InvoiceEntity i INNER JOIN ProductInfoEntity p ON i.product.id = p.id WHERE p.name LIKE :keyword" +
            " AND i.type = :type")
    Page<InvoiceEntity> findByTypeAndProduct_NameContaining(@Param("keyword") String keyword, @Param("type") Integer type, Pageable pageable);

    @Query("SELECT i FROM InvoiceEntity i INNER JOIN ProductInfoEntity p ON i.product.id = p.id INNER JOIN CategoryEntity c ON p.category.id = c.id" +
            " WHERE (:cateCode IS NULL OR c.code = :cateCode) AND (:keyword IS NULL OR p.name LIKE :keyword)" +
            " AND (:type IS NULL OR i.type = :type)" +
            " AND (:fromDate IS NULL OR i.createdDate >= :fromDate)" +
            " AND (:toDate IS NULL OR i.createdDate <= :toDate)")
    Page<InvoiceEntity> findWithDynamicFilters(@Param("keyword") String keyword, @Param("cateCode") String cateCode,
                                               @Param("type") Integer type, @Param("fromDate") Timestamp fromDate,
                                               @Param("toDate") Timestamp toDate, Pageable pageable);

    Page<InvoiceEntity> findByType(Integer type ,Pageable pageable);
    List<InvoiceEntity> findByType(Integer type);
    Optional<InvoiceEntity> findByCode(String code);
}
