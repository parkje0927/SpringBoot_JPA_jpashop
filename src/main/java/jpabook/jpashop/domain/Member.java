package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded
    private Address address;

    //한 회원이 여러 개의 상품을 주문할 수 있으므로 일대다
    //연관관계의 주인이 아니므로 mappedBy 를 넣어준다
    //order table 에 있는 member field 에 의해 mapping 이 된 것이라는 의미
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

}
