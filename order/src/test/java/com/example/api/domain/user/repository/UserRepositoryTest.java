package com.example.api.domain.user.repository;

import static com.example.api.domain.user.Util.createUser;

import com.example.core.db.config.QueryDslConfig;
import com.example.core.domain.user.domain.User;
import com.example.core.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
@Import({QueryDslConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("UserRepository 테스트")
@Slf4j
public class UserRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("User 데이터가 저장되는지 확인합니다.")
    public void shouldPersistUserSuccessfully() {
        // given
        User user = createUser("email", "password", "nickname");

        // when
        userRepository.save(user);

        // then
        Assertions.assertThat(em.contains(user)).isTrue();
    }

    @Test
    @DisplayName("이메일, 비밀번호, 닉네임이 없으면 예외를 던집니다.")
    public void shouldThrowExceptionWhenEmailPasswordNicknameIsNull() {
        // given
        User u1 = createUser(null, "password", "nickname");
        User u2 = createUser("email", null, "nickname");
        User u3 = createUser("email", "password", null);

        // when

        // then
        Assertions.assertThatThrownBy(() -> userRepository.save(u1)).isExactlyInstanceOf(
            DataIntegrityViolationException.class);
        Assertions.assertThatThrownBy(() -> userRepository.save(u2)).isExactlyInstanceOf(
            DataIntegrityViolationException.class);
        Assertions.assertThatThrownBy(() -> userRepository.save(u3)).isExactlyInstanceOf(
            DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("이메일이 중복되면 예외를 던집니다.")
    public void shouldThrowExceptionWhenEmailIsDuplicated() {
        // given
        User u1 = createUser("email", "password", "nickname");
        userRepository.save(u1);
        em.flush();

        // when
        User u2 = createUser("email", "password", "nickname");
        Assertions.assertThatThrownBy(() -> userRepository.save(u2)).isExactlyInstanceOf(
            DataIntegrityViolationException.class);
    }

}
