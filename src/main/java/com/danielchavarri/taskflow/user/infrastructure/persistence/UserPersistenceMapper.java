
package com.danielchavarri.taskflow.user.infrastructure.persistence;

import com.danielchavarri.taskflow.user.domain.model.User;

public final class UserPersistenceMapper {

    private UserPersistenceMapper() {
    }

    public static UserJpaEntity toEntity(User user) {
        return new UserJpaEntity(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getCreatedAt());
    }

    public static User toDomain(UserJpaEntity entity) {
        return User.reconstitute(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getCreatedAt());
    }
}