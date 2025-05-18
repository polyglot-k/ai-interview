package com.example.aiinterview.module.user.application;

import com.example.aiinterview.module.user.application.dto.CreateMemberRequest;
import com.example.aiinterview.module.user.domain.entity.User;
import com.example.aiinterview.module.user.exception.DuplicateEmailException;
import com.example.aiinterview.module.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Mono<User> create(CreateMemberRequest request) {
        String email = request.email();

        return userRepository.findByEmail(email)
                .flatMap(user -> Mono.<User>error(new DuplicateEmailException()))
                .switchIfEmpty(saveNewUser(request));
    }

    private Mono<User> saveNewUser(CreateMemberRequest request) {
        User newUser = User.create(
                request.email(),
                request.name(),
                request.password()
        );
        return userRepository.save(newUser);
    }
}
