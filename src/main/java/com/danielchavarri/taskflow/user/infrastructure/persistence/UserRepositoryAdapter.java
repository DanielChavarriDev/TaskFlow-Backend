
package com.danielchavarri.taskflow.user.infrastructure.persistence;

import com.danielchavarri.taskflow.user.domain.model.User;
import com.danielchavarri.taskflow.user.domain.port.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository springDataRepository;

    public UserRepositoryAdapter(
            SpringDataUserRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = UserPersistenceMapper.toEntity(user);
        UserJpaEntity savedEntity = springDataRepository.save(entity);

        return UserPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return springDataRepository.findByEmail(email)
                .map(UserPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return springDataRepository.existsByEmail(email);
    }
}