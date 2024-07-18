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

<h3>개선 코드</h3>

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


### 문제점

<p align="center">
  <img src= "https://github.com/user-attachments/assets/74688fe9-db8e-493e-a549-e218eff5c2c4" />
</p>

- 비관적 락은 **데이터베이스 레벨에서 락을 걸기 때문에, 모든 스레드가 물리 디스크에 직접 접근하여 부하가 커지고**.
- 분산 DB 환경의 경우 **변경된 데이터를 각 데이터베이스들 간 동기화를 하는데 문제점이 된다**.

### 기존 코드

```java
@Transactional
public void validatePayment(Long itemId, String merchantUid, String impUid, Long price) {
    Order order = orderRepository.findByMerchantUid(merchantUid)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문번호입니다."));
    IamportResponse<Payment> paymentIamportResponse = null;
    try {
        paymentIamportResponse = iamportClient.paymentByImpUid(
            impUid);

        if (paymentIamportResponse.getCode() != 0) {
            throw new IllegalArgumentException("결제 내역이 존재하지 않습니다.");
        }

        if (paymentIamportResponse.getResponse().getAmount().longValue() != order.getPrice()) {
            CancelData data = new CancelData(impUid, true);
            IamportResponse<Payment> response = iamportClient.cancelPaymentByImpUid(data);
            throw new IllegalArgumentException("결제 금액이 일치하지 않습니다.");
        }
    } catch (IamportResponseException e) {
        e.printStackTrace();
        throw new IllegalArgumentException("결제 내역이 존재하지 않습니다.");
    } catch (IOException e) {
        e.printStackTrace();
        throw new IllegalArgumentException("결제 내역이 존재하지 않습니다.");
    }
    //   비관적 락 실행 코드
    Item item = itemRepository.findByIdWithPessimisticLock(order.getItem().getId())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이템입니다."));
    item.decreaseStock();
    order.updateStatus(OrderStatusEnum.PAYMENT_SUCCESS);
    orderRepository.save(order);
}
```

### 개선 코드

```java
@RedissonLock(value = "#itemId")
public void decreaseStock(Long itemId, String merchantUid) {
    Item item = itemRepository.findById(itemId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이템입니다."));
    item.decreaseStock();
    itemRepository.save(item);
}
```

```java
@Override
public void validatePayment(Long itemId, String merchantUid, String impUid, Long price) {
    Order order = orderRepository.findByMerchantUid(merchantUid)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문 번호입니다."));

    IamportResponse<Payment> paymentIamportResponse = null;
    try {
        paymentIamportResponse = iamportClient.paymentByImpUid(
            impUid);

        if (paymentIamportResponse.getCode() != 0) {
            throw new IllegalArgumentException("결제 내역이 존재하지 않습니다.");
        }

        if (paymentIamportResponse.getResponse().getAmount().longValue()
            != price) {
            CancelData data = new CancelData(impUid, true);
            IamportResponse<Payment> response = iamportClient.cancelPaymentByImpUid(data);
            throw new IllegalArgumentException("결제 금액이 일치하지 않습니다.");
        }
    } catch (IamportResponseException e) {
        e.printStackTrace();
        throw new IllegalArgumentException("결제 내역이 존재하지 않습니다.");
    } catch (IOException e) {
        e.printStackTrace();
        throw new IllegalArgumentException("결제 내역이 존재하지 않습니다.");
    }
    // 분산 락 실행 코드
    itemService.decreaseStock(itemId);
    order.updateStatus(OrderStatusEnum.PAYMENT_SUCCESS);
    orderRepository.save(order);
}
```
### 해결방법

<p align="center">
  <img src= "https://github.com/user-attachments/assets/d9cd6415-1071-4493-8611-fbfd5ce5cd85" />
</p>

Lettuce는 락 획득하기 못하는 경우 **Redis에 계속해서 요청을 보내기 때문에** Redis의 부하가 생길 수 있다는 점을 고려하여 **Pub/Sub 방식의 Redisson을 이용하여 분산락**을 구현하였다.<br>

또한 Redisson은 Non-Blocking I/O 방식으로 관리하기 때문에 비관적 락보다 성능이 향상되는 것을 확인할 수 있었다.<br>
-> **평균 응답 시간 68831ms -> 7931ms 단축**

### 1000건 동시 요청 테스트 결과

<p align="center">
  <img src= "https://www.notion.so/image/https%3A%2F%2Fprod-files-secure.s3.us-west-2.amazonaws.com%2Fb60ba698-3478-44e8-b66b-40ecb9dfa408%2Fc5ac3300-5583-4fd2-984b-151a81840a6a%2FUntitled.png?table=block&id=54612c4c-3b2b-45f2-8148-932440aea02a&spaceId=b60ba698-3478-44e8-b66b-40ecb9dfa408&width=1920&userId=47471456-9b72-4efb-98e4-c4997f3e30e8&cache=v2" />
</p>

<p align="center">
  <img src= "https://www.notion.so/image/https%3A%2F%2Fprod-files-secure.s3.us-west-2.amazonaws.com%2Fb60ba698-3478-44e8-b66b-40ecb9dfa408%2Fe40ae858-12f7-4dd0-81a8-f196ce23da97%2FUntitled.png?table=block&id=faf11d14-f415-4c71-8477-9ec672d26808&spaceId=b60ba698-3478-44e8-b66b-40ecb9dfa408&width=1920&userId=47471456-9b72-4efb-98e4-c4997f3e30e8&cache=v2" />
</p>

  
</details>

<details>
<summary>3차 서비스 분리 후 Message Broker를 통해 서버 부하 분산</summary>

</details>

<details>
<summary>4차 분산 트랜잭션을 위하여 Saga Pattern 구현 (Choreography 방식)</summary>
</details>
