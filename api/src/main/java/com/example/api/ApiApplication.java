package com.example.api;

import com.example.api.domain.payment.service.PaymentService;
import com.example.api.domain.payment.service.RedissonPaymentService;
import com.example.core.domain.item.repository.ItemRepository;
import com.example.core.domain.order.repository.OrderRepository;
import com.siot.IamportRestClient.IamportClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "com.example")
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

    @Bean
    public PaymentService paymentService(IamportClient iamportClient,
        OrderRepository orderRepository, ItemRepository itemRepository) {
        return new RedissonPaymentService(iamportClient, orderRepository, itemRepository);
    }
}
