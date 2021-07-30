package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
