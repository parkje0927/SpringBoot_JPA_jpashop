package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    /*
    한 order 가 여러 개의 orderItem 을 가질 수 있으므로
    order : orderItem => 일대다
     */
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; //주문 당시의 가격
    private int count; //주문 당시의 수량


}
