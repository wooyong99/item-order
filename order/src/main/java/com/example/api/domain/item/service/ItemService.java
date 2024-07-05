package com.example.api.domain.item.service;


import com.example.api.domain.item.dto.ItemCreateRequest;
import com.example.api.domain.item.dto.ItemDetailResponse;
import com.example.core.domain.item.domain.Item;
import com.example.core.domain.item.repository.ItemRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public Long save(ItemCreateRequest request) {
        Item item = request.toEntity();
        Item saveItem = itemRepository.save(item);
        return saveItem.getId();
    }

    public ItemDetailResponse findItem(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));
        ItemDetailResponse response = toResponseDto(item);
        return response;
    }

    public List<ItemDetailResponse> findItemList(Pageable pageable) {
        return itemRepository.findAll(pageable).stream().map(item -> toResponseDto(item)).toList();
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