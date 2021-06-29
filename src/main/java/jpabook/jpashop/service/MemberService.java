package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
// '읽기' 에서는 readonly=true 를 설정해주면 성능이 향상된다.
// 따라서 '쓰기' 의 경우에만 @Transactional 을 걸어준다.
@Transactional(readOnly = true)
//생성자를 만들어준다.
//@AllArgsConstructor

//final 이 적혀있는 것만 생성자를 만들어줘서 AllArgsConstructor 이거보다 더 낫다.
@RequiredArgsConstructor

public class MemberService {

    /**
     * 1) field injection
     * 변경이 불가능하다.
     */
//    @Autowired
//    private MemberRepository memberRepository;

    /**
     * 2) setter injection
     * 동작을 잘 하고 있는데 변경할 일이 없으므로 setter injection 은 좋지 않다.
     */
//    private MemberRepository memberRepository;
//    @Autowired
//    public void setMemberRepository(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    /**
     * 3) constructor injection
     * set 으로 변경이 되지 않고, test case 작성 시 유용
     * final 을 넣음으로써 문법 check 가능
     * RequiredArgsConstructor 를 활용해서 생성자 코드 안 써도 된다.
     */
    private final MemberRepository memberRepository;

//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    /**
     * 회원가입
     */
    @Transactional
    public Long join(Member member) {
        //중복 검사
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    //회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }
}
