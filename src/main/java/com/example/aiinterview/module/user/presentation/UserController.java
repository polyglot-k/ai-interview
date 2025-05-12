package com.example.aiinterview.module.user.presentation;

import com.example.aiinterview.module.user.application.UserService;
import com.example.aiinterview.module.user.application.dto.CreateMemberRequest;
import com.example.aiinterview.module.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping()
    public Mono<String> test(){
        return Mono.just("hello");
    }

    @PostMapping()
    public Mono<User> create(@RequestBody CreateMemberRequest request){
        return userService.create(request);
    }
}
