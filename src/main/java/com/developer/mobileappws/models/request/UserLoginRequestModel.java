package com.developer.mobileappws.models.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequestModel {
    private String email;
    private String password;
}
