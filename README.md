# 상품 주문 서비스

## 주요 기능
- 상품 주문

## 사용 기술
<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=OpenJDK&logoColor=white"> <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/Spring Data JPA-6DB33F?style=for-the-badge&logo=Spring Data JPA&logoColor=white"> 
<br>
<img src="https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=MariaDB&logoColor=white"> <img src="https://img.shields.io/badge/Docker compose-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white"> <img src="https://img.shields.io/badge/Apache Kafka-%3333333.svg?style=for-the-badge&logo=Apache Kafka&logoColor=white"> <img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=Redis&logoColor=white"> 

## 트러블 슈팅
<details>
<summary>1차 상품 재고 감소 시 비관적 락 적용</summary>
<h3>재고 감소 동시성 문제 </h3>
  
<p align="center">
<img src="https://github.com/user-attachments/assets/3a9f8394-c0ef-45de-873f-8d0f7a27f72a" />
</p>

<h3>기존 코드</h3>

```java
private void getDecreaseStock(Long itemId) {
    Item item = itemRepository.findById(itemId).get();
    item.decreaseStock();
    itemRepository.save(item);
}
```

<h3>수정 코드</h3>

```java
public interface ItemRepository extends JpaRepository<Item, Long>, ItemCustomRepository {
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Item i where i.id = :itemId")
    Optional<Item> findByIdWithPessimisticLock(@Param("itemId") Long itemId);
}
```

```java
private void getDecreaseStock(Long itemId) {
    Item item = itemRepository.findByIdWithPessimisticLock(itemId).get();
    item.decreaseStock();
    itemRepository.save(item);
}
```

### 해결방법
허나의 상품에 대해서 동시에 주문을 요청할 경우, 데이터의 일관성이 깨지게 된다. <br>
따라서, 락을 이용하여 하나의 요청에 대해서 동시성을 제어하기 위해 락을 이용하였다.<br>

낙관적 락이 비관적 락보다 성능적인 부분이 좋지만, 비관적 락을 선택한 이유는 **데이터의 일관성**과 **충동 발생 가능성**을 때문이다.<br>
또한 인기 상품의 경우는 동시에 주문 요청이 발생할 수 있기 때문에 비관적 락을 이용하는 것이 좋다고 생각했다.  
</details>

<details>
<summary>2차 Redis의 Pub/Sub 방식의 분산 락 적용</summary>
</details>

<details>
<summary>3차 서비스 분리 후 Message Broker를 통해 서버 부하 분산</summary>
</details>

<details>
<summary>4차 분산 트랜잭션을 위하여 Saga Pattern 구현 (Choreography 방식)</summary>
</details>
