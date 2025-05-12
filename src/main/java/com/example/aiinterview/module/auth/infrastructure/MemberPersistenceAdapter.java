package com.example.aiinterview.module.auth.infrastructure;

import com.example.aiinterview.module.auth.domain.port.MemberRetrievalPort;
import com.example.aiinterview.module.user.domain.entity.User;
import com.example.aiinterview.module.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MemberPersistenceAdapter implements MemberRetrievalPort {
    private final UserRepository userRepository;


    @Override
    public Mono<User> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(() -> new RuntimeException("User not found")));
    }
}
