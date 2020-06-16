package com.aag5.iotenv.ws;

public class AuthResponse {

	private Boolean authenticates;
	
	public AuthResponse(Boolean authenticates) {
		this.authenticates = authenticates;
	}
	
	public AuthResponse() {}

	public Boolean getAuthenticates() {
		return authenticates;
	}

	public void setAuthenticates(Boolean authenticates) {
		this.authenticates = authenticates;
	}
}
