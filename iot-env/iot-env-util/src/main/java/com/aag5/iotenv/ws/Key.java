package com.aag5.iotenv.ws;

public class Key{
	private String publicKeyB64;
	private String privateKeyB64;
	
	public Key(String publicKeyB64, String privateKeyB64) {
		this.publicKeyB64 = publicKeyB64;
		this.privateKeyB64 = privateKeyB64;
	}
	
	public Key() {}

	public String getPublicKeyB64() {
		return publicKeyB64;
	}

	public void setPublicKeyB64(String publicKeyB64) {
		this.publicKeyB64 = publicKeyB64;
	}

	public String getPrivateKeyB64() {
		return privateKeyB64;
	}

	public void setPrivateKeyB64(String privateKeyB64) {
		this.privateKeyB64 = privateKeyB64;
	}	
}