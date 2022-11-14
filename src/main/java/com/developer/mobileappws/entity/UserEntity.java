package com.developer.mobileappws.entity;

import lombok.*;
import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Entity(name = "users")
@Getter
@Setter
public class UserEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 8805756290168434150L;

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false,length = 50)
    private String firstName;

    @Column(nullable = false,length = 50)
    private String lastName;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false,length = 120)
    private String email;

    @Column(nullable = false)
    private String encryptedPassword;

    private String emailVerificationToken;

    @Column(nullable = false)
    private Boolean emailVerificationStatus = false;

    @OneToMany(mappedBy = "userDetails",cascade = CascadeType.ALL)
    private List<AddressEntity> addresses;
}
