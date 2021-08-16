package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * XToOne(ManyToOne, OneToOne)
 * Order
 * Order -> Member (ManyToOne)
 * Order -> Delivery (OneToOne)
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    /**
     * V1. 엔티티 직접 노출
     * - 양방향 관계 문제 발생
     *   - 양방향 연관관계 문제
     *   - Order 안에 Member 가 있고, Member 안에 Order 가 있음 -> 무한루프에 빠짐
     *
     * - 양방향 관계에서는 둘 중 한 곳에 @JsonIgnore 을 해줘야 한다.
     *   - Member, OrderItem, Delivery
     *   - but, 또 에러가 나타남. Type definition error
     *   - Order 를 가지고 왔는데, Order 의 Member 가 지연로딩이므로, db 에서 Member 를 가져오는 게 아니라, db 에서는 Order 만 가져온다.
     *   - hibernate 가 proxyMember 객체를 생성해서 넣어둔다. 이게 바로 ByteBuddy 라는 것이다.
     *   - 즉, Order 안에 있는 Member 는, private Member member = new ByteBuddyInterceptor(); 상태이다.
     *   - member 가 순수한 객체가 아니라 ByteBuddy 니까 이걸 가져올 수 없다고 에러가 나타나는 것이다.
     *
     * - 해결법
     *   - Hibernate5Module 을 설치해야 한다.
     *   - Hibernate5Module 모듈 등록, Lazy = null 처리
     *   - LAZY 강제 초기화
     *
     * - 주의
     *   - 앞에서 계속 강조했듯이 정말 간단한 애플리케이션이 아니면 엔티티를 API 응답으로 외부로 노출하는 것은 좋지 않다.
     *   - 따라서 Hibernate5Module 을 사용하기 보다는 DTO 로 변환해서 반환하는 것이 더 좋은 방법이다.
     *
     * - 주의
     *   - 지연 로딩(LAZY) 을 피하기 위해 즉시 로딩(EAGER) 으로 설정하면 안된다!
     *   - 즉시 로딩 때문에 연관관계가 필요 없는 경우에도 데이터를 항상 조회해서 성능 문제가 발생할 수 있다.
     *   - 즉시 로딩으로 설정하면 성능 튜닝이 매우 어려워진다.
     *   - 항상 지연 로딩을 기본으로 하고, 성능 최적화가 필요한 경우에는 fetch join 을 사용해라! (v3 에서 설명)
     */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());//검색 조건이 없으므로 다 가져오게 될 것
        for (Order order : all) {
            order.getMember().getName(); //getName 까지 하면 LAZY 강제 초기화해서 Member db 에 접근함
            order.getDelivery().getAddress(); //getAddress 까지 하면 LAZY 강제 초기화해서 Delivery db 에 접근함
        }
        return all;
    }

    /**
     * V2. 엔티티를 조회해서 DTO 로 변환 (fetch join 사용 x)
     * - 단점 : 지연로딩으로 쿼리 N 번 호출
     *
     * 엔티티를 DTO 로 변환하는 일반적인 방법이다.
     * 쿼리가 총 1 + N + N 번 실행된다. (v1 과 쿼리수 결과는 같다.)
     *   - order 조회 1번(order 조회 결과 수가 N 이 된다.)
     *   - order -> member 지연 로딩 조회 N 번
     *   - order -> delivery 지연 로딩 조회 N 번
     *   예시) order 의 결과가 4개면 최악의 경우 1 + 4 + 4 번 실행된다. (최악의 경우)
     *   지연 로딩은 영속성 컨텍스트에서 조회하므로, 이미 조회된 경우 쿼리를 생략한다.
     *
     * 처음에 order 조회하면 -> sql 1번 실행해서 -> 결과 주문 수가 2개가 나온다.
     * 그러면 loop 도는 곳에서 2번 돈다.
     * 그러므로 총 5번 돈다.
     *
     */
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        //아래에서 loop 돌릴 때, LAZY 초기화
        List<SimpleOrderDto> result = orders.stream()
                .map(SimpleOrderDto::new)
                .collect(Collectors.toList());

        return result;
    }

    /**
     * V3. 엔티티를 조회해서 DTO 로 변환(fetch join 사용 O)
     * - fetch join 으로 쿼리 1번 호출
     * 참고 : fetch join 에 대한 자세한 내용은 JPA 기본편 참고 (정말 중요함)
     *
     * 엔티티를 fetch join 을 사용해서 쿼리 1번에 조회
     * fetch join 으로 order -> member, order -> delivery 는 이미 조회된 상태이므로 지연 로딩 X
     *
     * 실무에서 jpa 사용하려면 fetch join 무조건 이해해야 함!
     * 다 lazy 로 만들고 필요한 것만 fetch join 으로 가져오면 대부분의 문제가 해결 된다.
     */
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> orderV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return result;
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); //LAZY 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); //LAZY 초기화
        }
    }

    /**
     * V4. JPA 에서 DTO 로 바로 조회
     * - 쿼리 1번 호출
     * - select 절에서 원하는 데이터만 선택해서 조회
     *
     * V3 와 V4 는 우열을 가리기가 어렵다.
     * V3 : 외부의 모습을 건들이지 않고 내부에 원하는 것만 fetch join. 재사용성이 높음.
     * V4 : 쿼리를 한 번 할 때 JPQL 을 짜서 가져옴. 재사용성이 떨어짐. V3 보다는 성능 최적화가 낮음.
     *
     * - 일반적인 SQL 을 사용할 때처럼 원하는 값을 선택해서 조회
     * - new 명령어를 사용해서 JPQL 의 결과를 DTO 로 즉시 변환
     * - select 절에서 원하는 데이터를 직접 선택하므로 DB -> 애플리케이션 네트워크 용량 최적화(생각보다 미비함)
     * - 리포지토리 재사용성 떨어짐. API 스펙에 맞춘 코드가 리포지토리에 들어가는 단점
     *
     * 정리
     * 엔티티를 DTO 로 변환하거나, DTO 로 바로 조회하는 두 가지 방법은 각각 장단점이 있다.
     * 둘 중 상황에 따라서 더 나은 방법을 선택하면 된다.
     * 엔티티로 조회하면 리포지토리 재사용성도 좋고, 개발도 단순해진다.
     * 리포지토리는 엔티티 조회할 때 사용하기!
     * 따라서 권장하는 방법은 다음과 같다.
     *
     * [ 쿼리 방식 선택 권장 순서 ]
     * 1. 우선 엔티티를 DTO 로 변환하는 방법을 선택한다.
     * 2. 필요하면 fetch join 으로 성능을 최적화한다. => 대부분의 성능 이슈가 해결되다.
     * 3. 그래도 안되면 DTO 로 직접 조회하는 방법을 사용한다.
     * 4. 최후이 방법은 JPA 가 제공하는 네이티브 SQL 이나 스프링 JDBC Template 을 사용해서 SQL 을 직접 사용한다.
     */
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }
}
