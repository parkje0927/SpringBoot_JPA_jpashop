package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class MemberRepository {

    // entityManager 를 만들어서 주입시켜준다.
    @PersistenceContext
    private EntityManager em;

    public void save(Member member) {
        em.persist(member);
        // 저장하고 나면 command, query 를 분리하라
        // Id 정도만 조회
//        return member.getId();
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }

    //sql => query 대상, jpql => entity 대상
    //jpql => from 의 대상이 entity 이다.
    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
