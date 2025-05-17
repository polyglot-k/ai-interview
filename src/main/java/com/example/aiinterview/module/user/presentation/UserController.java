package com.example.aiinterview.module.user.presentation;

import com.example.aiinterview.module.user.application.UserService;
import com.example.aiinterview.module.user.application.dto.CreateMemberRequest;
import com.example.aiinterview.module.user.domain.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name="USER API", description = "유저 관련 기능을 제공하는 API 입니다.")
public class UserController {
    private final UserService userService;
    @GetMapping()
    @Operation(summary = "테스트", description = "테스트 엔드포인트")
    public Mono<String> test(){
        return Mono.just("hello");
    }

    @PostMapping()
    @Operation(summary = "유저 생성", description = "유저 가입 정보를 입력하여 유저를 생성합니다.")
    public Mono<User> create(@RequestBody CreateMemberRequest request){
        return userService.create(request);
    }
}
