package com.example.payment;

import com.example.core.domain.item.repository.ItemRepository;
import com.example.core.domain.order.repository.OrderRepository;
import com.example.payment.domain.payment.service.PaymentService;
import com.example.payment.domain.payment.service.RedissonPaymentService;
import com.siot.IamportRestClient.IamportClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "com.example")
public class PaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }

    @Bean
    public PaymentService paymentService(IamportClient iamportClient,
        OrderRepository orderRepository, ItemRepository itemRepository) {
        return new RedissonPaymentService(iamportClient, orderRepository, itemRepository);
    }
}
