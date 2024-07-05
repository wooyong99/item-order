package com.example.api.domain.item.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDetailResponse {

    private Long itemId;

    private String name;

    private Long price;

    private Long stock;

    private LocalDateTime createAt;
}
