package com.inflearn.practical_testing.spring.api.service.order;

import com.inflearn.practical_testing.spring.api.controller.order.request.OrderCreateRequest;
import com.inflearn.practical_testing.spring.api.service.order.response.OrderResponse;
import com.inflearn.practical_testing.spring.domain.order.Order;
import com.inflearn.practical_testing.spring.domain.order.OrderRepository;
import com.inflearn.practical_testing.spring.domain.product.Product;
import com.inflearn.practical_testing.spring.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public OrderResponse createOrder(OrderCreateRequest request, LocalDateTime registeredDateTime) {
        List<String> productNumbers = request.getProductNumbers();
        List<Product> products = productRepository.findAllByProductNumberIn(productNumbers);

        Order order = Order.create(products, registeredDateTime);
        Order savedOrder = orderRepository.save(order);
        return OrderResponse.of(savedOrder);
    }

}