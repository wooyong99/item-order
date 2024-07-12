package com.example.api.domain.payment.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.example.api.domain.user.Util;
import com.example.core.domain.item.domain.Item;
import com.example.core.domain.item.repository.ItemRepository;
import com.example.core.domain.order.domain.Order;
import com.example.core.domain.order.repository.OrderRepository;
import com.example.core.domain.payment.dto.PaymentValidateRequest;
import com.example.core.domain.user.domain.User;
import com.example.core.exception.StockNegativeException;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DisplayName("DefaultPaymentService 테스트")
public class DefaultPaymentServiceTest {

    @InjectMocks
    DefaultPaymentService defaultPaymentService;
    @Mock
    OrderRepository orderRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    IamportClient iamportClient;
    @Mock
    IamportResponse<Payment> paymentIamportResponse;
    @Mock
    Order order;
    @Mock
    User user;
    @Mock
    Item item;
    @Mock
    Payment payment;

    @Test
    @DisplayName("결제 성공 테스트")
    public void shouldPurchaseSuccessfully() throws IamportResponseException, IOException {
        // given
        String merchantUid = "123";
        PaymentValidateRequest request = PaymentValidateRequest.builder().impUid("imp_uid").build();
        Long originStock = 100L;

        item = Util.createItem("name", originStock, 10000L);
        order = Order.builder().user(user).item(item).price(1000L).build();

        given(orderRepository.findByMerchantUid(merchantUid)).willReturn(
            Optional.ofNullable(order));
        given(itemRepository.findByIdWithPessimisticLock(any())).willReturn(
            Optional.ofNullable(item));
        given(iamportClient.paymentByImpUid(request.getImpUid())).willReturn(
            paymentIamportResponse);
        given(paymentIamportResponse.getResponse()).willReturn(payment);
        given(payment.getAmount()).willReturn(new BigDecimal(order.getPrice()));

        // when
        defaultPaymentService.validatePayment(merchantUid, request);

        // then
        Assertions.assertThat(item.getStock()).isEqualTo(originStock - 1);
    }

    @Test
    @DisplayName("결제 실패 테스트 - 재고 0 일 때")
    public void shouldPurchaseFail() throws IamportResponseException, IOException {
        // given
        String merchantUid = "123";
        PaymentValidateRequest request = PaymentValidateRequest.builder().impUid("imp_uid").build();
        Long originStock = 0L;

        item = Util.createItem("name", originStock, 10000L);
        order = Order.builder().user(user).item(item).price(1000L).build();

        given(orderRepository.findByMerchantUid(merchantUid)).willReturn(
            Optional.ofNullable(order));
        given(itemRepository.findByIdWithPessimisticLock(any())).willReturn(
            Optional.ofNullable(item));
        given(iamportClient.paymentByImpUid(request.getImpUid())).willReturn(
            paymentIamportResponse);
        given(paymentIamportResponse.getResponse()).willReturn(payment);
        given(payment.getAmount()).willReturn(new BigDecimal(order.getPrice()));

        // when

        // then
        Assertions.assertThatThrownBy(
                () -> defaultPaymentService.validatePayment(merchantUid, request))
            .isExactlyInstanceOf(StockNegativeException.class);
    }

    @Test
    @DisplayName("결제 실패 테스트 - 가격 불 일치")
    public void shouldThrowPurchasePriceIsInconsistency()
        throws IamportResponseException, IOException {
        // given
        String merchantUid = "123";
        PaymentValidateRequest request = PaymentValidateRequest.builder().impUid("imp_uid").build();
        Long originStock = 100L;

        item = Util.createItem("name", originStock, 10000L);
        order = Order.builder().user(user).item(item).price(1000L).build();

        given(orderRepository.findByMerchantUid(merchantUid)).willReturn(
            Optional.ofNullable(order));
        given(itemRepository.findByIdWithPessimisticLock(any())).willReturn(
            Optional.ofNullable(item));
        given(iamportClient.paymentByImpUid(request.getImpUid())).willReturn(
            paymentIamportResponse);
        given(paymentIamportResponse.getResponse()).willReturn(payment);
        given(payment.getAmount()).willReturn(
            new BigDecimal(order.getPrice() + 100));  // 요청 금액 = 주문 금액 + 100

        // when

        // then
        Assertions.assertThatThrownBy(
                () -> defaultPaymentService.validatePayment(merchantUid, request))
            .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("결제 실패 테스트 - 존재하지 않는 결제 내역")
    public void shouldThrowExceptionPaymentHistoryNegative()
        throws IamportResponseException, IOException {
        // given
        String merchantUid = "123";
        PaymentValidateRequest request = PaymentValidateRequest.builder().impUid("imp_uid").build();
        Long originStock = 100L;

        item = Util.createItem("name", originStock, 10000L);
        order = Order.builder().user(user).item(item).price(1000L).build();

        given(orderRepository.findByMerchantUid(merchantUid)).willReturn(
            Optional.ofNullable(order));
        given(itemRepository.findByIdWithPessimisticLock(any())).willReturn(
            Optional.ofNullable(item));
        given(iamportClient.paymentByImpUid(request.getImpUid())).willReturn(
            paymentIamportResponse);
        given(paymentIamportResponse.getResponse()).willReturn(payment);
        given(paymentIamportResponse.getCode()).willReturn(1);
        given(payment.getAmount()).willReturn(new BigDecimal(order.getPrice() - 100));

        // when

        // then
        Assertions.assertThatThrownBy(
                () -> defaultPaymentService.validatePayment(merchantUid, request))
            .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("결제 실패 테스트 - 존재하지 않는 주문번호")
    public void shouldThrowExceptionMerchantUidNegative() {
        // given
        String merchantUid = "123";
        PaymentValidateRequest request = PaymentValidateRequest.builder().impUid("imp_uid").build();
        Long originStock = 100L;

        item = Util.createItem("name", originStock, 10000L);
        order = Order.builder().user(user).item(item).price(1000L).build();

        given(orderRepository.findByMerchantUid("not merchantUid")).willReturn(
            Optional.ofNullable(order));    // 다른 merchantUid 값 입력

        // when

        // then
        Assertions.assertThatThrownBy(
                () -> defaultPaymentService.validatePayment(merchantUid, request))
            .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("결제 실패 테스트 - 존재하지 않는 아이템")
    public void shouldThrowExceptionItemNegative() {
        // given
        String merchantUid = "123";
        PaymentValidateRequest request = PaymentValidateRequest.builder().impUid("imp_uid").build();
        Long originStock = 100L;

        item = Util.createItem("name", originStock, 10000L);
        order = Order.builder().user(user).item(item).price(1000L).build();

        given(orderRepository.findByMerchantUid("not merchantUid")).willReturn(
            Optional.ofNullable(order));
        given(itemRepository.findByIdWithPessimisticLock(any())).willReturn(
            null);  // null 값 리턴

        // when

        // then
        Assertions.assertThatThrownBy(
                () -> defaultPaymentService.validatePayment(merchantUid, request))
            .isExactlyInstanceOf(IllegalArgumentException.class);
    }

}
