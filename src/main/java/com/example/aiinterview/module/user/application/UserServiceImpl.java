package com.example.aiinterview.module.user.application;

import com.example.aiinterview.global.common.response.dto.ApiResponse;
import com.example.aiinterview.global.common.utils.CryptUtils;
import com.example.aiinterview.module.user.application.dto.CreateMemberRequest;
import com.example.aiinterview.module.user.domain.entity.User;
import com.example.aiinterview.module.user.exception.DuplicateEmailException;
import com.example.aiinterview.module.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Mono<User> create(CreateMemberRequest request) {
        String email = request.email();

        return userRepository.findByEmail(email)
                .flatMap(user -> Mono.<User>error(new DuplicateEmailException()))
                .switchIfEmpty(Mono.defer(() -> saveNewUser(request)).subscribeOn(Schedulers.boundedElastic()));
    }

    @Override
    public Mono<User> findAll() {
        return userRepository.findById(100L);
    }

    @Override
    public Mono<Void> delete(Long userId) {
        return userRepository.deleteById(userId)
                .then();
    }

    private Mono<User> saveNewUser(CreateMemberRequest request) {
        return Mono.fromCallable(() -> {
            String hashedPassword = CryptUtils.hashPassword(request.password());
            User newUser = User.create(
                    request.email(),
                    request.name(),
                    hashedPassword
            );
            return userRepository.save(newUser);
        }).flatMap(Mono::from);
    }
}
