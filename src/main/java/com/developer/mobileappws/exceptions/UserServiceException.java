package com.developer.mobileappws.exceptions;

import java.io.Serial;

public class UserServiceException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 7559403017852156366L;

    public UserServiceException(String message) {
        super(message);
    }
}
