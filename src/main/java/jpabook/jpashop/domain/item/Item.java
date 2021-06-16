package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
/*
JOINED : 가장 정규화된 style
SINGLE_TABLE : 한 table 에 다 넣는 것
TABLE_PER_CLASS
 */
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter
@Setter
public abstract class Item {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    //양방향
    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();
}
