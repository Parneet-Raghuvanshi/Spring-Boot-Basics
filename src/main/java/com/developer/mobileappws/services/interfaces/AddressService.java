package com.developer.mobileappws.services.interfaces;

import com.developer.mobileappws.dto.AddressDto;

import java.util.List;

public interface AddressService {
    List<AddressDto> getAddresses(String userId);
    AddressDto getAddress(String addressId);
}
