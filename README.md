### 2. 서버가 시작하는 시점에 부모 ApplicationContext와 자식 ApplicationContext가 초기화되는 과정에 대해 구체적으로 설명해라.

1. 서블릿 컨테이너가 실행되면 web.xml파일을 찾아서 읽는다
2. 컨테이너를 생성한다

    > 2.1  만약<br>ContextLoaderListener listener가 설정되어 있다면 ContextLoaderListener의 contextInitialized을 실행시킨다<br><br>
	2.2
	만약<br>load-on-startup 이 설정되어있다면 HTTPServlet.init메소드 까지 실행시킨다
	<br><br>
	부모 컨테이너<br>
	ContextLoaderListener의 ContextInitialized method가 실행되면 부모 Spring Container가 생성된다<br><br>
	자식 컨테이너<br>
	HttpServlet의 init method가 실행되면 자식 Spring Container가 생성된다<br><br>
	만약 부모 컨테이너가 존재한다면 자식 컨테이너는 부모 컨테이너를 생성자의 인자로 받아서 생성하게 된다. 그 결과 자식은 부모의 Bean에 접근할 수 있으나 부모는 자식 Container에 접근할 수 없는 현상이 발생한다

### 3. 서버 시작 후 http://localhost:8080으로 접근해서 질문 목록이 보이기까지 흐름에 대해 최대한 구체적으로 설명하라. 
* 요청이 들어오면 QuestionController Class의 @RequestMapping(value={"", "/questions"}) 설정과 QuestionController Class내의 list method의 @RequestMapping("") 설정이 만나서 최종적으로 / 즉 http://localhost:8080/ 요청에 대해 list 메소드가 반응하게 된다

* 해당 메소드는 인자로 Model model을 받는데 해당 model에 필요한 정보들을 추가해주고
model.addAttribute("questions", qnaService.findAll());
return 값으로 view resource의 위치를 반환한다.
그러면 컨테이너에 설정해 두었던 InternalResourceViewResolver bean이 실제 jsp를 찾아서 클라이언트로 내려보내준다


### 9. UserService와 QnaService 중 multi thread에서 문제가 발생할 가능성이 있는 소스는 무엇이며, 그 이유는 무엇인가?
* 스프링 Container는 등록된 빈들을 관리할 때 특별한 설정을 하지 않는 이상 기본적으로 인스턴스를 1개만 유지한다

* 스프링 Container가 빈을 관리할때 기본값으로 설정되는 빈의 Scope(범위)는 위에서 말한것 처럼 singleton이다

* UserService에는 xml 설정에도, @Scope 어노테이션도 사용하지 않았으므로 UserService 빈은 싱글톤으로 관리 될 것이다.
상태값이 있는 객체를 싱글톤으로 유지하면 멀티스레드 환경에서 문제가 발생할 수 있는데 UserService 필드에 보면 private User existedUser; 라는 상태값이 있는것으로 보아 문제가 발생할 것으로 보인다

* QnaService에는 @Scope("prototype") 라는 설정이 되어 있으므로 Container내에 여러개의 인스턴스가 존재 가능하다.
하지만 지금 주어진 코드에서는 실제로 인스턴스가 Container 전체에서 2개만 존재하게 된다.

* 왜냐하면 prototype은 Container에다가 getBean() 메소드로 빈을 요청하거나 @Autowired등의 설정으로 인해 다른 빈에 prototype으로 설정된 빈이 주입될 때 새로운 인스턴스가 추가적으로 생성되게 되는데 주어진 코드에서는 QnaService 빈이 2개의 Controller에서만 참조하고 있으므로 Container 내부에 존재하는 단 2개의 인스턴스가 수많은 사용자들의 요청을 멀티스레드 환경에서 대응하게 되므로 멀티스레드 환경에서 안전하지 못하게 된다.

* 빈이 상태값을 가져도 문제가 없으려면 요청 하나당 하나의 인스턴스가 대응되어야 하는데 2개의 인스턴스만 가지고는 수많은 사용자들의 요청을 멀티스레드 환경에서 대응할때 문제가 발생할 수 밖에 없게 된다.

* 따라서 두 경우 모두 멀티스레드 환경에서 문제가 발생할 가능성이 있다고 볼 수 있다.


