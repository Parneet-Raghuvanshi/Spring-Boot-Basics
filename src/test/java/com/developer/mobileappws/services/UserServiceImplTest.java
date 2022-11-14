package com.developer.mobileappws.services;

import com.developer.mobileappws.dto.AddressDto;
import com.developer.mobileappws.dto.UserDto;
import com.developer.mobileappws.entity.AddressEntity;
import com.developer.mobileappws.entity.UserEntity;
import com.developer.mobileappws.repository.UserRepository;
import com.developer.mobileappws.utility.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    Utils utils;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    String userId = "uy5gnj0fg5bn41dsd";
    String encryptedPassword = "vsdvsdcvs5dc154c7zx4c1zxczs4c";
    UserEntity userEntity;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);

        userEntity = new UserEntity();
        userEntity.setUserId(userId);
        userEntity.setId(1L);
        userEntity.setFirstName("Parneet");
        userEntity.setLastName("Raghuvanshi");
        userEntity.setEncryptedPassword(encryptedPassword);
        userEntity.setEmail("test@test.com");
        userEntity.setEmailVerificationToken("sdvdsv4sdv14xc4v7xcv");
        userEntity.setAddresses(getAddressesEntity());
    }

    @Test
    final void testGetUser() {
        when(userRepository.findUserByEmail(anyString())).thenReturn(userEntity);

        UserDto userDto = userService.getUser("test@test.com");
        assertNotNull(userDto);
        assertEquals("Parneet",userDto.getFirstName());
    }

    @Test
    final void testGetUser_UsernameNotFoundException() {
        when(userRepository.findUserByEmail(anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,
                ()-> userService.getUser("test@test.com"));
    }

    @Test
    final void testCreateUser() {
        when(userRepository.findUserByEmail(anyString())).thenReturn(null);
        when(utils.generateAddressId(anyInt())).thenReturn("d2sd6csdc1sd1cds");
        when(utils.generateUserId(anyInt())).thenReturn(userId);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        UserDto userDto = new UserDto();
        userDto.setAddresses(getAddressesDto());
        userDto.setFirstName("Parneet");
        userDto.setLastName("Raghuvanshi");
        userDto.setPassword("12345678");
        userDto.setEmail("test@test.com");

        UserDto storesUserDetails = userService.createUser(userDto);
        assertNotNull(storesUserDetails);
        assertEquals(userEntity.getFirstName(),storesUserDetails.getFirstName());
        assertEquals(userEntity.getLastName(),storesUserDetails.getLastName());
        assertNotNull(storesUserDetails.getUserId());
        assertEquals(storesUserDetails.getAddresses().size(),userEntity.getAddresses().size());
        verify(utils,times(storesUserDetails.getAddresses().size())).generateAddressId(30);
        verify(utils,times(1)).generateUserId(30);
        verify(bCryptPasswordEncoder,times(1)).encode(anyString());
        verify(userRepository,times(1)).save(any(UserEntity.class));
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

    private List<AddressEntity> getAddressesEntity() {
        List<AddressDto> addresses = getAddressesDto();
        Type listType = new TypeToken<List<AddressEntity>>() {}.getType();
        return new ModelMapper().map(addresses,listType);
    }
}
