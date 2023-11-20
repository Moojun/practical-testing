package com.inflearn.practical_testing.spring.api.service.order;

import com.inflearn.practical_testing.spring.api.controller.order.request.OrderCreateRequest;
import com.inflearn.practical_testing.spring.api.service.order.response.OrderResponse;
import com.inflearn.practical_testing.spring.domain.Stock.Stock;
import com.inflearn.practical_testing.spring.domain.Stock.StockRepository;
import com.inflearn.practical_testing.spring.domain.order.Order;
import com.inflearn.practical_testing.spring.domain.order.OrderRepository;
import com.inflearn.practical_testing.spring.domain.product.Product;
import com.inflearn.practical_testing.spring.domain.product.ProductRepository;
import com.inflearn.practical_testing.spring.domain.product.ProductType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final StockRepository stockRepository;

    /**
     * 실제로 재고 감소는 단순한 문제가 아니다 -> 동시성 고민
     * e.g) DB에 재고가 5개 있는데, 두 사람이 동시에 주문을 넣은 경우 각각 5개의 재고를 읽어갈 텐데,
     * 각자 차감을 하고 각자 반영을 한다. 어떻게 반영을 할 것인가? 어떤 데이터가 더 우선인 것인가?
     * Optimistic Lock / pessimistic lock / ... 개념을 사용함.
     */
    public OrderResponse createOrder(OrderCreateRequest request, LocalDateTime registeredDateTime) {
        List<String> productNumbers = request.getProductNumbers();
        List<Product> products = findProductsBy(productNumbers);

        deductStockQuantities(products);

        Order order = Order.create(products, registeredDateTime);
        Order savedOrder = orderRepository.save(order);
        return OrderResponse.of(savedOrder);
    }

    private void deductStockQuantities(List<Product> products) {
        // 재고 차감 체크가 필요한 상품들 Filter
        List<String> stockProductNumbers = extractStockProductNumbers(products);

        // 재고 엔티티 조회
        Map<String, Stock> stockMap = createStockMapBy(stockProductNumbers);
        // 상품별 counting
        Map<String, Long> productCountingMap = createCountingMapBy(stockProductNumbers);

        // 재고 차감 시도
        for (String stockProductNumber : new HashSet<>(stockProductNumbers)) {
            Stock stock = stockMap.get(stockProductNumber);
            int quantity = productCountingMap.get(stockProductNumber).intValue();

            // 질문
            // 여기서 이미 재고 체크를 하는데,
            if (stock.isQuantityLessThan(quantity)) {
                throw new IllegalArgumentException("재고가 부족한 상품이 있습니다.");
            }
            // 여기서 또 재고 체크를 할 필요가 있을까?
            // 관점의 문제. 서비스에서 체크 후 예외를 던지는 것과 여기서의 체크 후 예외를 던지는 것은 아주 다른 상황임.
            // 같은 상황이지만 어쨌든 발생할 수 있는 상황이 다르다.
            stock.deductQuantity(quantity);
        }
    }

    private List<Product> findProductsBy(List<String> productNumbers) {
        List<Product> products = productRepository.findAllByProductNumberIn(productNumbers);
        Map<String, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getProductNumber, p -> p));

        return productNumbers.stream()
                .map(productMap::get)
                .collect(Collectors.toList());
    }

    private static List<String> extractStockProductNumbers(List<Product> products) {
        return products.stream()
                .filter(product -> ProductType.containsStockType(product.getType()))
                .map(Product::getProductNumber)
                .collect(Collectors.toList());
    }

    private Map<String, Stock> createStockMapBy(List<String> stockProductNumbers) {
        List<Stock> stocks = stockRepository.findAllByProductNumberIn(stockProductNumbers);
        return stocks.stream()
                .collect(Collectors.toMap(Stock::getProductNumber, s -> s));
    }

    private static Map<String, Long> createCountingMapBy(List<String> stockProductNumbers) {
        return stockProductNumbers.stream()
                .collect(Collectors.groupingBy(p -> p, Collectors.counting()));
    }

}