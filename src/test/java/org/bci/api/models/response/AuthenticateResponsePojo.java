package org.bci.api.models.response;

import org.bci.api.pojo.AuthResultPojo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthenticateResponsePojo {

    private AuthResultPojo result;
    private boolean status;
    private String message;
    private int totalCount;
    private String isFormReset;

    public AuthResultPojo getResult() {
        return result;
    }

    public void setResult(AuthResultPojo result) {
        this.result = result;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getIsFormReset() {
        return isFormReset;
    }

    public void setIsFormReset(String isFormReset) {
        this.isFormReset = isFormReset;
    }
}