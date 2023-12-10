package com.fc.shimpyo_be.domain.product.repository;


import com.fc.shimpyo_be.domain.product.dto.request.SearchKeywordRequest;
import com.fc.shimpyo_be.domain.product.entity.Product;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;


public interface ProductRepository extends JpaRepository<Product, Long>,
    JpaSpecificationExecutor<Product> {


    @Query(value = "SELECT p FROM Product as p "
        + "JOIN FETCH p.rooms as r "
        + "JOIN FETCH p.address as addr "
        + "WHERE p.name LIKE %:#{#searchKeywordRequest.productName}% "
        + "AND addr.detailAddress LIKE %:#{#searchKeywordRequest.address}% "
        + "AND p.category in :#{#searchKeywordRequest.category} "
        + "AND r.capacity >= :#{#searchKeywordRequest.capacity}")
    Page<Product> findAll(@Param("searchKeywordRequest") SearchKeywordRequest searchKeywordRequest,
        Pageable pageable);


}
