package hello.core;

import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MemberApp {
    public static void main(String[] args){
//        AppConfig appConfig = new AppConfig();
//        MemberService memberService = appConfig.memberService();

        //Bean에 등록된 객체를 가져옴
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        //이름, 반환 타입
        MemberService memberService = applicationContext.getBean("memberService", MemberService.class);

        Member member = new Member(1L, "memberA", Grade.VIP);
        memberService.join(member);

        Member findMember = memberService.findMember(1L);
        System.out.println("new Memeber: "+member.getName());
        System.out.println("find Member: "+findMember.getName());
    }
}
