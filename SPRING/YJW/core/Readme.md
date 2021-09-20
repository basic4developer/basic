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

- ApplicationContext JUnit Test
```
AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

    @Test
    @DisplayName("모든 빈 출력하기")
    void findAllBean(){
        String[] beanDefinitionNames = ac.getBeanDefinitionNames();
        for(String beanDifinitionName : beanDefinitionNames){
            Object bean = ac.getBean(beanDifinitionName);
            System.out.println("name= "+ beanDifinitionName + " object = "+ bean);
        }
    }

    @Test
    @DisplayName("애플리케이션 빈 출력하기")
    void findApplicationBean(){
        String[] beanDefinitionNames = ac.getBeanDefinitionNames();
        for(String beanDifinitionName : beanDefinitionNames){
            BeanDefinition beanDefinition = ac.getBeanDefinition(beanDifinitionName);

            //Role ROLA_APPLICATION: 직접 등록한 애플리케이션 빈
            //Role ROLA_INFRASTRUCTURE: 스프링 내부에서 사용하는 빈
            if(beanDefinition.getRole() == BeanDefinition.ROLE_APPLICATION){
                Object bean = ac.getBean(beanDifinitionName);
                System.out.println("name= "+ beanDifinitionName + " object = "+ bean);
            }
        }
    }
```

<br>

- 스프링 빈 조회 - 상속관계
    - 부모 타입으로 조회하면, 자식 타입도 함께 조회한다.
    - 모든 자바 객체의 최고 부모인 object 타입으로 조회하면 모든 스프링 빈을 조회한다.

- BeanFactory
    - 스프링 컨테이너의 최상위 인터페이스
    - 스프링 빈을 관리하고 조회하는 역할을 담당
    - getBean()을 제공한다.
    - 사용했던 대부분의 기능은 BeanFactory가 제공하는 기능이다.

- ApplicationContext
    - BeanFactory 기능을 모두 상속받아서 제공한다.
    - 빈을 관리하고 검색하는 기능을 BeanFactory가 제공해주는데, 둘의 차이는?
    - 애플리케이션을 개발할 때는 빈은 관리하고 조회하는 기능은 물론이고, 수많은 부가기능이 필요하다.

### 스프링 빈 설정 메타 정보 - BeanDefinition
- 역할과 구현을 개념적으로 나눈것
    - XML을 읽어서 BeanDefinition을 만들면 된다.
    - 자바 코드를 읽어서 BeanDefinition을 만들면 된다.
    - 스프링 컨테이너는 자바 코드인지, XML인지 몰라도 된다. 오직 BeanDefinition만 알면 된다.
- BeanDefinition을 빈 설정 메타정보라 한다.
    - @Bean, \<bean> 당 각각 하니씩 메타 정보가 생성
- 스프링 컨테이너는 이 메타정보를 기반으로 스프링 빈을 생성한다.

### 싱글톤 컨테이너(패턴)
- 클래스의 인스턴스가 딱 1개만 생성되는 것을 보장하는 디자인 패턴
```
public class SingletonTest {

    @Test
    @DisplayName("스프링 없는 순수한 DI 컨테이너")
    void pureContainer(){
        AppConfig appConfig = new AppConfig();
        //1. 조회: 호출할 때 마다 객체를 생성
        MemberService memberService1 = appConfig.memberService();

        //2. 조회: 호출할 때 마다 객체를 생성
        MemberService memberService2 = appConfig.memberService();

        //참조값이 다른 것을 확인
        System.out.println("memberService1 = "+ memberService1);
        System.out.println("memberService2 = "+ memberService2);

        Assertions.assertThat(memberService1).isNotSameAs(memberService2);
    }
}
```
- 스프링이 없는 순수한 DI컨테이너인 AppConfig는 요청을 할 때 마다 객체를 새로 생성한다. -> 메모리 낭비가 심하다
- 해결방안은 해당 객체가 딱 1개만 생성되고, 공유하도록 설계하면 된다. -> 싱글톤 패턴

```
public class SingletonService {
    private static final SingletonService instance = new SingletonService();

    public static SingletonService getInstance(){
        return instance;
    }

    //private 생성자로 인스턴스 하나만 생성할 수 있도록 한다.
    //이 객체 인스턴스가 필요하면 오직 getInstance() 메서드를 통해서만 조회할 수 있다.
    //private으로 막아서 혹시라도 외부에서 new 키워드로 객체 인스턴스가 생성되는 것을 막는다.
    private SingletonService(){

    }

    public void logic(){
        System.out.println("싱글톤 객체 로직 호출");
    }
}

    @Test
    @DisplayName("싱글톤 패턴을 적용한 객체 사용")
    void singletonServiceTest(){
        SingletonService singletonService1 =  SingletonService.getInstance();
        SingletonService singletonService2 =  SingletonService.getInstance();

        //같은 객체 참조
        System.out.println("singletonService1 = "+ singletonService1);
        System.out.println("singletonService2 = "+ singletonService2);

        assertThat(singletonService1).isSameAs(singletonService2);
    }

    @Test
    @DisplayName("스프링 컨테이너와 싱글톤")
    void springContainer(){
        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

        MemberService memberService1 = ac.getBean("memberService", MemberService.class);
        MemberService memberService2 = ac.getBean("memberService", MemberService.class);

        //참조값이 다른 것을 확인
        System.out.println("memberService1 = "+ memberService1);
        System.out.println("memberService2 = "+ memberService2);

        assertThat(memberService1).isSameAs(memberService2);
    }
```

<br>

### 싱글통 방식의 주의점
- 객체 인스턴스를 하나만 생성해서 공유하는 싱글톤 방식은 여러 클라이언트가 하나의 같은 객체 인스턴스를 공유하기 때문에 싱글톤 객체는 상태를 유지(stateful)하게 설계하면 안된다.
- 무상태(stateless)로 설계해야 한다.
    - 특정 클라이언트에 의존적인 필드가 있으면 안된다.
    - 특정 클라이언트가 값을 변경할 수 있는 필드가 있으면 안된다.
    - 가급적 읽기만 가능해야 한다.
    - 필드 대신에 자바에서 공유되지 않는, 지역변수, 파라미터, ThreadLocal 등을 사용해야 한다.
- 스프링 빈의 필드에 공유 값을 설정하면 큰 장애가 발생 할 수 있다.
