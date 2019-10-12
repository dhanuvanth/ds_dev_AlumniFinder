package com.example.aluminifinder;

public class User {
    private String email_txt,status;

    public User() {
    }

    public User(String email, String status) {
        this.email_txt = email;
        this.status = status;
    }

    public String getEmail_txt() {
        return email_txt;
    }

    public void setEmail_txt(String email_txt) {
        this.email_txt = email_txt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
