package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
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
    @ManyToOne //다대일의 관계
    @JoinColumn(name = "member_id") //mapping 을 무엇을 할 것인지
    private Member member;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    //hibernate 가 지원을 해주기 때문에 annotation 필요 없음
    private LocalDateTime orderDate; //주문시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; //주문상태 [ORDER, CANCEL]




}
