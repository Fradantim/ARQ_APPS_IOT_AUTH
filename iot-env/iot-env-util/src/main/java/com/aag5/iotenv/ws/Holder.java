package com.aag5.iotenv.ws;

public class Holder<S> {
	private S theThing;

	public Holder(S theThing) {this.theThing = theThing; }
	public Holder() {}
	
	public S getTheThing() {return theThing; }
	public void setTheThing(S theThing) {this.theThing = theThing; }
}
