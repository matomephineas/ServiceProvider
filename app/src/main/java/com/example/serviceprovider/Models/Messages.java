package com.example.serviceprovider.Models;

public class Messages {
    private String message,currentUserNam,currentDate,currentTime;

    public Messages() {
    }

    public Messages(String message, String currentUserNam, String currentDate, String currentTime) {
        this.message = message;
        this.currentUserNam = currentUserNam;
        this.currentDate = currentDate;
        this.currentTime = currentTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCurrentUserNam() {
        return currentUserNam;
    }

    public void setCurrentUserNam(String currentUserNam) {
        this.currentUserNam = currentUserNam;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }
}
