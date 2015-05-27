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
* 