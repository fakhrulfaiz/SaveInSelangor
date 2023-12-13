package com.example.assignment.data;

public class CardViewData extends Data {
    private String title;
    private String detail;

    private String phone;
    private String email;
    private int weightSum = 0;
    public CardViewData(String title, String detail) {
        this.title = title;
        this.detail = detail;
    }
    public CardViewData(String title, String detail, String phone, String email) {
        this.title = title;
        this.detail = detail;
        this.phone = phone;
        this.email = email;
    }

    public CardViewData(String title, String detail, String phone, String email, int buttonLayoutWeight) {
        this.title = title;
        this.detail = detail;
        this.phone = phone;
        this.email = email;
        this.weightSum = buttonLayoutWeight;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle() {
         this.title = title;
    }
    public String getDetails() {
        return detail;
    }
    public int getWeightSum() {
        return weightSum;
    }
    public void setDetails() {
        this.detail = detail;
    }
}

