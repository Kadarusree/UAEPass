package com.srikanth.uaepass.apis;

import androidx.annotation.NonNull;

public class UserProfileResponse {

    private String sub;
    private String fullnameAR;
    private String gender;
    private String mobile;
    private String lastnameEN;
    private String fullnameEN;
    private String uuid;
    private String lastnameAR;
    private String idn;
    private String nationalityEN;
    private String firstnameEN;
    private String userType;
    private String nationalityAR;
    private String firstnameAR;
    private String email;

    // Getters and Setters

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getFullnameAR() {
        return fullnameAR;
    }

    public void setFullnameAR(String fullnameAR) {
        this.fullnameAR = fullnameAR;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLastnameEN() {
        return lastnameEN;
    }

    public void setLastnameEN(String lastnameEN) {
        this.lastnameEN = lastnameEN;
    }

    public String getFullnameEN() {
        return fullnameEN;
    }

    public void setFullnameEN(String fullnameEN) {
        this.fullnameEN = fullnameEN;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getLastnameAR() {
        return lastnameAR;
    }

    public void setLastnameAR(String lastnameAR) {
        this.lastnameAR = lastnameAR;
    }

    public String getIdn() {
        return idn;
    }

    public void setIdn(String idn) {
        this.idn = idn;
    }

    public String getNationalityEN() {
        return nationalityEN;
    }

    public void setNationalityEN(String nationalityEN) {
        this.nationalityEN = nationalityEN;
    }

    public String getFirstnameEN() {
        return firstnameEN;
    }

    public void setFirstnameEN(String firstnameEN) {
        this.firstnameEN = firstnameEN;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getNationalityAR() {
        return nationalityAR;
    }

    public void setNationalityAR(String nationalityAR) {
        this.nationalityAR = nationalityAR;
    }

    public String getFirstnameAR() {
        return firstnameAR;
    }

    public void setFirstnameAR(String firstnameAR) {
        this.firstnameAR = firstnameAR;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    @NonNull
    @Override
    public String toString() {
        return "{" +
                "sub='" + sub + '\'' +
                ", fullnameAR='" + fullnameAR + '\'' +
                ", gender='" + gender + '\'' +
                ", mobile='" + mobile + '\'' +
                ", lastnameEN='" + lastnameEN + '\'' +
                ", fullnameEN='" + fullnameEN + '\'' +
                ", uuid='" + uuid + '\'' +
                ", lastnameAR='" + lastnameAR + '\'' +
                ", idn='" + idn + '\'' +
                ", nationalityEN='" + nationalityEN + '\'' +
                ", firstnameEN='" + firstnameEN + '\'' +
                ", userType='" + userType + '\'' +
                ", nationalityAR='" + nationalityAR + '\'' +
                ", firstnameAR='" + firstnameAR + '\'' +
                ", email='" + email + '\'' +
                '}' ;
    }
}
