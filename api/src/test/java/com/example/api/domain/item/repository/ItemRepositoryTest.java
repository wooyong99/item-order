package com.example.api.domain.item.repository;

import static com.example.api.domain.user.Util.createItem;

import com.example.api.domain.user.Util;
import com.example.core.db.config.QueryDslConfig;
import com.example.core.domain.item.domain.Item;
import com.example.core.domain.item.repository.ItemRepository;
import com.example.core.domain.user.domain.User;
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
    ItemRepository itemRepository;

    User user;

    @BeforeEach
    void setUp() {
        user = Util.createUser("email", "password", "nickname");
    }

    @Test
    @DisplayName("Item 데이터가 저장되는지 확인합니다.")
    public void shouldPersistItemSucessfully() {
        // given
        Item item = createItem("name", 1000L, 10000L);

        // when
        itemRepository.save(item);

        // then
        Assertions.assertThat(itemRepository.findById(item.getId()).get()).isEqualTo(item);
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

}
