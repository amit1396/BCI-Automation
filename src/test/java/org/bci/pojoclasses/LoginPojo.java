package org.bci.pojoclasses;

public class LoginPojo {

	private String tcId;
	private String runMode;
	private String username;
	private String password;
	private String plantName;
	private String expectedUrlMsg;
	private String logoutExpectedMsg;
	private String expectedSuccessMessage;
	private String expectedAlertMessage;
	private String expectedErrorTitle;
	private String expectedErrorMessage;

	public String getTcId() {
		return tcId;
	}

	public void setTcId(String tcId) {
		this.tcId = tcId;
	}

	public String getRunMode() {
		return runMode;
	}

	public void setRunMode(String runMode) {
		this.runMode = runMode;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPlantName() {
		return plantName;
	}

	public void setPlantName(String plantName) {
		this.plantName = plantName;
	}

	public String getExpectedUrlMsgName() {
		return expectedUrlMsg;
	}

	public void setExpectedUrlMsg(String expectedUrlMsg) {
		this.expectedUrlMsg = expectedUrlMsg;
	}

	public String getLogoutExpectedMsg() {
		return logoutExpectedMsg;
	}

	public void setLogoutExpectedMsg(String logoutExpectedMsg) {
		this.logoutExpectedMsg = logoutExpectedMsg;
	}

	public String getExpectedSuccessMessage() {
		return expectedSuccessMessage;
	}

	public void setExpectedSuccessMessage(String expectedSuccessMessage) {
		this.expectedSuccessMessage = expectedSuccessMessage;
	}

	public String getExpectedAlertMessage() {
		return expectedAlertMessage;
	}

	public void setExpectedAlertMessage(String expectedAlertMessage) {
		this.expectedAlertMessage = expectedAlertMessage;
	}

	public String getExpectedErrorTitle() {
		return expectedErrorTitle;
	}

	public void setExpectedErrorTitle(String expectedErrorTitle) {
		this.expectedErrorTitle = expectedErrorTitle;
	}

	public String getExpectedErrorMessage() {
		return expectedErrorMessage;
	}

	public void setExpectedErrorMessage(String expectedErrorMessage) {
		this.expectedErrorMessage = expectedErrorMessage;
	}

	@Override
	public String toString() {
		return "TC: " + tcId + " | User: " + username;
	}

}