package com.developer.mobileappws.repository;

import com.developer.mobileappws.entity.PasswordResetTokenEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetTokenEntity,Long> {
    PasswordResetTokenEntity findByToken(String token);
}
