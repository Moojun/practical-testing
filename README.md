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