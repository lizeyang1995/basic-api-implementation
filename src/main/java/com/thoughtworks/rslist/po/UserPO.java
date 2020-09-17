package com.thoughtworks.rslist.po;

import javax.persistence.*;

@Entity
@Table(name = "user")
public class UserPO {
    @Id
    @GeneratedValue
    private int id;
    @Column(name = "name")
    private String userName;
    private String gender;
    private int age;
    private String email;
    private String phone;
    private int voteNumber = 10;
}
