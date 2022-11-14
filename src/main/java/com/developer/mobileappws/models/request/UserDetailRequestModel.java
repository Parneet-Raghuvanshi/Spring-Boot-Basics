package com.developer.mobileappws.models.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDetailRequestModel {
    // DAO - Data Access Object
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;
    private List<AddressRequestModel> addresses;
}
