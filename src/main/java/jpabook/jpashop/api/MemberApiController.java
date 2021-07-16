package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController //Controller + ResponseBody
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    /**
     * 등록 V1 : 요청 값으로 Member 엔티티를 직접 받는다.
     * 문제점
     * - 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다.
     * - 엔티티에 API 검증을 위한 로직이 들어간다. (@NotEmpty 등등)
     * - 실무에서는 회원 엔티티를 위한 API 가 다양하게 만들어지는데, 한 엔티티에 각각의 API 를
     위한 모든 요청 요구사항을 담기는 어렵다.
     * - 엔티티가 변경되면 API 스펙이 변한다.
     * 결론
     * - API 요청 스펙에 맞추어 별도의 DTO 를 파라미터로 받는다.
     */
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /**
     * 등록 V2 : 요청 값으로 Member 엔티티 대신에 별도의 DTO 를 받는다.
     *
     * Member 엔티티 대신에 RequestBody 와 매핑한다.
     * 엔티티와 프레젠테이션 계층을 위한 로직을 분리할 수 있다.
     * 엔티티와 API 스펙을 명확하게 분리할 수 있다.
     * 엔티티가 변해도 API 스펙이 변하지 않는다.
     *
     * 참고 : 실무에서는 엔티티를 API 스펙에 노출하면 안된다!
     */
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /**
     * 수정 API
     *
     * 회원 수정 API updateMemberV2 는 회원 정보를 부분 업데이트 한다.
     * 여기서 PUT 방식을 사용했는데, PUT 은 전체 업데이트를 할 때 사용하는 것이 맞다.
     * 부분 업데이트를 하려면 PATCH 를 사용하거나 POST 를 사용하는 것이 REST 스타일에 맞다.
     */
    @PatchMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id,
                                               @RequestBody @Valid UpdateMemberRequest request) {
        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    /**
     * 조회 API
     *
     * 조회 V1 : 응답 값으로 엔티티를 직접 외부에 노출한다.
     * 문제점
     * - 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다.
     *  - 기본적으로 엔티티의 모든 값이 노출된다.
     *  - 응답 스펙을 맞추기 위해 로직이 추가된다.
     *    (Member 의 orders 에 @JsonIgnore 을 추가하여 orders 는 빼고 반환한다거나, 별도의 뷰 로직 등등)
     *  - 실무에서는 같은 엔티티에 대해 API 가 용도에 따라 다양하게 만들어지는데,
     *    한 엔티티에 각각의 API 를 위한 프레젠테이션 응답 로직을 담기는 어렵다.
     *  - 엔티티가 변경되면 API 스펙이 변한다.
     *  - 추가로 컬렉션을 직접 반환하면 향후 API 스펙을 변경하기 어렵다.
     *    (별도의 Result 클래스 생성으로 해결)
     * 결론
     * - API 응답 스펙에 맞추어 별도의 DTO 를 반환한다.
     *
     * 조회 V1
     * => 안 좋은 버전, 모든 엔티티가 노출, @JsonIgnore -> 이건 정말 최악, api 가 이거 하나인가!! 화면에 종속적이지 마라!!
     */
    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers();
    }

    /**
     * 조회 V2 : 응답 값으로 엔티티가 아닌 별도의 DTO 를 반환한다.
     *
     * 엔티티를 DTO 로 변환해서 반환한다.
     * 엔티티가 변해도 API 스펙이 변경되지 않는다. (예시 : username 으로 바꾸면 에러 뜬다.)
     * 추가로 Result 클래스로 컬렉션을 감싸서 향후 필요한 필드를 추가할 수 있다.
     */
    @GetMapping("/api/v2/members")
    public Result memberV2() {
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(member -> new MemberDto(member.getName()))
                .collect(Collectors.toList());
//        return new Result(collect);
        return new Result(collect.size(), collect); //count 도 추가할 수 있다.
    }


    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count; //count 추가 시 필요
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name; //단순하게 이름만 반환한다고 가정
    }
}
