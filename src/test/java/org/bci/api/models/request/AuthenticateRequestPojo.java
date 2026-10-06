package org.bci.api.models.request;

public class AuthenticateRequestPojo {

    private String userNameOrEmailAddress;
    private String password;

    public AuthenticateRequestPojo() {
    }

    public AuthenticateRequestPojo(String userNameOrEmailAddress, String password) {
        this.userNameOrEmailAddress = userNameOrEmailAddress;
        this.password = password;
    }

    public String getUserNameOrEmailAddress() {
        return userNameOrEmailAddress;
    }

    public void setUserNameOrEmailAddress(String userNameOrEmailAddress) {
        this.userNameOrEmailAddress = userNameOrEmailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "AuthenticateRequestPojo{" +
                "userNameOrEmailAddress='" + userNameOrEmailAddress + '\'' +
                '}';
    }
}