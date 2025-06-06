package com.example.aiinterview.module.user.application;

import com.example.aiinterview.global.common.response.dto.ApiResponse;
import com.example.aiinterview.module.user.application.dto.CreateMemberRequest;
import com.example.aiinterview.module.user.domain.entity.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<User> create(CreateMemberRequest request);
    Mono<User> findAll();

    Mono<Void> delete(Long userId);
}
