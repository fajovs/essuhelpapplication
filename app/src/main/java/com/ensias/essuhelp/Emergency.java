package com.ensias.essuhelp;

public class Emergency {
    private String userId;
    private String userName;
    private String description;
    private String emergencyLevel;
    private String location;
    private String timestamp;
    private String status;

    // Default constructor required for Firebase
    public Emergency() {
    }

    public Emergency(String userId, String userName, String description, String emergencyLevel,
                     String location, String timestamp, String status) {
        this.userId = userId;
        this.userName = userName;
        this.description = description;
        this.emergencyLevel = emergencyLevel;
        this.location = location;
        this.timestamp = timestamp;
        this.status = status;
    }

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmergencyLevel() {
        return emergencyLevel;
    }

    public void setEmergencyLevel(String emergencyLevel) {
        this.emergencyLevel = emergencyLevel;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}