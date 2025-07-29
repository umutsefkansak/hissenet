package com.infina.hissenet.entity;


import com.infina.hissenet.entity.base.BaseEntity;
import com.infina.hissenet.entity.enums.CustomerType;
import com.infina.hissenet.entity.enums.RiskProfile;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "customers", indexes = {
        @Index(name = "idx_customer_email", columnList = "email"),
        @Index(name = "idx_customer_tc_number", columnList = "tcNumber"),
        @Index(name = "idx_customer_customer_number", columnList = "customerNumber")
})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "customer_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Customer extends BaseEntity {



    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type", nullable = false, insertable = false, updatable = false)
    private CustomerType customerType;

    @Column(name = "customer_number", nullable = false, unique = true, length = 20)
    private String customerNumber; // Otomatik oluşturulacak müşteri numarası

    @Email
    @NotBlank
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$")
    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "nationality", length = 50)
    private String nationality = "TR";


    @Column(name = "kyc_verified", nullable = false)
    private Boolean kycVerified = false;

    @Column(name = "kyc_verified_at")
    private LocalDate kycVerifiedAt;

    @Column(name = "risk_profile")
    @Enumerated(EnumType.STRING)
    private RiskProfile riskProfile;


    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Account account;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Address> addresses = new HashSet<>();

    /*@ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "customer_roles",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();*/

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public Boolean getKycVerified() {
        return kycVerified;
    }

    public void setKycVerified(Boolean kycVerified) {
        this.kycVerified = kycVerified;
    }

    public LocalDate getKycVerifiedAt() {
        return kycVerifiedAt;
    }

    public void setKycVerifiedAt(LocalDate kycVerifiedAt) {
        this.kycVerifiedAt = kycVerifiedAt;
    }

    public RiskProfile getRiskProfile() {
        return riskProfile;
    }

    public void setRiskProfile(RiskProfile riskProfile) {
        this.riskProfile = riskProfile;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Set<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(Set<Address> addresses) {
        this.addresses = addresses;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }
}