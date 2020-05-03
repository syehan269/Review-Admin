package com.example.reviewadm;

public class User {

    public String name, level;
    public String uid;
    public String email;
    public String pass;


    public User(String name, String uid, String pass, String email, String level) {
        this.name = name;
        this.uid = uid;
        this.email = email;
        this.pass = pass;
        this.level = level;
    }

    public User() {
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public String getuid() {
        return uid;
    }

    public void setuid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return pass;
    }

    public void setPassword(String password) {
        this.pass = pass;
    }
}
