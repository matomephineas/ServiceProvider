package com.example.serviceprovider.Models;

public class Services {
    private String service,uid,name,phone,image;
    private int amount;

    public Services() {
    }

    public Services(String service, String uid, String name, String phone, String image, int amount) {
        this.service = service;
        this.uid = uid;
        this.name = name;
        this.phone = phone;
        this.image = image;
        this.amount = amount;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
