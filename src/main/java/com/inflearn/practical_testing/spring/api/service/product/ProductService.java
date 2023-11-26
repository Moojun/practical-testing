package com.inflearn.practical_testing.spring.api.service.product;

import com.inflearn.practical_testing.spring.api.service.product.response.ProductResponse;
import com.inflearn.practical_testing.spring.api.controller.product.dto.request.ProductCreateRequest;
import com.inflearn.practical_testing.spring.domain.product.Product;
import com.inflearn.practical_testing.spring.domain.product.ProductRepository;
import com.inflearn.practical_testing.spring.domain.product.ProductSellingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * readOnly = true : 읽기 전용
 * CRUD 에서 CUD 동작 X / Only Read
 * JPA : CUD 스냅샷 저장, 변경감지 X (성능 향상)
 * <p>
 * CQRS - Command(CUD) / Query 책임 분리
 * 일반적으로 Command와 Query(Read) 비중은 약 2:8 정도. Read가 훨씬 비중이 많다.
 * 책임을 분리해서 서로 연관이 없도록 설계
 * e.g) 사용자의 Read 작업이 몰렸는데 이로 인해 Command 작업이 영향을 받거나, 혹은 그 반대의 경우로 인해 서로 영향을 받으면 안된다.
 * 분리: Transaction(readOnly = true)를 신경써서 작성해야 한다.
 * 조회를 하는 서비스 따로, CUD 작업 서비스 따로 애플리케이션 레벨에서 명시적으로 분리를 할 수 있고,
 * 그리고 DB의 endpoint를 구분할 수 있다. 요즘 AWS DB를 쓰면 Read형 DB, Write형 DB를 나눠서 쓴다.
 * Master-slave DB 형식으로, Master DB는 Write, Replica인 Slave DB는 Read 전용으로 사용.
 * 결론으로 DB endpoint를 구분함으로써 장애 격리를 할 수 있다.
 * <p>
 * 추천하는 방법은, 누락 방지를 위해
 * Service class 상단에 '@Transactional(readOnly = true)' 를 설정하고,
 * CUD 작업이 있는 경우 메서드 단위에 '@Transactional' 을 설정한다.
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    // 동시성 이슈 발생 가능성 존재
    // case 1: 동시 접속자가 어느 정도 있는 경우: DB의 productNumber에 해당하는 column에 unique 제약 조건을 걸고,
    // 요청시에 이미 동일한 값이 있으면 시스템 내에서 자체적으로 3회 정도 재요청하도록 로직 구성
    // case 2: 동시 접속자가 너무 많은 경우: productNumber 자체에 대한 정책을 수정.
    // 지금처럼 증가하는 값이 아닌, UUID 등을 사용하면 동시성과 관련 없이 해결이 가능할 수 있다.
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        String nextProductNumber = createNextProductNumber();

        Product product = request.toEntity(nextProductNumber);
        Product savedProduct = productRepository.save(product);

        return ProductResponse.of(savedProduct);
    }

    //    @Transactional(readOnly = true)
    public List<ProductResponse> getSellingProducts() {
        List<Product> products = productRepository.findAllBySellingStatusIn(ProductSellingStatus.forDisplay());

        return products.stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    /**
     * productNumber
     * 001, 002, 003, ...
     * DB에서 마지막으로 저장된 Product의 상품번호를 읽어와서 +1 증가시킨다.
     */
    private String createNextProductNumber() {
        String latestProductNumber = productRepository.findLatestProductNumber();
        if (latestProductNumber == null) {
            return "001";
        }

        int latestProductNumberInt = Integer.parseInt(latestProductNumber);
        int nextProductNumberInt = latestProductNumberInt + 1;

        // 9 -> 009, 10 -> 010
        return String.format("%03d", nextProductNumberInt);
    }

}
