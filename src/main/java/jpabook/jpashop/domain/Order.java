package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    /*
    연관관계의 주인을 정해야 한다.
    외래키가 있는 주문을 연관관계의 주인으로 정하는 것이 좋다.
    어떤 값이 변경되었을 때 foreign key(fk) 를 바꿀 것이라고 지정해주면 되는데 그게 연관관계의 주인이다.
    order 에 member_id 라는 fk 가 있으므로 이를 연관관계의 주인이다.
     */
    @ManyToOne(fetch = LAZY) //다대일의 관계
    @JoinColumn(name = "member_id") //mapping 을 무엇을 할 것인지
    private Member member;

    //eager 타입일 경우
    //JPQL select o from order o; -> select * from order
    //query 가 100개면 100 + 1(처음 쿼리) 개의 쿼리가 날아가는 것이다.

    /*
    cascade 없으면,
    persist(orderItemA)
    persist(orderItemB)
    persist(orderItemC)
    persist(order)
    이렇게 따로 해야하는데

    cascade 가 있는 경우,
    order 저장 시 , orderItem, delivery 도 같이 persist 된다.
    p
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    //hibernate 가 지원을 해주기 때문에 annotation 필요 없음
    private LocalDateTime orderDate; //주문시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; //주문상태 [ORDER, CANCEL]

    //==연관관계 메서드==//
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==//
    // 주문 생성 시 여기서 모두 작성 된다.
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //==비즈니스 로직==//
    /**
     * 주문 취소
     */
    public void cancel() {
        // 이미 배송 완료된 상품일 경우
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }

        this.setStatus(OrderStatus.CANCLE);
        // orderItem 도 cancel 해야 한다.
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    //==조회 로직==//
    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice() {
//        int totalPrice = 0;
//        for (OrderItem orderItem : orderItems) {
//            totalPrice += orderItem.getTotalPrice();
//        }
//        return totalPrice;
        return orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum();
    }
}
