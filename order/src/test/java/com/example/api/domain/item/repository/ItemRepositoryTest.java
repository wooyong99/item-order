package com.example.api.domain.item.repository;

import static com.example.api.domain.user.Util.createItem;

import com.example.api.domain.user.Util;
import com.example.core.db.config.QueryDslConfig;
import com.example.core.domain.item.domain.Item;
import com.example.core.domain.item.repository.ItemRepository;
import com.example.core.domain.user.domain.User;
import com.example.core.exception.StockNegativeException;
import jakarta.persistence.EntityManager;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.InvalidDataAccessApiUsageException;

@DataJpaTest
@Import({QueryDslConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("OrderRepositoryTest 테스트")
@Slf4j
public class ItemRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    ItemRepository itemRepository;

    User user;

    @BeforeEach
    void setUp() {
        user = Util.createUser("email", "password", "nickname");
    }

    @Test
    @DisplayName("Item 데이터가 저장되는지 확인합니다.")
    public void shouldPersistItemSuccessfully() {
        // given
        Item item = createItem("name", 1000L, 10000L);

        // when
        itemRepository.save(item);

        // then
        Assertions.assertThat(itemRepository.findById(item.getId()).get()).isEqualTo(item);
    }

    @Test
    @DisplayName("Item 데이터가 삭제되는지 확인합니다.")
    public void shouldDeleteItemSuccessfully() {
        // given
        Item item = createItem("name", 1000L, 10000L);
        itemRepository.save(item);
        em.flush();

        // when
        itemRepository.deleteById(item.getId());
        em.flush();
        em.clear();

        // then
        Assertions.assertThat(em.contains(item)).isFalse();
    }

    @Test
    @DisplayName("가격과 재고가 0 미만이면 예외를 던집니다.")
    public void shouldThrowExceptionPriceAndStockIsZeroAndNegative() {
        // given
        Item i1 = createItem("name", -1L, 10000L);
        Item i2 = createItem("name", 1000L, -1L);

        // when
        Assertions.assertThatThrownBy(() -> itemRepository.save(i1)).isExactlyInstanceOf(
            InvalidDataAccessApiUsageException.class);
        Assertions.assertThatThrownBy(() -> itemRepository.save(i2)).isExactlyInstanceOf(
            InvalidDataAccessApiUsageException.class);
    }

    @Test
    @DisplayName("상품 이름이 공백이거나 null 이면 예외를 던집니다.")
    public void shouldThrowExceptionNameIsNullAndEmpty() {
        // given
        Item i1 = createItem(null, -1L, 10000L);
        Item i2 = createItem("", -1L, 10000L);

        // when
        Assertions.assertThatThrownBy(() -> itemRepository.save(i1)).isExactlyInstanceOf(
            InvalidDataAccessApiUsageException.class);
        Assertions.assertThatThrownBy(() -> itemRepository.save(i2)).isExactlyInstanceOf(
            InvalidDataAccessApiUsageException.class);
    }

    @Test
    @DisplayName("재고가 정상적으로 감소되는지 확인합니다.")
    public void shouldDecreaseStockSuccessfully() {
        // given
        long originStock = 1000;
        int decreaseCount = 10;
        Item item = createItem("name", originStock, 1000L);
        itemRepository.save(item);

        // when
        getDecreaseStock(decreaseCount, item.getId());

        // then
        Assertions.assertThat(item.getStock()).isEqualTo(originStock - decreaseCount);
    }

    @Test
    @DisplayName("재고 감소 시 0 미만이면 예외를 던집니다.")
    public void shouldThrowExceptionDecreaseStockNegative() {
        // given
        long originStock = 5;
        Item item = createItem("name", originStock, 1000L);
        itemRepository.save(item);

        // when
        Assertions.assertThatThrownBy(() -> getDecreaseStock(10, item.getId())).isExactlyInstanceOf(
            StockNegativeException.class);
    }

    public void getDecreaseStock(int index, Long itemId) {
        Item item = itemRepository.findById(itemId).get();
        IntStream.range(0, index).forEach(i -> item.decreaseStock());
        itemRepository.saveAndFlush(item);
    }
}
