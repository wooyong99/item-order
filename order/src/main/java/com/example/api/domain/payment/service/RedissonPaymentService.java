package com.example.api.domain.payment.service;

import com.example.core.aop.RedissonLock;
import com.example.core.domain.item.domain.Item;
import com.example.core.domain.item.repository.ItemRepository;
import com.example.core.domain.order.domain.Order;
import com.example.core.domain.order.repository.OrderRepository;
import com.example.core.domain.payment.dto.PaymentValidateRequest;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import java.io.IOException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RedissonPaymentService implements PaymentService {

    private final IamportClient iamportClient;

    private final OrderRepository orderRepository;

    private final ItemRepository itemRepository;

    // 주문 결제 확인
    @RedissonLock(value = "#request.getItemId().toString()")
    public void validatePayment(String merchantUid, PaymentValidateRequest request) {
        Order order = orderRepository.findByMerchantUid(merchantUid)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문번호입니다."));

        IamportResponse<Payment> paymentIamportResponse = null;
        try {
            paymentIamportResponse = iamportClient.paymentByImpUid(
                request.getImpUid());

            if (paymentIamportResponse.getCode() != 0) {
                throw new IllegalArgumentException("결제 내역이 존재하지 않습니다.");
            }
            if (request.getItemId() != order.getItem().getId()) {
                CancelData data = new CancelData(request.getImpUid(), true);
                IamportResponse<Payment> response = iamportClient.cancelPaymentByImpUid(data);
                throw new IllegalArgumentException("주문 상품과 결제 상품이 일치하지 않습니다.");
            }

            if (paymentIamportResponse.getResponse().getAmount().longValue() != order.getPrice()) {
                CancelData data = new CancelData(request.getImpUid(), true);
                IamportResponse<Payment> response = iamportClient.cancelPaymentByImpUid(data);
                throw new IllegalArgumentException("결제 금액이 일치하지 않습니다.");
            }
        } catch (IamportResponseException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("결제 내역이 존재하지 않습니다.");
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("결제 내역이 존재하지 않습니다.");
        }

        Item item = itemRepository.findById(request.getItemId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이템입니다."));
        item.decreaseStock();
        itemRepository.save(item);
//        order.increaseStatus();
//        order.setImpUid(request.getImpUid());
//        orderRepository.save(order);
    }

//    @Transactional
//
//    public void decreaseStock(Long itemId) {
//        Item item = itemRepository.findById(itemId)
//            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문번호입니다."));
//        item.decreaseStock();
//        itemRepository.save(item);
//    }

    public void canclePayment(String merchantUid) {
        CancelData data = new CancelData(merchantUid, true);
        try {
            // impuid로 수정 예정
            IamportResponse<Payment> response = iamportClient.cancelPaymentByImpUid(data);
        } catch (IamportResponseException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }
    }

}
