package com.maro.waka_well;

public class User {
    private String firstname;
    private String lastname;
    private String email;
    private String profile_image;
    private String phone;
    private String dob;
    private String user_id;


    public User(String firstname, String lastname, String email, String profile_image, String phone,
                String dob, String user_id) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.profile_image = profile_image;
        this.phone = phone;
        this.dob = dob;
        this.user_id = user_id;
    }

    public User() {
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "User{" +
                "firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", profile_image='" + profile_image + '\'' +
                ", phone='" + phone + '\'' +
                ", dob='" + dob + '\'' +
                ", user_id='" + user_id + '\'' +
                '}';
    }
}
