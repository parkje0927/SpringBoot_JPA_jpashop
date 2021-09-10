package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    /**
     * 주문내역에서 추가로 주문한 상품 정보를 추가로 조회하자.
     * Order 기준으로 컬렉션인 OrderItem 과 Item 이 필요하다.
     *
     * 앞의 예제에서는 toOne(OneToOne, ManyToOne) 관계만 있었다.
     * 이번에는 컬렉션인 일대다 관계(OneToMany) 를 조회하고, 최적화하는 방법을 알아보자.
     *
     * V1. 엔티티 직접 노출
     * - 엔티티가 변하면 API 스펙이 변한다.
     * - 트랜잭션 안에서 지연 로딩 필요
     * - 양방향 연관관계 문제
     *
     * - Hibernate5Module 모듈 등록, LAZY=null 처리
     * - 양방향 관계 문제 발생 -> @JsonIgnore
     * @return
     */
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); //LAZY 강제 초기화
            order.getDelivery().getAddress(); //LAZY 강제 초기화
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName()); //LAZY 강제 초기화
        }

        return all;
    }

    /**
     * V2. 엔티티를 조회해서 DTO 로 변환(fetch join 사용 x)
     * - 트랜잭션 안에서 지연 로딩 필요
     */


    /**
     * V3. 엔티티를 조회해서 DTO 로 변환(fetch join 사용 o)
     * - 페이징 시에는 N 부분을 포기해야 함(대신에 batch fetch size? 옵션 주면 N -> 1 쿼리로 변경 가능)
     */

    /**
     * V4. JPA 에서 DTO 로 바로 조회, 컬렉션 N 조회 (1 + N Query)
     * - 페이징 가능
     */

    /**
     * V5. JPA 에서 DTO 로 바로 조회, 컬렉션 1 조회 최적화 버전 (1 + 1 Query)
     * - 페이징 가능
     */

    /**
     * V6. JPA 에서 DTO 바로 조회, 플랫 데이터(1 Query)
     * - 페이징 불가능...
     */
}
