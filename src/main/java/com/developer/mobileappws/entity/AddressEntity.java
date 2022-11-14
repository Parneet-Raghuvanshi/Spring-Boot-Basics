package com.developer.mobileappws.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Entity(name = "addresses")
@Getter
@Setter
public class AddressEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = -6397787648715178266L;

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false,length = 30)
    private String addressId;

    @Column(nullable = false,length = 15)
    private String city;

    @Column(nullable = false,length = 15)
    private String country;

    @Column(nullable = false,length = 100)
    private String streetName;

    @Column(nullable = false,length = 7)
    private String postalCode;

    @Column(nullable = false,length = 10)
    private String type;

    @ManyToOne
    @JoinColumn(name = "users_id")
    private UserEntity userDetails;
}
