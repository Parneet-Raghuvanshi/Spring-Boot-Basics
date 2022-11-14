package com.developer.mobileappws.repository;

import com.developer.mobileappws.entity.AddressEntity;
import com.developer.mobileappws.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends CrudRepository<AddressEntity,Long> {
    List<AddressEntity> findAllAddressByUserDetails(UserEntity userEntity);
    AddressEntity findAddressByAddressId(String addressId);
}
