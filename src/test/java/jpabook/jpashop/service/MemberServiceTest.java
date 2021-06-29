package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

//실제 spring boot 올려서 테스트한다는 의미
@RunWith(SpringRunner.class)
@SpringBootTest
// Transactional 가 test 코드에서는 rollback 을 해버리므로 Rollback annotation 을 붙인다.
// Rollback(value = false) 을 Test annotation 밑에 써준다.
@Transactional
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    //live template
    @Test
//    @Rollback(value = false)
    public void 회원가입() throws Exception {
        //given
        Member member = new Member();
        member.setName("park");

        //when
        Long saveId = memberService.join(member);

        //then
//        em.flush(); // 이렇게 해주면 insert 문이 날아간다. 이후 rollback 된다.
        assertEquals(member, memberRepository.findOne(saveId));

    }

    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("park");

        Member member2 = new Member();
        member2.setName("park");

        //when
        memberService.join(member1);
        memberService.join(member2); // 예외가 발생해야 한다!!
//        try {
//            memberService.join(member2); // 예외가 발생해야 한다!!
//        } catch (IllegalStateException e) {
//            return;
//        }

        //then
        // 여기에 오면 잘못 된 것이므로 fail 을 날린다.
        fail("예외가 발생해야 한다.");

    }
}

/**
 * RunWith(SpringRunner.class) : 스프링과 테스트 통합
 * SpringBootTest : 스프링 부트 띄우고 테스트(이게 없으면 @Autowired 다 실패)
 * Transactional : 반복 가능한 테스트 지원, 각각의 테스트를 실행할 때마다 트랜잭션을 시작하고 테스트
 * 가 끝나면 트랜잭션을 강제로 롤백 (이 어노테이션이 테스트 케이스에서 사용될 때만 롤백)
 */

/*
 * 테스트 케이스를 위한 설정
 * 테스트는 케이스 격리된 환경에서 실행하고, 끝나면 데이터를 초기화 하는 것이 좋다.
 * 그런 면에서 메모리 DB를 사용하는 것이 가장 이상적이다.
 * 추가로 테스트 케이스를 위한 스프링 환경과 일반적으로 애플리케이션을 실행하는 환경은 보통 다르므로 설정 파일을 다르게 사용하자.
 *
 * db 띄우는 거 없이 진행하기 위해 in-memory 사용 할 수 있다.
 * test 안에서 resources 폴더를 만들고 main 에서의 application.yml 파일을 복사해온다.
 *
 * 이제 테스트에서 스프링을 실행하면 이 위치에 있는 설정 파일을 읽는다.
 * 만약 이 위치에 없으면 src/resources/application.yml 을 읽는다.
 *
 * 스프링 부트는 datasource 설정이 없으면 기본적으로 메모리 DB를 사용하고,
 * driver-class 도 현재 등록된 라이브러리르 보고 찾아준다.
 * 추가로 ddl-auto 도 create-drop 모드로 동작한다.(create 이후 drop 을 해서 자원을 정리해준다.)
 * 따라서 데이터소스나, JPA 관련된 별도의 추가 설정을 하지 않아도 된다.
 *
 */
