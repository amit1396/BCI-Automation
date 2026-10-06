package org.bci.api.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthResultPojo {

    @JsonProperty("AccessToken")
    private String accessToken;

    @JsonProperty("RefreshToken")
    private String refreshToken;

    @JsonProperty("UserId")
    private int userId;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("UserName")
    private String userName;

    @JsonProperty("PlantId")
    private int plantId;

    @JsonProperty("PlantCode")
    private String plantCode;

    @JsonProperty("PasswordStatus")
    private int passwordStatus;

    @JsonProperty("ResetPasswordDaysLeft")
    private int resetPasswordDaysLeft;

    @JsonProperty("SessionExpiryTime")
    private int sessionExpiryTime;

    // Getters and Setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getPlantId() {
        return plantId;
    }

    public void setPlantId(int plantId) {
        this.plantId = plantId;
    }

    public String getPlantCode() {
        return plantCode;
    }

    public void setPlantCode(String plantCode) {
        this.plantCode = plantCode;
    }

    public int getPasswordStatus() {
        return passwordStatus;
    }

    public void setPasswordStatus(int passwordStatus) {
        this.passwordStatus = passwordStatus;
    }

    public int getResetPasswordDaysLeft() {
        return resetPasswordDaysLeft;
    }

    public void setResetPasswordDaysLeft(int resetPasswordDaysLeft) {
        this.resetPasswordDaysLeft = resetPasswordDaysLeft;
    }

    public int getSessionExpiryTime() {
        return sessionExpiryTime;
    }

    public void setSessionExpiryTime(int sessionExpiryTime) {
        this.sessionExpiryTime = sessionExpiryTime;
    }
}