package com.thoughtworks.rslist.domain;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class User {
    @NotNull
    @Size(max = 8)
    private String name;
    @NotNull
    private String gender;
    @Size(min = 18)
    @Size(max = 100)
    private int age;
    @Email
    private String email;
    @Pattern(regexp = "1\\d{10}")
    private String phoneNumber;
    private int voteNumber = 10;

    public User(String name, String gender, int age, String email, String phoneNumber, int voteNumber) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.voteNumber = voteNumber;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getVoteNumber() {
        return voteNumber;
    }

    public void setVoteNumber(int voteNumber) {
        this.voteNumber = voteNumber;
    }
}
