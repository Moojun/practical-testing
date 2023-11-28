package com.inflearn.practical_testing.spring.api.service.order;

import com.inflearn.practical_testing.spring.api.service.mail.MailService;
import com.inflearn.practical_testing.spring.domain.order.Order;
import com.inflearn.practical_testing.spring.domain.order.OrderRepository;
import com.inflearn.practical_testing.spring.domain.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 메일을 전송하는 로직에는 @Transactional 을 붙이지 않는게 좋다.
 * 트랜잭션을 가지고 DB 조회를 orderRepository.findOrdersBy 에서 할 때 커넥션의 자원을 계속 소유하고 있는다.
 * 트랜잭션이 달리면 트랜잭션이 끝나기 전까지 커넥션을 계속 가지고 있을 텐데
 * 이런 긴 작업이 있는 이런 서비스에는 트랜잭션을 걸지 않는게 좋다.
 * <p>
 * orderRepository.findOrdersBy 에서 조회할 때도 조회용 트랜잭션이 따로 리포지토리 단에서 걸릴 것이므로
 * 이런 서비스에는 걸지 않는 것이 좋다.
 */
@RequiredArgsConstructor
@Service
public class OrderStatisticsService {

    private final OrderRepository orderRepository;
    private final MailService mailService;

    public boolean sendOrderStatisticsMail(LocalDate orderDate, String email) {
        // 해당 일자에 결제완료된 주문들을 가져와서(하루치 주문을 가져오고 싶다)
        List<Order> orders = orderRepository.findOrdersBy(
                orderDate.atStartOfDay(),
                orderDate.plusDays(1).atStartOfDay(),
                OrderStatus.PAYMENT_COMPLETED
        );

        // 총 매출 합계를 계산하고
        int totalAmount = orders.stream()
                .mapToInt(Order::getTotalPrice)
                .sum();

        // 메일 전송
        boolean result = mailService.sendMail(
                "no-reply@cafekiosk.com",
                email,
                String.format("[매출통계] %s", orderDate),
                String.format("총 매출 합계는 %s원입니다.", totalAmount)
        );

        if (!result) {
            throw new IllegalArgumentException("매출 통계 메일 전송에 실패했습니다.");
        }

        return true;
    }

}
