package com.example.serviceprovider.Models;

public class Requests {
    private String service,amount,name,status,uid,id,phone,custName,custPhone,custAddres,timeRequested,dateRequested,emergencyStatus,referenceNumber;

    public Requests() {
    }

    public Requests(String service, String amount, String name, String status, String uid, String id, String phone, String custName, String custPhone, String custAddres, String timeRequested, String dateRequested, String emergencyStatus, String referenceNumber) {
        this.service = service;
        this.amount = amount;
        this.name = name;
        this.status = status;
        this.uid = uid;
        this.id = id;
        this.phone = phone;
        this.custName = custName;
        this.custPhone = custPhone;
        this.custAddres = custAddres;
        this.timeRequested = timeRequested;
        this.dateRequested = dateRequested;
        this.emergencyStatus = emergencyStatus;
        this.referenceNumber = referenceNumber;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getCustPhone() {
        return custPhone;
    }

    public void setCustPhone(String custPhone) {
        this.custPhone = custPhone;
    }

    public String getCustAddres() {
        return custAddres;
    }

    public void setCustAddres(String custAddres) {
        this.custAddres = custAddres;
    }

    public String getTimeRequested() {
        return timeRequested;
    }

    public void setTimeRequested(String timeRequested) {
        this.timeRequested = timeRequested;
    }

    public String getDateRequested() {
        return dateRequested;
    }

    public void setDateRequested(String dateRequested) {
        this.dateRequested = dateRequested;
    }

    public String getEmergencyStatus() {
        return emergencyStatus;
    }

    public void setEmergencyStatus(String emergencyStatus) {
        this.emergencyStatus = emergencyStatus;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
}
