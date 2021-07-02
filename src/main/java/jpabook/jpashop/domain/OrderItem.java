package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
// 예시로, OrderService 에서 OrderItem 객체를 선언해서 주문상품을 생성하려는 시도를 막기 위함
// protected level 의 생성자를 만들어주거나 다음과 같은 lombok 을 활용
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    /*
    한 order 가 여러 개의 orderItem 을 가질 수 있으므로
    order : orderItem => 일대다
     */
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; //주문 당시의 가격
    private int count; //주문 당시의 수량

    //==생성 메서드==//
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        // 재고 변경
        item.removeStock(count);

        return orderItem;
    }

    //==비즈니스 로직==//
    public void cancel() {
        // 재고를 가져와서 수량을 원복해준다.
        getItem().addStock(count);
    }

    //==조회 로직==//
    /** 주문상품 전체 가격 조회 */
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}