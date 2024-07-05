package com.example.api.domain.item.repository;

import com.example.core.domain.item.domain.Item;
import com.example.core.domain.item.repository.ItemRepository;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class StockConcurrencyTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    @DisplayName("재고 필드 동시성 테스트")
    @Disabled
    public void testStockConcurrency() throws InterruptedException {
        int threadCount = 100;

        ExecutorService executorService = Executors.newFixedThreadPool(32);

        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    getDecreaseStock(1L);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        Assertions.assertThat(itemRepository.findById(1L).get().getStock()).isEqualTo(0);
    }

    public void getDecreaseStock(Long itemId) {
        Item item = itemRepository.findById(itemId).get();
        item.decreaseStock();
        itemRepository.save(item);
    }
}
