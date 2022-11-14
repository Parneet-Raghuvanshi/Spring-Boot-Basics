package com.developer.mobileappws.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class UserDto implements Serializable {
    // Data Transfer Object
    @Serial
    private static final long serialVersionUID = 709583653406642112L;
    private long id;
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String password;
    private String encryptedPassword;
    private String emailVerificationToken;
    private Boolean emailVerificationStatus = false;
    private List<AddressDto> addresses;
}
