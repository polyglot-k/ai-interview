package com.example.aiinterview.module.user.application;

import com.example.aiinterview.module.user.application.dto.CreateMemberRequest;
import com.example.aiinterview.module.user.domain.entity.User;
import com.example.aiinterview.module.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    @Override
    public Mono<User> create(CreateMemberRequest request) {
        User user = User.create(
                request.email(),
                request.name(),
                request.password()
        );
        return userRepository.save(user);
    }
}
