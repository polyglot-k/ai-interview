-- production 에서 사용 금지
-- DROP TABLE IF EXISTS interview_detail_feedback;
-- DROP TABLE IF EXISTS interview_message;
-- DROP TABLE IF EXISTS interview_recap_feedback;
-- DROP TABLE IF EXISTS interview_recap_problem;
-- DROP TABLE IF EXISTS interview_recap_session;
-- DROP TABLE IF EXISTS interview_result_summary;
-- DROP TABLE IF EXISTS interview_session;
-- DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users (
   id bigint primary key auto_increment,
   name varchar(100),
   email varchar(255) unique,
   password varchar(255),
   created_at datetime
);

CREATE TABLE IF NOT EXISTS interview_session (
    id bigint primary key auto_increment,
    started_at datetime,
    ended_at datetime,
    status varchar(50),
    u_id bigint,
    foreign key (u_id) references users(id)
       on delete cascade
       on update cascade
);

CREATE TABLE IF NOT EXISTS interview_result_summary (
    id bigint primary key auto_increment,
    overall_score double,
    overall_feedback text,
    created_at datetime,
    i_id bigint,
    foreign key (i_id) references interview_session(id)
      on delete cascade
      on update cascade
);

CREATE TABLE IF NOT EXISTS interview_message (
    id bigint primary key auto_increment,
    content text,
    created_at datetime,
    sender varchar(10), -- USER or LLM
    i_id bigint,
    foreign key (i_id) references interview_session(id)
        on delete cascade
        on update cascade
);
CREATE TABLE IF NOT EXISTS interview_recap_session (
    id bigint primary key auto_increment,
    started_at datetime,
    ended_at datetime,
    status varchar(50),
    u_id bigint,
    i_id bigint,
    foreign key (u_id) references users(id)
     on delete cascade
     on update cascade,
    foreign key (i_id) references interview_session(id)
     on delete cascade
     on update cascade
);

CREATE TABLE IF NOT EXISTS interview_recap_problem (
    id bigint primary key auto_increment,
    user_answer text,
    status varchar(20),
    created_at datetime,
    irs_id bigint,
    im_id bigint,
    foreign key (irs_id) references interview_recap_session(id)
    on delete cascade
    on update cascade,
    foreign key (im_id) references interview_message(id)
    on delete cascade
    on update cascade
);

CREATE TABLE IF NOT EXISTS interview_recap_feedback (
    id bigint primary key auto_increment,
    recap_feedback text,
    created_at datetime,
    irp_id bigint,
    foreign key (irp_id) references interview_recap_problem(id)
      on delete cascade
      on update cascade
);


CREATE TABLE IF NOT EXISTS interview_detail_feedback (
    id bigint primary key auto_increment,
    feedback_text text,
    score double,
    created_at datetime,
    llm_id bigint,
    user_id bigint,
    foreign key (llm_id) references interview_message(id)
    on delete cascade
    on update cascade,
    foreign key (user_id) references interview_message(id)
    on delete cascade
    on update cascade
);
CREATE TABLE  IF NOT EXISTS chat_memory (

 memory_id bigint PRIMARY KEY,

 messages_json TEXT

);
--
-- -- 유저 추가
-- INSERT INTO users (email, name, password, created_at)
-- VALUES
    --     ('testuser@example.com', '테스트 유저', '$2a$10$b2fYsaeG/hBBgYUAtg4sxeXtuacBfsqOJ4vpqYJUiF3c.B2Sf7Mcy', NOW()),
--     ('testuser2@example.com', '테스트 유저2', '$2a$10$b2fYsaeG/hBBgYUAtg4sxeXtuacBfsqOJ4vpqYJUiF3c.B2Sf7Mcy', NOW());
--
-- -- 인터뷰 세션 추가
-- INSERT INTO interview_session (status, started_at, u_id)
-- VALUES ('ACTIVE', NOW(), 1);
--
-- -- 인터뷰 메시지 (질문과 답변) 추가
-- INSERT INTO interview_message (i_id, sender, content, created_at) VALUES
-- -- 1. @Transactiona  l
-- (1, 'LLM', 'Spring에서 @Transactional 어노테이션이 어떻게 작동하는지 설명해보세요. 그리고 어떤 경우에 예상과 다르게 롤백이 되지 않는지 예시를 들어주세요.', NOW()),
-- (1, 'USER', '@Transactional은 메서드 실행을 하나의 트랜잭션으로 묶어주는 기능입니다. 메서드가 정상적으로 완료되면 커밋되고, 예외가 발생하면 롤백됩니다. 하지만 체크 예외는 기본적으로 롤백되지 않으며, 런타임 예외나 에러만 롤백됩니다. 예를 들어 try-catch로 예외를 처리하거나, @Transactional(rollbackFor = Exception.class)와 같이 롤백 조건을 명시하지 않으면 트랜잭션이 커밋될 수 있습니다.', NOW()),
--
-- -- 2. DI
-- (1, 'LLM', 'Spring에서 DI(Dependency Injection)란 무엇인가요? 그리고 어떻게 사용되나요?', NOW()),
-- (1, 'USER', 'DI는 객체 간의 의존 관계를 외부에서 주입하는 방법입니다. Spring에서는 @Autowired, @Inject, XML 설정 등을 사용하여 객체를 주입할 수 있습니다. 이를 통해 객체 생성과 관리가 Spring 컨테이너에 의해 처리되며, 코드 간의 결합도를 낮추어 유연성과 테스트 용이성을 증가시킵니다.', NOW()),
--
-- -- 3. Component, Service, Repository, Controller 차이
-- (1, 'LLM', 'Spring에서 @Component, @Service, @Repository, @Controller 어노테이션의 차이를 설명해주세요.', NOW()),
-- (1, 'USER', '이 어노테이션들은 모두 빈(bean)을 정의하는 데 사용되지만, 각 역할에 차이가 있습니다. @Component는 일반적인 빈을 정의하는 데 사용되고, @Service는 서비스 레이어의 빈, @Repository는 데이터 액세스 레이어의 빈, @Controller는 웹 컨트롤러 레이어의 빈을 정의하는 데 사용됩니다.', NOW()),
--
-- -- 4. AOP
-- (1, 'LLM', 'Spring에서 AOP(Aspect-Oriented Programming)란 무엇인가요? 어떤 경우에 유용하게 사용될 수 있나요?', NOW()),
-- (1, 'USER', 'AOP는 횡단 관심사(cross-cutting concern)를 모듈화하는 방법입니다. 예를 들어 로깅, 트랜잭션 관리, 보안 등을 비즈니스 로직과 분리하여 코드의 재사용성을 높이고 유지 보수를 쉽게 할 수 있습니다.', NOW()),
--
-- -- 5. @Value
-- (1, 'LLM', 'Spring에서 @Value 어노테이션은 무엇을 위한 것인가요?', NOW()),
-- (1, 'USER', '@Value 어노테이션은 프로퍼티 값을 주입하는 데 사용됩니다. 예를 들어 application.properties 또는 application.yml 파일에서 정의된 값을 클래스의 필드에 주입할 수 있습니다.', NOW()),
--
-- -- 6. @SpringBootApplication
-- (1, 'LLM', 'Spring Boot에서 @SpringBootApplication 어노테이션의 역할은 무엇인가요?', NOW()),
-- (1, 'USER', '@SpringBootApplication은 @Configuration, @EnableAutoConfiguration, @ComponentScan을 포함하는 메타 어노테이션입니다. Spring Boot 애플리케이션의 시작 지점을 지정하고, 자동 설정과 컴포넌트 스캔을 활성화하여 애플리케이션을 쉽게 시작할 수 있게 합니다.', NOW()),
--
-- -- 7. RequestMapping vs Get/PostMapping
-- (1, 'LLM', 'Spring에서 @RequestMapping과 @GetMapping, @PostMapping의 차이는 무엇인가요?', NOW()),
-- (1, 'USER', '@RequestMapping은 HTTP 요청의 URL, 메서드(GET, POST 등)를 지정하는 데 사용됩니다. @GetMapping, @PostMapping은 각각 GET, POST 요청을 처리하는 축약형 어노테이션으로, 코드의 가독성을 높이고 직관적으로 사용할 수 있게 합니다.', NOW()),
--
-- -- 8. @PreAuthorize vs @Secured
-- (1, 'LLM', 'Spring Security에서 @PreAuthorize와 @Secured 어노테이션의 차이를 설명해주세요.', NOW()),
-- (1, 'USER', '@PreAuthorize는 표현식 기반의 권한 제어를 제공하며, @Secured는 특정 역할에 대한 접근 제어를 정의하는 데 사용됩니다. @PreAuthorize는 더 유연하고 강력한 제어를 할 수 있지만, @Secured는 간단한 역할 기반 접근 제어를 위해 사용됩니다.', NOW()),
--
-- -- 9. application.properties vs application.yml
-- (1, 'LLM', 'Spring Boot에서 application.properties와 application.yml 파일의 차이는 무엇인가요?', NOW()),
-- (1, 'USER', '두 파일은 모두 Spring Boot 애플리케이션의 설정 파일입니다. application.properties는 키-값 쌍의 형태로 설정을 관리하고, application.yml은 YAML 형식으로 설정을 관리합니다. YAML은 계층적인 데이터를 더 쉽게 표현할 수 있습니다.', NOW()),
--
-- -- 10. Bean Life Cycle
-- (1, 'LLM', 'Spring의 Bean Life Cycle에 대해 설명해주세요.', NOW()),
-- (1, 'USER', 'Spring의 Bean Life Cycle은 Bean의 생성부터 소멸까지의 과정입니다. 이 과정에서 생성자 호출, 의존성 주입, 초기화, 소멸 메소드 등이 포함됩니다. 주요 단계는 @PostConstruct와 @PreDestroy 어노테이션을 사용하여 초기화 및 소멸 작업을 처리할 수 있습니다.', NOW()),
--
-- -- 11. 예외 처리
-- (1, 'LLM', 'Spring에서의 예외 처리 방법에 대해 설명해주세요.', NOW()),
-- (1, 'USER', 'Spring에서는 @ExceptionHandler, @ControllerAdvice 등을 사용하여 예외를 처리할 수 있습니다. @ExceptionHandler는 컨트롤러 내에서 발생한 예외를 처리하고, @ControllerAdvice는 전역적으로 예외를 처리할 수 있습니다.', NOW()),
--
-- -- 12. 프로파일
-- (1, 'LLM', 'Spring Boot에서 프로파일(Profile)은 무엇인가요? 어떻게 활용되나요?', NOW()),
-- (1, 'USER', 'Spring Boot에서 프로파일은 환경에 맞는 설정을 다르게 적용하는 방법입니다. 예를 들어 개발 환경에서는 `application-dev.properties` 파일을 사용하고, 배포 환경에서는 `application-prod.properties` 파일을 사용하여 각 환경에 맞는 설정을 적용할 수 있습니다.', NOW()),
--
-- -- 13. Spring Batch Chunk Processing
-- (1, 'LLM', 'Spring Batch에서 Chunk Processing이란 무엇인가요?', NOW()),
-- (1, 'USER', 'Chunk Processing은 대용량 데이터를 배치 처리하는 방식으로, 데이터를 일정한 크기로 나누어 처리하고, 처리한 후 커밋하는 방식입니다. 이를 통해 메모리 사용을 최적화하고 성능을 향상시킬 수 있습니다.', NOW()),
--
-- -- 14. @Entity
-- (1, 'LLM', 'Spring에서 @Entity 어노테이션은 무엇인가요?', NOW()),
-- (1, 'USER', '@Entity 어노테이션은 해당 클래스가 JPA 엔티티임을 나타내는 어노테이션입니다. JPA는 이 클래스와 연결된 테이블을 생성하고, 객체와 관계형 데이터베이스 간의 매핑을 처리합니다.', NOW());
-- -- 15. 기타
-- (1, 'LLM', '자바스크립트의 실행 컨텍스트에 대해 대답해주세요', NOW()),
-- (1, 'USER', '실행 컨텍스트란.. "실행 컨텍스트"다..', NOW());
