package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    //@Autowired
    private final MemberRepository memberRepository;

//    /**
//     * 생성자 인젝션
//     * 생성자가 1개만 있으면 알아서 인젝션 해줌
//     * @param memberRepository
//     */
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    // @RequiredArgsConstructor : final 필드만 가지고 생성자 만들어줌


    /**
     * 회원가입
     * @param member
     * @return
     */
    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member);    // 중복회원 검증
        memberRepository.save(member);

        return member.getId();
    }

    // 검증을 해도 동시에 같은 이름을 등록하는 경우 검증 안될 수 있음 > name(유니크 제약조건 설정 필요)
    private void validateDuplicateMember(Member member) {
        // EXCEPTION
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }


    /**
     * 회원 전체조회
     * @return
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }


    /**
     * 회원조회
     * @param memberId
     * @return
     */
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }


    @Transactional
    public void update(Long id, String name) {
        // 변경감지
        Member member = memberRepository.findOne(id);
        member.setName(name);
    }
}
