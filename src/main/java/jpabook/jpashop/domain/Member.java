package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

//    @NotEmpty //apiV2 로 변경하면서 삭제
    private String name;

    @Embedded
    private Address address;

    //한 회원이 여러 개의 상품을 주문할 수 있으므로 일대다
    //연관관계의 주인이 아니므로 mappedBy 를 넣어준다
    //order table 에 있는 member field 에 의해 mapping 이 된 것이라는 의미

    //컬렉션은 변경하지 말고 그대로 사용하기 => hibernate 가 컬렉션을 관리하므로 필드에서 초기화하는 것이 좋다.
    @OneToMany(mappedBy = "member")
    @JsonIgnore // 간단한 주문 조회 V1 에서 추가
    private List<Order> orders = new ArrayList<>();

}
