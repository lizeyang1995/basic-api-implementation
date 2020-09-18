package com.thoughtworks.rslist.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
public class User {
    @NotNull
    @Size(max = 8)
    @JsonProperty("user_name")
    private String userName;
    @NotNull
    @JsonProperty("user_gender")
    private String gender;
    @Min(18)
    @Max(100)
    @JsonProperty("user_age")
    private int age;
    @Email
    @JsonProperty("user_email")
    private String email;
    @Pattern(regexp = "1\\d{10}")
    @JsonProperty("user_phone")
    private String phone;
    private int voteNumber = 10;

    public User(String userName, String gender, int age, String email, String phone) {
        this.userName = userName;
        this.gender = gender;
        this.age = age;
        this.email = email;
        this.phone = phone;
    }

    public User() {
    }
}
