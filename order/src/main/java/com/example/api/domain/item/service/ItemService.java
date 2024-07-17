package com.example.api.domain.item.service;


import com.example.core.aop.RedissonLock;
import com.example.core.domain.item.domain.Item;
import com.example.core.domain.item.dto.ItemCreateRequest;
import com.example.core.domain.item.dto.ItemDetailResponse;
import com.example.core.domain.item.repository.ItemRepository;
import com.example.core.kafka.producer.StatusSuccessProducer;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final StatusSuccessProducer statusSuccessProducer;

    @Transactional
    public Long save(ItemCreateRequest request) {
        Item item = request.toEntity();
        Item saveItem = itemRepository.save(item);
        return saveItem.getId();
    }

    @Transactional
    public ItemDetailResponse findItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));
        ItemDetailResponse response = toResponseDto(item);
        return response;
    }

    @Transactional
    public List<ItemDetailResponse> findItemList(Pageable pageable) {
        return itemRepository.findAll(pageable).stream().map(item -> toResponseDto(item)).toList();
    }

    @RedissonLock(value = "#itemId")
    public void decreaseStock(Long itemId, String merchantUid) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이템입니다."));
        item.decreaseStock();
        itemRepository.save(item);
        statusSuccessProducer.send(merchantUid);        // 주문 상태 성공 이벤트 발행
    }

    public ItemDetailResponse toResponseDto(Item item) {
        return ItemDetailResponse.builder()
            .itemId(item.getId())
            .name(item.getName())
            .price(item.getPrice())
            .stock(item.getStock())
            .createAt(item.getCreatedAt())
            .build();
    }
}
