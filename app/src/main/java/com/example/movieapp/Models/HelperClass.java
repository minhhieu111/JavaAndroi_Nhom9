package com.example.movieapp.Models;

public class HelperClass {
    String username, email, dob ,gender, img;

    public HelperClass(String username, String email, String dob, String gender, String img) {
        this.username = username;
        this.email = email;
        this.dob = dob;
        this.gender = gender;
        this.img = img;
    }

    public HelperClass() {
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}