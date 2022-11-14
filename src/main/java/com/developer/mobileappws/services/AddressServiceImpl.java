package com.developer.mobileappws.services;

import com.developer.mobileappws.dto.AddressDto;
import com.developer.mobileappws.entity.AddressEntity;
import com.developer.mobileappws.entity.UserEntity;
import com.developer.mobileappws.repository.AddressRepository;
import com.developer.mobileappws.repository.UserRepository;
import com.developer.mobileappws.services.interfaces.AddressService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AddressRepository addressRepository;

    @Override
    public List<AddressDto> getAddresses(String userId) {
        List<AddressDto> returnValue = new ArrayList<>();
        ModelMapper mapper = new ModelMapper();

        UserEntity userEntity = userRepository.findUserByUserId(userId);
        if (userEntity == null) return returnValue;

        List<AddressEntity> addresses = addressRepository.findAllAddressByUserDetails(userEntity);

        for (AddressEntity e : addresses) {
            returnValue.add(mapper.map(e,AddressDto.class));
        }

        return returnValue;
    }

    @Override
    public AddressDto getAddress(String addressId) {
        AddressDto returnValue = new AddressDto();
        AddressEntity addressEntity = addressRepository.findAddressByAddressId(addressId);
        if (addressEntity != null) {
            returnValue = new ModelMapper().map(addressEntity,AddressDto.class);
        }
        return returnValue;
    }
}
