package jpabook.jpashop;

import jpabook.jpashop.repository.MemberRepository;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

//    @Test
//    @Transactional
//    @Rollback(false)
//    public void testMember() throws Exception {
//        //given
//        Member member = new Member();
//        member.setUsername("memberA");
//
//        //when
//        Long saveId = memberRepository.save(member);
//        Member findMember = memberRepository.find(saveId);
//
//        //then
//        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
//        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
//        // Transactional annotation 이 test 파일에 있는 경우 test 가 끝난 뒤, rollback 을 한다.
//        // Rollback(false) 를 한 경우 db에 저장이 된다.
//
//        // 같은 transaction 안에서 저장하고 조회하면 같은 영속성 context 를 갖는 것이고,
//        // 식별자가 같으면 같은 entity 로 인식한다.
//        Assertions.assertThat(findMember).isEqualTo(member);
//        System.out.println("(findMember == member) = " + (findMember == member)); //true
//
//    }
}