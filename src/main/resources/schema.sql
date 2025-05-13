CREATE TABLE IF NOT EXISTS user (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   email VARCHAR(255) UNIQUE,
    name VARCHAR(255),
    password VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS interview_session (
      id BIGINT AUTO_INCREMENT PRIMARY KEY,
      interviewee_id BIGINT,  -- 🔥 여기에 FK 추가
      status VARCHAR(20),
    feedback TEXT,
    created_at TIMESTAMP,
    FOREIGN KEY (interviewee_id) REFERENCES user(id) ON DELETE SET NULL
    );

CREATE TABLE IF NOT EXISTS interview_messages (
      id BIGINT AUTO_INCREMENT PRIMARY KEY,
      room_id BIGINT,
      sender VARCHAR(255),
        message TEXT,
        created_at TIMESTAMP,
        FOREIGN KEY (room_id) REFERENCES interview_session(id) ON DELETE CASCADE
    );

INSERT INTO user (email, name, password, created_at, updated_at)
VALUES ('testuser@example.com', '테스트 유저', '$2a$10$b2fYsaeG/hBBgYUAtg4sxeXtuacBfsqOJ4vpqYJUiF3c.B2Sf7Mcy', NOW(), NOW());

INSERT INTO user (email, name, password, created_at, updated_at)
VALUES ('testuser2@example.com', '테스트 유저', '$2a$10$b2fYsaeG/hBBgYUAtg4sxeXtuacBfsqOJ4vpqYJUiF3c.B2Sf7Mcy', NOW(), NOW());

INSERT INTO interview_session(interviewee_id,status,feedback,created_at)
values (1, 'ACTIVE', null, NOW());

INSERT INTO interview_messages(room_id, sender, message, created_at)
VALUES (1, 'LLM', 'Spring에서 @Transactional 어노테이션이 어떻게 작동하는지 설명해보세요. 그리고 어떤 경우에 예상과 다르게 롤백이 되지 않는지 예시를 들어주세요.', NOW());

INSERT INTO interview_messages(room_id, sender, message, created_at)
VALUES (1, 'USER', '@Transactional은 메서드 실행을 하나의 트랜잭션으로 묶어주는 기능입니다. 메서드가 정상적으로 완료되면 커밋되고, 예외가 발생하면 롤백됩니다. 하지만 체크 예외는 기본적으로 롤백되지 않으며, 런타임 예외나 에러만 롤백됩니다. 예를 들어 try-catch로 예외를 처리하거나, @Transactional(rollbackFor = Exception.class)와 같이 롤백 조건을 명시하지 않으면 트랜잭션이 커밋될 수 있습니다.', NOW());

-- 2. DI(Dependency Injection)
INSERT INTO interview_messages(room_id, sender, message, created_at)
VALUES (1, 'LLM', 'Spring에서 DI(Dependency Injection)란 무엇인가요? 그리고 어떻게 사용되나요?', NOW());

INSERT INTO interview_messages(room_id, sender, message, created_at)
VALUES (1, 'USER', 'DI는 객체 간의 의존 관계를 외부에서 주입하는 방법입니다. Spring에서는 @Autowired, @Inject, XML 설정 등을 사용하여 객체를 주입할 수 있습니다. 이를 통해 객체 생성과 관리가 Spring 컨테이너에 의해 처리되며, 코드 간의 결합도를 낮추어 유연성과 테스트 용이성을 증가시킵니다.', NOW());

-- 3. Component와 Service, Repository, Controller 차이
INSERT INTO interview_messages(room_id, sender, message, created_at)
VALUES (1, 'LLM', 'Spring에서 @Component, @Service, @Repository, @Controller 어노테이션의 차이를 설명해주세요.', NOW());

INSERT INTO interview_messages(room_id, sender, message, created_at)
VALUES (1, 'USER', '이 어노테이션들은 모두 빈(bean)을 정의하는 데 사용되지만, 각 역할에 차이가 있습니다. @Component는 일반적인 빈을 정의하는 데 사용되고, @Service는 서비스 레이어의 빈, @Repository는 데이터 액세스 레이어의 빈, @Controller는 웹 컨트롤러 레이어의 빈을 정의하는 데 사용됩니다.', NOW());

-- 4. AOP(Aspect-Oriented Programming)
INSERT INTO interview_messages(room_id, sender, message, created_at)
VALUES (1, 'LLM', 'Spring에서 AOP(Aspect-Oriented Programming)란 무엇인가요? 어떤 경우에 유용하게 사용될 수 있나요?', NOW());

INSERT INTO interview_messages(room_id, sender, message, created_at)
VALUES (1, 'USER', 'AOP는 횡단 관심사(cross-cutting concern)를 모듈화하는 방법입니다. 예를 들어 로깅, 트랜잭션 관리, 보안 등을 비즈니스 로직과 분리하여 코드의 재사용성을 높이고 유지 보수를 쉽게 할 수 있습니다.', NOW());

-- 5. @Value 어노테이션
INSERT INTO interview_messages(room_id, sender, message, created_at)
VALUES (1, 'LLM', 'Spring에서 @Value 어노테이션은 무엇을 위한 것인가요?', NOW());

INSERT INTO interview_messages(room_id, sender, message, created_at)
VALUES (1, 'USER', '@Value 어노테이션은 프로퍼티 값을 주입하는 데 사용됩니다. 예를 들어 application.properties 또는 application.yml 파일에서 정의된 값을 클래스의 필드에 주입할 수 있습니다.', NOW());

-- 6. @SpringBootApplication 어노테이션
INSERT INTO interview_messages(room_id, sender, message, created_at)
VALUES (1, 'LLM', 'Spring Boot에서 @SpringBootApplication 어노테이션의 역할은 무엇인가요?', NOW());

INSERT INTO interview_messages(room_id, sender, message, created_at)
VALUES (1, 'USER', '@SpringBootApplication은 @Configuration, @EnableAutoConfiguration, @ComponentScan을 포함하는 메타 어노테이션입니다. Spring Boot 애플리케이션의 시작 지점을 지정하고, 자동 설정과 컴포넌트 스캔을 활성화하여 애플리케이션을 쉽게 시작할 수 있게 합니다.', NOW());

-- 7. @RequestMapping, @GetMapping, @PostMapping 차이
INSERT INTO interview_messages(room_id, sender, message, created_at)
VALUES (1, 'LLM', 'Spring에서 @RequestMapping과 @GetMapping, @PostMapping의 차이는 무엇인가요?', NOW());

INSERT INTO interview_messages(room_id, sender, message, created_at)
VALUES (1, 'USER', '@RequestMapping은 HTTP 요청의 URL, 메서드(GET, POST 등)를 지정하는 데 사용됩니다. @GetMapping, @PostMapping은 각각 GET, POST 요청을 처리하는 축약형 어노테이션으로, 코드의 가독성을 높이고 직관적으로 사용할 수 있게 합니다.', NOW());

-- 8. @PreAuthorize와 @Secured 어노테이션의 차이
INSERT INTO interview_messages(room_id, sender, message, created_at)
VALUES (1, 'LLM', 'Spring Security에서 @PreAuthorize와 @Secured 어노테이션의 차이를 설명해주세요.', NOW());

INSERT INTO interview_messages(room_id, sender, message, created_at)
VALUES (1, 'USER', '@PreAuthorize는 표현식 기반의 권한 제어를 제공하며, @Secured는 특정 역할에 대한 접근 제어를 정의하는 데 사용됩니다. @PreAuthorize는 더 유연하고 강력한 제어를 할 수 있지만, @Secured는 간단한 역할 기반 접근 제어를 위해 사용됩니다.', NOW());

-- 9. application.properties와 application.yml 차이
INSERT INTO interview_messages(room_id, sender, message, created_at)
VALUES (1, 'LLM', 'Spring Boot에서 application.properties와 application.yml 파일의 차이는 무엇인가요?', NOW());

INSERT INTO interview_messages(room_id, sender, message, created_at)
VALUES (1, 'USER', '두 파일은 모두 Spring Boot 애플리케이션의 설정 파일입니다. application.properties는 키-값 쌍의 형태로 설정을 관리하고, application.yml은 YAML 형식으로 설정을 관리합니다. YAML은 계층적인 데이터를 더 쉽게 표현할 수 있습니다.', NOW());

-- 10. Spring의 Bean Life Cycle
INSERT INTO interview_messages(room_id, sender, message, created_at)
VALUES (1, 'LLM', 'Spring의 Bean Life Cycle에 대해 설명해주세요.', NOW());

INSERT INTO interview_messages(room_id, sender, message, created_at)
VALUES (1, 'USER', 'Spring의 Bean Life Cycle은 Bean의 생성부터 소멸까지의 과정입니다. 이 과정에서 생성자 호출, 의존성 주입, 초기화, 소멸 메소드 등이 포함됩니다. 주요 단계는 @PostConstruct와 @PreDestroy 어노테이션을 사용하여 초기화 및 소멸 작업을 처리할 수 있습니다.', NOW());

-- 11. Spring에서의 예외 처리
INSERT INTO interview_messages(room_id, sender, message, created_at)
VALUES (1, 'LLM', 'Spring에서의 예외 처리 방법에 대해 설명해주세요.', NOW());

INSERT INTO interview_messages(room_id, sender, message, created_at)
VALUES (1, 'USER', 'Spring에서는 @ExceptionHandler, @ControllerAdvice 등을 사용하여 예외를 처리할 수 있습니다. @ExceptionHandler는 컨트롤러 내에서 발생한 예외를 처리하고, @ControllerAdvice는 전역적으로 예외를 처리할 수 있습니다.', NOW());

-- 12. Spring Boot에서의 프로파일(Profile)
INSERT INTO interview_messages(room_id, sender, message, created_at)
VALUES (1, 'LLM', 'Spring Boot에서 프로파일(Profile)은 무엇인가요? 어떻게 활용되나요?', NOW());

INSERT INTO interview_messages(room_id, sender, message, created_at)
VALUES (1, 'USER', 'Spring Boot에서 프로파일은 환경에 맞는 설정을 다르게 적용하는 방법입니다. 예를 들어 개발 환경에서는 `application-dev.properties` 파일을 사용하고, 배포 환경에서는 `application-prod.properties` 파일을 사용하여 각 환경에 맞는 설정을 적용할 수 있습니다.', NOW());

-- 13. Spring Batch에서 Chunk Processing
INSERT INTO interview_messages(room_id, sender, message, created_at)
VALUES (1, 'LLM', 'Spring Batch에서 Chunk Processing이란 무엇인가요?', NOW());

INSERT INTO interview_messages(room_id, sender, message, created_at)
VALUES (1, 'USER', 'Chunk Processing은 대용량 데이터를 배치 처리하는 방식으로, 데이터를 일정한 크기로 나누어 처리하고, 처리한 후 커밋하는 방식입니다. 이를 통해 메모리 사용을 최적화하고 성능을 향상시킬 수 있습니다.', NOW());

-- 14. Spring의 @Entity 어노테이션
INSERT INTO interview_messages(room_id, sender, message, created_at)
VALUES (1, 'LLM', 'Spring에서 @Entity 어노테이션은 무엇인가요?', NOW());

INSERT INTO interview_messages(room_id, sender, message, created_at)
VALUES (1, 'USER', '@Entity 어노테이션은 해당 클래스가 JPA 엔티티임을 나타내는 어노테이션입니다. JPA는 이 클래스와 연결된 테이블을 생성하고, 객체와 관계형 데이터베이스 간의 매핑을 처리합니다.', NOW());
