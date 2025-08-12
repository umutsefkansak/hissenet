package com.infina.hissenet.entity;


import com.infina.hissenet.entity.enums.CustomerType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("CORPORATE")
public class CorporateCustomer extends Customer {

    @NotBlank
    @Column(name = "company_name", length = 200)
    private String companyName;

    @Pattern(regexp = "^[0-9]{10}$")
    @Column(name = "tax_number", unique = true, length = 10)
    private String taxNumber;

    @Column(name = "trade_registry_number", length = 50)
    private String tradeRegistryNumber;

    @Column(name = "tax_office")
    private String taxOffice;

    @Column(name = "establishment_date")
    private LocalDate establishmentDate;

    @Column(name = "sector", length = 100)
    private String sector;

    @Column(name = "authorized_person_name", length = 100)
    private String authorizedPersonName;

    @Column(name = "authorized_person_title", length = 50)
    private String authorizedPersonTitle;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$")
    @Column(name = "authorized_person_phone", length = 20)
    private String authorizedPersonPhone;

    @Pattern(regexp = "^[1-9][0-9]{10}$")
    @Column(name = "authorized_person_tc_number", length = 11)
    private String authorizedPersonTcNumber;

    @Email
    @Column(name = "authorized_person_email", length = 100)
    private String authorizedPersonEmail;

    @Column(name = "website", length = 200)
    private String website;


    public CorporateCustomer() {
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }

    public String getTradeRegistryNumber() {
        return tradeRegistryNumber;
    }

    public void setTradeRegistryNumber(String tradeRegistryNumber) {
        this.tradeRegistryNumber = tradeRegistryNumber;
    }

    public LocalDate getEstablishmentDate() {
        return establishmentDate;
    }

    public void setEstablishmentDate(LocalDate establishmentDate) {
        this.establishmentDate = establishmentDate;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getAuthorizedPersonName() {
        return authorizedPersonName;
    }

    public void setAuthorizedPersonName(String authorizedPersonName) {
        this.authorizedPersonName = authorizedPersonName;
    }

    public String getAuthorizedPersonTitle() {
        return authorizedPersonTitle;
    }

    public void setAuthorizedPersonTitle(String authorizedPersonTitle) {
        this.authorizedPersonTitle = authorizedPersonTitle;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getAuthorizedPersonPhone() {
        return authorizedPersonPhone;
    }

    public void setAuthorizedPersonPhone(String authorizedPersonPhone) {
        this.authorizedPersonPhone = authorizedPersonPhone;
    }

    public String getAuthorizedPersonTcNumber() {
        return authorizedPersonTcNumber;
    }

    public void setAuthorizedPersonTcNumber(String authorizedPersonTcNumber) {
        this.authorizedPersonTcNumber = authorizedPersonTcNumber;
    }

    public String getAuthorizedPersonEmail() {
        return authorizedPersonEmail;
    }

    public void setAuthorizedPersonEmail(String authorizedPersonEmail) {
        this.authorizedPersonEmail = authorizedPersonEmail;
    }

    public String getTaxOffice() {
        return taxOffice;
    }

    public void setTaxOffice(String taxOffice) {
        this.taxOffice = taxOffice;
    }
}
