package com.developer.mobileappws.repository;

import com.developer.mobileappws.entity.UserEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity,Long> {
    UserEntity findUserByEmail(String email);
    UserEntity findUserByUserId(String userId);
    UserEntity findUserByEmailVerificationToken(String token);
}
