package com.infina.hissenet.entity;

import com.infina.hissenet.entity.base.BaseEntity;
import com.infina.hissenet.entity.enums.AddressType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;


@Entity
@Table(name = "addresses")
public class Address extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "address_type", nullable = false)
    private AddressType addressType;

    @NotBlank
    @Column(name = "street", nullable = false, length = 200)
    private String street;

    @Column(name = "district", length = 100)
    private String district;

    @NotBlank
    @Column(name = "city", nullable = false, length = 50)
    private String city;

    @NotBlank
    @Column(name = "state", nullable = false, length = 50)
    private String state;

    @NotBlank
    @Column(name = "country", nullable = false, length = 50)
    private String country = "Turkey";

    @Pattern(regexp = "^[0-9]{5}$")
    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    public String getFullAddress() {
        return String.format("%s, %s, %s/%s %s",
                street, district, city, state, postalCode);
    }

    public AddressType getAddressType() {
        return addressType;
    }

    public void setAddressType(AddressType addressType) {
        this.addressType = addressType;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Boolean getPrimary() {
        return isPrimary;
    }

    public void setPrimary(Boolean primary) {
        isPrimary = primary;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}