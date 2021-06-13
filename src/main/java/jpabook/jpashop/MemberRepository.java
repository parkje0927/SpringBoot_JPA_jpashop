package jpabook.jpashop;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository {

    // entityManager 를 주입시켜준다.
    @PersistenceContext
    private EntityManager em;

    public Long save(Member member) {
        em.persist(member);
        // 저장하고 나면 command, query 를 분리하라
        // Id 정도만 조회
        return member.getId();
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}
