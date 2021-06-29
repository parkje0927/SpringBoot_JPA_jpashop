package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    // entityManager 를 만들어서 주입시켜준다.
//    @PersistenceContext -> 이거 대신 Autowired 을 적어도 가능한 이유는 Spring JPA 가 이를 지원하기 때문이다.
    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
        // 저장하고 나면 command, query 를 분리하라
        // Id 정도만 조회
        // 영속성 context 에 pk 값이 들어간다.
        // persist 를 한다고 db에 insert 가 되는 것은 아니다.
//        return member.getId();
    }

    public Member findOne(Long id) {
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
