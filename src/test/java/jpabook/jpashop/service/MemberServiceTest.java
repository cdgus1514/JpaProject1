package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    @Test
    //@Rollback(value = false)
    public void 회원가입() throws  Exception {
        //given
        Member member = new Member();
        member.setName("choi");

        //when
        Long serviceId = memberService.join(member);

        //then
        assertEquals(member, memberRepository.findOne(serviceId));
    }

    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("choi1");

        Member member2 = new Member();
        member2.setName("choi1");

        //when
        memberService.join(member1);
        memberService.join(member2);    // 예외발생

        //then
        fail("예외발생.");
    }

}