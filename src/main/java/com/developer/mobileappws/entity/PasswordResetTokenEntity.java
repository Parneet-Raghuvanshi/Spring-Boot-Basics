package com.developer.mobileappws.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Entity(name = "password_reset_tokens")
public class PasswordResetTokenEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = -5056454534043770694L;

    @Id
    @GeneratedValue
    private long id;

    private String token;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity userDetails;
}
