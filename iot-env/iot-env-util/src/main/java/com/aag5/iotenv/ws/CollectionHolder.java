package com.aag5.iotenv.ws;

import java.util.Collection;

public class CollectionHolder<T> extends Holder<Collection<T>>{
	public CollectionHolder(Collection<T> theThing) { super(theThing);}
	public CollectionHolder() {super();}
}