# Practical Testing

## Persistence Layer

* Data Access의 역할
* 비즈니스 가공 로직이 포함되어서는 안 된다. Data에 대한 CRUD만 집중한 레이어
* Repository를 테스트하였으며,Spring을 띄우므로 통합 테스트긴 하지만,오로지 Persistence Layer만 테스트를 하는 느낌이기 때문에 단위 테스트 느낌이 난다.

## Business Layer

* 비즈니스 로직을 구현하는 역할
* Persistence Layer와의 상호작용(Data를 읽고 쓰는 행위)을 통해 비즈니스 로직을 전개시킨다.
* `트랜잭션`을 보장해야 한다.
* Service test(Business Layer + Persistence Layer): 통합 테스트 느낌이 난다. 



## Presentation Layer

* 외부 세계의 요청을 가장 먼저 받는 계층(프론트엔드에서 넘겨준 값 등)
* 파라미터에 대한 최소한의 검증을 수행한다.(넘겨받은 값에 대한 Validation)

* Controller test: 하위에 있는 Business Layer, Persistence Layer를 Mocking 처리하고, 단위 테스트 느낌으로 진행해보려고 한다. 

### MockMvc

* Mock(가짜) 객체를 사용해 스프링 MVC 동작을 재현할 수 있는 테스트 프레임워크





### Test Double

* https://martinfowler.com/articles/mocksArentStubs.html 

* Dummy, Fake, Stub, Spy, Mock 
  * Dummy: 아무 것도 하지 않는 깡통 객체
  * Fake: 단순한 형태로 동일한 기능은 수행하나, 프로덕션에서 쓰기에는 부족한 객체(ex. FakeRepository - Memory Map 같은 형태를 구현해서 FakeRepository.save() 가 요청되면 해당 객체를 Map에다가 put 한다. find by id의 경우 id를 기반으로 객체를 Map에서 찾아서 리턴해주는 등 리포지토리의 CRUD 기능을 다 모방은 할 수 있는 형태이다)
  * Stub: 테스트에서 요청한 것에 대해 미리 준비한 결과를 제공하는 객체, 정의하지 않은 그 외의 요청들에 대해서는 응답하지 않는다.
  * Spy: Stub이면서 호출된 내용을 기록하여 보여줄 수 있는 객체. 일부는 실제 객체처럼 동작시키고 일부만 Stubbing할 수 있다. 
  * Mock: 행위에 대한 기대를 명세하고, 그에 따라 동작하도록 만들어진 객체
* Stub과 Mock은 굉장히 헷갈린다. 
  * 가짜 객체이고 ''뭔가 요청한 것에 대해 이러한 결과를 리턴해줘'' 라는 부분에서는 비슷하게 생각이 든다. 
  * 검증하려는 목적이 다르다.
  * Stub: 내부적인 상태가 어떻게 바뀌었는지에 대한 것에 초점이 맞추어져 있다. 
  * Mock: 행위에 대한 것을 중심으로 검증을 하게 된다. 





