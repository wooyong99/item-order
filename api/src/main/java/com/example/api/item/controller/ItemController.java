package com.example.api.item.controller;

import com.example.api.domain.item.service.ItemService;
import com.example.core.domain.item.dto.ItemCreateRequest;
import com.example.core.domain.item.dto.ItemDetailResponse;
import com.example.core.domain.user.domain.UserRole;
import com.example.core.security.aop.AuthorizationRequired;
import io.swagger.v3.oas.annotations.Operation;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @Operation(summary = "아이템 저장")
    @AuthorizationRequired({UserRole.GENERAL})
    public ResponseEntity saveItem(@RequestBody ItemCreateRequest request) {
        Long itemId = itemService.save(request);

        return ResponseEntity.created(URI.create("/items/" + itemId)).build();
    }

    @Operation(summary = "아이템 단건 조회")
    @GetMapping("/{itemId}")
    public ResponseEntity detailItem(@PathVariable Long itemId) {
        ItemDetailResponse response = itemService.findItem(itemId);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "아이템 리스트 조회")
    public ResponseEntity listItem(@PageableDefault Pageable pageable) {
        List<ItemDetailResponse> response = itemService.findItemList(pageable);

        return ResponseEntity.ok(response);
    }
}
