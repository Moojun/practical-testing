package com.inflearn.practical_testing.spring.domain.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * select *
     * from product
     * where selling_status in ('SELLING', 'HOLD');
     */
    List<Product> findAllBySellingStatusIn(List<ProductSellingStatus> sellingStatuses);

    List<Product> findAllByProductNumberIn(List<String> productNumbers);

    // 이 부분에서, Query 메서드를 사용하거나, native query를 사용해서 작성하거나 혹은 QueryDSL을 사용해서 작성하던가에 관계없이,
    // 즉, 리포지토리의 구현 내용에 관계없이 우리는 테스트 코드를 작성해야 한다.
    @Query(value = "select p.product_number from product p order by id desc limit 1", nativeQuery = true)
    String findLatestProductNumber();
}