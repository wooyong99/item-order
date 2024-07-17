package com.example.payment.domain.payment.service;

import com.example.core.domain.payment.dto.PaymentValidateRequest;
import com.example.core.kafka.producer.StockDecreaseProducer;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class RedissonPaymentService implements PaymentService {

    private final IamportClient iamportClient;

    private final StockDecreaseProducer stockDecreaseProducer;

    @Override
    public void validatePayment(Long itemId, String merchantUid, String impUid, Long price) {
        IamportResponse<Payment> paymentIamportResponse = null;
        try {
            paymentIamportResponse = iamportClient.paymentByImpUid(
                impUid);

            if (paymentIamportResponse.getCode() != 0) {
                throw new IllegalArgumentException("결제 내역이 존재하지 않습니다.");
            }

            if (paymentIamportResponse.getResponse().getAmount().longValue()
                != price) {
                CancelData data = new CancelData(impUid, true);
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

        stockDecreaseProducer.send(itemId, merchantUid, impUid);
    }

    // 주문 결제 확인
    @Transactional
    public void validatePayment(String merchantUid, PaymentValidateRequest request) {
        IamportResponse<Payment> paymentIamportResponse = null;
        try {
            paymentIamportResponse = iamportClient.paymentByImpUid(
                request.getImpUid());

            if (paymentIamportResponse.getCode() != 0) {
                throw new IllegalArgumentException("결제 내역이 존재하지 않습니다.");
            }

            if (paymentIamportResponse.getResponse().getAmount().longValue()
                != request.getPrice()) {
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
    }

    public void canclePayment(String impUid) {
        CancelData data = new CancelData(impUid, true);
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
