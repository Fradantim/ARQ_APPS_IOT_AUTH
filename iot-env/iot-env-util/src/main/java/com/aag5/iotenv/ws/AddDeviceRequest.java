package com.aag5.iotenv.ws;

import com.aag5.iotenv.model.Device;

public class AddDeviceRequest {

	private Device device;
	private String publicKey;
	
	public AddDeviceRequest(Device device, String publicKey) {
		this.device = device;
		this.publicKey = publicKey;
	}
	
	public AddDeviceRequest() {}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	@Override
	public String toString() {
		return "AddDeviceRequest [device=" + device + ", publicKey=" + publicKey + "]";
	}
}
