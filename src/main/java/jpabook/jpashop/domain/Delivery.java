package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@Setter
public class Delivery {

    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    /*
    하나의 order 는 하나의 delivery 만 가지므로 일대일 관계이다.
    fk 를 order 에 두어도 되고 delivery 에 두어도 된다.
    order 에 fk 에 둔다면, order table 의 delivery 가 연관관계 주인이다.
     */
    @JsonIgnore // 간단한 주문 조회 V1 에서 추가
    @OneToOne(mappedBy = "delivery", fetch = LAZY)
    private Order order;

    @Embedded
    private Address address;

    //EnumType 을 ORDINAL 로 사용하게 되면 중간에 다른 타입이 추가될 경우 오류가 날 수 있다.
    //따라서 STRING 타입으로 지정해줄 것!
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status; //READY(배송준비), COMP(배송)
}