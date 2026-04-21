package com.example.hsm_demo.models;

public class EncryptResponse {

	private String ivBase64Value;

	private String encryptedValue;

	public String getIvBase64Value() {
		return ivBase64Value;
	}

	public void setIvBase64Value(String ivBase64Value) {
		this.ivBase64Value = ivBase64Value;
	}

	public String getEncryptedValue() {
		return encryptedValue;
	}

	public void setEncryptedValue(String encryptedValue) {
		this.encryptedValue = encryptedValue;
	}

}
