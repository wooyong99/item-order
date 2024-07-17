package com.example.core.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestMessage {

    private Long itemId;

    private String merchantUid;

    private String impUid;

    private Long price;

}
