package com.inflearn.practical_testing.spring.api.service.order;

import com.inflearn.practical_testing.spring.client.mail.MailSendClient;
import com.inflearn.practical_testing.spring.domain.history.mail.MailSendHistory;
import com.inflearn.practical_testing.spring.domain.history.mail.MailSendHistoryRepository;
import com.inflearn.practical_testing.spring.domain.order.Order;
import com.inflearn.practical_testing.spring.domain.order.OrderRepository;
import com.inflearn.practical_testing.spring.domain.order.OrderStatus;
import com.inflearn.practical_testing.spring.domain.orderproduct.OrderProductRepository;
import com.inflearn.practical_testing.spring.domain.product.Product;
import com.inflearn.practical_testing.spring.domain.product.ProductRepository;
import com.inflearn.practical_testing.spring.domain.product.ProductType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.inflearn.practical_testing.spring.domain.product.ProductSellingStatus.SELLING;
import static com.inflearn.practical_testing.spring.domain.product.ProductType.HANDMADE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class OrderStatisticsServiceTest {

    @Autowired
    private OrderStatisticsService orderStatisticsService;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MailSendHistoryRepository mailSendHistoryRepository;

    @MockBean
    private MailSendClient mailSendClient;

    @AfterEach
    void tearDown() {
        orderProductRepository.deleteAllInBatch();  // 얘를 먼저 지워야 한다.
        orderRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        mailSendHistoryRepository.deleteAllInBatch();
    }

    // OrderStatisticsService를 통합 테스트를 하는데,
    // 외부 네트워크를 타는 메일 전송(sendMail)이라는 로직 때문에
    // 테스트를 돌릴 때마다 우리가 계속 메일 전송을 해야 하는가?
    // 사실 우리가 이러한 로직 때문에 테스트 하기를 포기하거나
    // 테스트 메일 계정을 놓고, 테스트를 돌릴 때마다 실제로 메일이 전송되도록 세팅할 수도 있다.
    // 하지만 이렇게 하는 것은 시간적인 면이나 비용적인 면에서 번거롭다.
    // -> 이런 부분을 해결하기 위해 Mocking을 사용할 수 있다.
    @DisplayName("결제완료 주문들을 조회하여 매출 통계 메일을 전송한다.")
    @Test
    void sendOrderStatisticsMail() {
        // given
        LocalDateTime now = LocalDateTime.of(2023, 3, 5, 0, 0);

        Product product1 = createProduct(HANDMADE, "001", 1000);
        Product product2 = createProduct(HANDMADE, "002", 2000);
        Product product3 = createProduct(HANDMADE, "003", 3000);
        List<Product> products = List.of(product1, product2, product3);
        productRepository.saveAll(products);

        // 경계값 테스트
        // 3월 5일에 해당하는 결과를 원하므로, order2, order3 두 개가 해당되어야 한다.
        Order order1 = createPaymentCompletedOrder(LocalDateTime.of(2023, 3, 4, 23, 59, 59), products);
        Order order2 = createPaymentCompletedOrder(now, products);
        Order order3 = createPaymentCompletedOrder(LocalDateTime.of(2023, 3, 5, 23, 59, 59), products);
        Order order4 = createPaymentCompletedOrder(LocalDateTime.of(2023, 3, 6, 0, 0), products);

        // stubbing: Mock 객체에다가 우리가 원하는 행위를 정의한다.
        // Mail을 전송하는 중간 과정이 이 테스트 할 때는 필요없는 과정이고, 어떤 네트워크를 타거나 불필요한 과정이므로
        // Mock을 사용해서 테스트를 하였다.
        Mockito.when(mailSendClient.sendEmail(any(String.class), any(String.class), any(String.class), any(String.class)))
                .thenReturn(true);

        // when
        boolean result = orderStatisticsService.sendOrderStatisticsMail(LocalDate.of(2023, 3, 5), "test@test.com");

        // then
        assertThat(result).isTrue();

        List<MailSendHistory> histories = mailSendHistoryRepository.findAll();
        assertThat(histories).hasSize(1)
                .extracting("content")
                .contains("총 매출 합계는 12000원입니다.");
    }

    private Order createPaymentCompletedOrder(LocalDateTime now, List<Product> products) {
        Order order = Order.builder()
                .products(products)
                .orderStatus(OrderStatus.PAYMENT_COMPLETED)
                .registeredDateTime(now)
                .build();
        return orderRepository.save(order);
    }

    private Product createProduct(ProductType type, String productNumber, int price) {
        return Product.builder()
                .type(type)
                .productNumber(productNumber)
                .price(price)
                .sellingStatus(SELLING)
                .name("메뉴 이름")
                .build();
    }

}