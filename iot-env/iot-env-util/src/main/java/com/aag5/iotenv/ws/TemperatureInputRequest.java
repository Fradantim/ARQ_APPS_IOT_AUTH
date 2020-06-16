package com.aag5.iotenv.ws;

public class TemperatureInputRequest {

	private Integer temp;

	public TemperatureInputRequest(Integer temp) { this.temp = temp; }
	public TemperatureInputRequest() {}

	public Integer getTemp() {
		return temp;
	}

	public void setTemp(Integer temp) {
		this.temp = temp;
	}
	
	@Override
	public String toString() {
		return "TemperatureInputRequest [temp=" + temp + "]";
	}
}
