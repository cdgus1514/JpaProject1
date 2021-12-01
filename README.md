# JpaProject1
첫 번째 Jpa 프로젝트

- 비즈니스 모델 개발
- 테스트 케이스 검증
- 도메인 주도 설게 이해 



# 1. JPA 핵심 비즈니스 구현

## 스프링부트 동작원리

- 스프링부트 어플리케이션. 어노테이션 > 하위 패키지를 스프링이  컴포드 스캔해서 스프링빈에 등록
- PersistenceContext 어노테이션이 있으면 스프링이 알아서 EntityManager 주입





## 주문조회 검색기능 추가
* OrderSearch
* OrderRepository

+ String
+ Criteria
- QueryDSL



## 상품수정

- 변경감지
- 병합

-> 터티체킹(변경감지) : JPA가 변경된 부분을 찾아서 업데이트쿼리 자동생성 후 반영


> 준영속 엔티티 : **영속성** 컨텍스트가 더이상 관리하지 않는 엔티티  

-> 객체는 새로 생성했어도 JPA가 식별할 수 있는(이미 한번 등록된 id)를 사용하면 준영속 상태로 변경
-> 준영속 상태에서는 엔티티 속성의 값을 바꿔도 업데이트 안됨(save 업데이트 가능)  


> 준영속 엔티티 수정방법

1. 변경감지 기능사용

-> 영속성 컨텍스트에서 엔티티를 다시 조회한 후 데이터를 수정하는 방법

2. 병합 사용

-> 준영속 상태의. 엔티티를 영속성. 상태로 변경하는 방법
-> 파라미터로 넘어온 모든 필드를 업데이트 처리

!! 파라미터 필드 값이 null이면 null로 업데이트됨….



## API 개발

1. 엔티티를 그대로 사용하기 보다는 DTO를 사용해서 받은 후 사용하는걸 권장!

-> 패키지 구분
1) 엔티티를 조회하는 경우
2) 특정 페이지에 의존적인 데이터를 조회해야 하는 경우


## API개발 고급
1. 지연로딩 조회 최적화
2. 컬렉션 조회 최적화
3. 페이징 최적화
4. OSIV 최적화



### 지연로딩 조회 최적화

1. @ToOne 관계에서 양방향 연관관계가 존재하면 서로 참조하여 무한루프 발생
> 양방향 연관관계 중 한쪽은 JsonIgnore로 막아야함

2. 지연로딩 > 매핑 된 객체를 실제로 가져오지 않고 (DB에서 안가져옴)
   hibernate에서 매핑 된 객체를 상속받아서 가짜 Proxy객체를 생성해서 넣어둠 (bytebuddy 라이브러리)

! jackson 라이브러리에서 proxy객체를 몰라서 에러발생


     * 해결방법
1) hibernate5module 설치 후 Bean 등록 후 조회


> 지연로딩 시 n+1 쿼리


1. 패치조인 사용 (조인된 엔티티 모든 데이터 가져옴)



### 컬렉션 조회 최적화

> 일대다(@ToMany) 관계를 조회하는 경우 row수 증가

1. Distinct를 사용해서 JPA에서 중복제거 도와줌(but 페이징 불가)

-> 메모리에서 페이징처리함(개망함)


2. 컬렉션 조회 + 페이징 처리

1) @ToOne 관계를 모두 패치조인
2) 지연로딩 + default_batch_fetch_size 사용
   -> IN 쿼리로 묶어서 한번에 가져옴

* dafault_batch_fetch_size : 전역 설정
* 컬렉션에서 사용할 경우 해당 엔티티 속성에 @BatchSize()로 설정 가능
* @ToOne에서 사용할 경우 엔티티에 설정



3. 컬렉션 & DTO 직접조회

1) 컬렉션 조회 시 N+1 발생 -> 컬렉션 쿼리에서 IN절로 처리 후 Map으로 받아서 결과전달

* Map으로 성능향상 -> O(1)
* 일대다 까지 JOIN 후 중복데이터 별도로 제거(stream groupBy 처리)
  -> groupBy 사용 시 EqualsAndHashCode(기준이 될 필드) 선택 필요