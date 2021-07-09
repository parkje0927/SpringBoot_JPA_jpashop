package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    /**
     * 병합 merge
     * ItemService 의 updateItem 코드와 같은 역할을 한다.
     * 데이터를 바꿔치기해서 transaction commit 할 때 변경 감지 되서 update 된다.
     * updateItem 코드 역할을 한다고 보면 된다.
     */
    public void save(Item item) {
        if (item.getId() == null) {
            em.persist(item);
        } else {
            //이는 update 와 유사하다. 이후 더 설명 추가할 예정
            //item 은 영속성 컨텍스트에서 관리되는 것은 아니고 merge 의 결과가 영속성 컨텍스트로 관리된다.
            //item 은 파라미터 역할이다.
            em.merge(item);
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
