# 회원 도메인 설계
- 회원 도메인 요구사항
    - 회원을 가입하고 조회할 수 있다.
    - 회원은 일반과 VIP 두 가지 등급이 있다.
    - 회원 데이터는 자체 DB를 구축할 수 있고, 외부 시스템과 연동할 수 있다.(미확정)

<br>

### DIP, OCP가 잘 지켜지고 있는가?
```
    //DIP, OCP를 위한하고 있다.
    //MemberRepository 추상화에 의존, MemoryMemeberRepository 구체화에 의존, 즉 둘 다 의존하고 있다.
    private final MemberRepository memberRepository = new MemoryMemeberRepository();
```
- 인터페이스에만 의존하도록 코드를 바꿀 필요가 있다.
```
//DIP, OCP 만족, 인터페이스에만 의존
//하지만 널 포인트 exception 발생
private DiscountPolicy discountPolicy;

//해결 방안
1. AppConfig를 이용
애플리케이션의 실제 동작에 필요한 구현객체를 생성한다.
생성한 객체 인스턴스의 참조(레퍼런스)를 생성자를 통해서 주입해준다.
* DIP완성: ServiceImpl은 추상에만 의존하면 된다. 이제 구현 클래스를 몰라도 된다.
* 관심사의 분리!
* DI(Dependency Injection) 의존관계 주입!
public class AppConfig {
    public MemberService memberService(){
        return new MemberServiceImpl(new MemoryMemeberRepository());
    }

    public OrderService orderService(){
        return new OrderServiceImpl(new MemoryMemeberRepository(), new FixDiscountPolicy());
    }
}
```
- AppConfig Refactoring
```
현재 문제점: 중복 및 역할에 따른 구현이 잘 안보인다.

```

<br>

# 주문 도메인 설계
- 주문과 할인 정책
    - 회원은 상품 주문할 수 있다.
    - 회원 등급에 따라 할인 정책을 적용할 수 있다.
    - 할인 정책은 모든 VIP는 1000원 할인해주는 고정 급액 할인을 적용해달라.(나중에 변경될 수 있음)
    - 할인 정책은 변경 가능성이 높다. 회사의 기본 할인 정책을 아직 정하지 못했고, 오픈 직전까지 고민을 미루고 있다. 최악의 경우 할인을 적용하지 않을 수 있다.(미확정)

<br>

# IoC, DI 그리고 Container

- 제어의 역전 IoC(Inversion of Control)
    - AppConfig로 인해 구현 객체는 자신의 로직을 실행하는 역할만 담당함다. 프로그램의 제어의 흐름은 AppConfig가 담당한다.
    - 프로그램의 흐름을 직접 제어하는 것이 아니라 외부에서 관리하는 것을 제어의 흐름IoC라고 한다.

<br>

- 프레임워크 vs 라이브러리
    - 프레임워크가 내가 작성한 코드를 제어하고, 대신 실행하면 그것은 프레임워크가 맞다.
    - 반면에 내가 작성한 코드가 직접 제어의 흐름을 담당한다면 그것은 프레임워크가 아니라 라이브러리다.

<br>

- 의존관계 주입 DI(Dependency Injection)
    - 정적인 클래스 의존관계와, 실행 시점에 결정되는 동적인 객체(인스턴스) 의존관계 둘을 분리해서 생각해야한다.
    - 애플리케이션 '실행 시점'에 외부에서 실제 구현 객체를 생성하고 클라이언트에 전달해서 클라이언트와 서버의 실제 의존 관계가 연결되는 것을 '의존관계 주입' 이라고 한다.
    - 의존관계 주입을 사용하면 정적인 클래스 의존관계를 변경하지 않고, 동적인 객체 인스턴스 의존관계를 쉽게 변경할 수 있다.

<br>

- IoC 컨테이너, DI 컨테이너
    - AppConfig처럼 객체를 생성하고 관리하면서 의존관계를 연결해 주는 것을 IoC컨테이너 또는 DI컨테이너라고 한다.
    - 의존관계 주입에 초점을 맞추어 최근에는 주로 DI컨테이너라고 한다.

<br>

- Spirng 컨테이너
    - ApplicationContext를 스프링 컨테이너라고 한다.
    - 스프링 컨테이너는 @Configuration이 붙은 AppConfig를 구성(설정)정보로 사용한다.
    - @Bean이라 적힌 메서드를 모두 호출해서 반환된 객체를 스프링 컨테이너에 등록한다.
    - 스프링 컨테이너를 통해서 필요한 스프링 빈(객체)를 찾아야한다. 스프링 빈은 applicationContext.getBean() 메서드를 사용해서 찾을 수 있다.

### 스프링 컨테이너와 스프링 빈
