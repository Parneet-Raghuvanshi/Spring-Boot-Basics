package com.developer.mobileappws.controller;

import com.developer.mobileappws.dto.AddressDto;
import com.developer.mobileappws.dto.UserDto;
import com.developer.mobileappws.models.response.UserRest;
import com.developer.mobileappws.services.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    @InjectMocks
    UserController userController;

    @Mock
    UserServiceImpl userService;

    UserDto userDto;
    final String USER_ID = "fv5df1v47x1v85c4";

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);

        userDto = new UserDto();
        userDto.setEmail("test@test.com");
        userDto.setFirstName("Parneet");
        userDto.setLastName("Raghuvanshi");
        userDto.setEmailVerificationStatus(Boolean.FALSE);
        userDto.setEmailVerificationToken(null);
        userDto.setUserId(USER_ID);
        userDto.setAddresses(getAddressesDto());
        userDto.setEncryptedPassword("sdvdsv21xx8v4c");
    }

    @Test
    final void testGetUser() {
        when(userService.getUserByUserId(anyString())).thenReturn(userDto);

        UserRest userRest = userController.getUser(USER_ID);
        assertNotNull(userRest);
        assertEquals(USER_ID,userRest.getUserId());
        assertEquals(userDto.getFirstName(),userRest.getFirstName());
        assertEquals(userDto.getLastName(), userRest.getLastName());
        assertEquals(userDto.getAddresses().size(), userRest.getAddresses().size());
    }

    private List<AddressDto> getAddressesDto() {
        AddressDto shippingAddressDto = new AddressDto();
        shippingAddressDto.setType("shipping");
        shippingAddressDto.setCity("Ghaziabad");
        shippingAddressDto.setCountry("India");
        shippingAddressDto.setPostalCode("202150522");
        shippingAddressDto.setStreetName("cdjc cdk cdkm");

        AddressDto billingAddressDto = new AddressDto();
        billingAddressDto.setType("billing");
        billingAddressDto.setCity("Ghaziabad");
        billingAddressDto.setCountry("India");
        billingAddressDto.setPostalCode("202150522");
        billingAddressDto.setStreetName("cdjc cdk cdkm");

        List<AddressDto> addresses = new ArrayList<>();
        addresses.add(shippingAddressDto);
        addresses.add(billingAddressDto);
        return addresses;
    }
}
