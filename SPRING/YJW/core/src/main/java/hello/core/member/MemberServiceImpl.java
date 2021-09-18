package hello.core.member;

public class MemberServiceImpl implements MemberService{
    //DIP를 위한하고 있다.
    //MemberRepository 추상화에 의존, MemoryMemeberRepository 구체화에 의존, 즉 둘 다 의존하고 있다.
    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void join(Member member) {
        memberRepository.save(member);
    }

    @Override
    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId);
    }
}
