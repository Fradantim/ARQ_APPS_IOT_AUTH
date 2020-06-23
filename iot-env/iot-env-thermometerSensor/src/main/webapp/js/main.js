
window.onload = function wakeUp() {
	// jwt='eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJGcmFuY28iLCJleHAiOjE1ODMwNDM3NjQsImlhdCI6MTU4MzAwNzc2NH0.pUUK2G3mviHrsaQ6MtCBWSW2DhA26Qdc2RvLoTE_-hA';
	console.log("JS file started!");
	$('#temp').val($('#tempRange').val());
	fillDeviceInfo();
	fillKeysInfo(getKeys());
}

function copyText(elementID) {
	/* Get the text field */
	var str = $('#'+elementID).text();

	const el = document.createElement('textarea');
	el.value = str;
	document.body.appendChild(el);
	el.select();
	document.execCommand('copy');
	document.body.removeChild(el);
	console.log("'"+elementID+"' value copied: '"+str+"'")
	$.notify("'" + elementID + "' text copied.", "info");
}




function getDevice(){
	console.log("Asking for device id");
	var theResult="notReady";
	
	$.ajax({
		url :"/device",
		type : "GET",
		async: false,
		success : function(data, textStatus, jqXHR) {
			console.log("OK!");
			//console.log(" -> data: " + JSON.stringify(data));
			theResult=data;
		},
		error : function(data) {
			console.log("ERROR! "+JSON.stringify(data));
		},
	})
	return theResult;
}

function fillDeviceInfo(){
	var device = getDevice();
	$('#deviceId').text(device.id);
	$('#deviceName').text(device.name);
}

function getKeys(){
	console.log("Asking for keys");
	var theResult="notReady";
	
	$.ajax({
		url :"/keys",
		type : "GET",
		async: false,
		success : function(data, textStatus, jqXHR) {
			console.log("OK!");
			//console.log(" -> data: " + JSON.stringify(data));
			theResult=data;
		},
		error : function(data) {
			console.log("ERROR! "+JSON.stringify(data));
		},
	})
	return theResult;
}

function fillKeysInfo(keys){
	$('#privateKey').text(keys.privateKeyB64);
	$('#publicKey').text(keys.publicKeyB64);
}

function emptyKeysInfo(){
	$('#privateKey').text("???");
	$('#publicKey').text("???");
}

function generateKeys(){
	console.log("Asking for key regeneration");
	emptyKeysInfo();
	$.ajax({
		url :"/regenerateKeys",
		type : "GET",
		async: false,
		success : function(data, textStatus, jqXHR) {
			console.log("OK!");
			//console.log(" -> data: " + JSON.stringify(data));
			fillKeysInfo(data);
		},
		error : function(data) {
			console.log("ERROR! "+JSON.stringify(data));
		},
	})
}

function tempChanged(){
	var temp =$('#tempRange').val();
	$('#temp').val(temp);
	console.log("Temperature changed to "+temp);
	$.notify("Temperature changed to "+temp, "info");
	
	console.log("Sending new info to Filter Server");
	$.notify("Sending new info to Filter Server","info");
	
	$.ajax({
		url : "/temp",
		type : "POST",
		data : JSON.stringify({ temp : temp }),
		contentType : "application/json",
		success : function(data, textStatus, jqXHR) {
			console.log("OK!");
			$.notify("Temperature change was successfully informed to the filter server.", "success");
		},
		error : function(data) {
			$.notify("Error ocurred while trying to inform filter server a new temperature.", "error");
			$.notify("More info in browser's console... perhaps....", "info");
			console.log("ERROR! "+JSON.stringify(data));
			console.error(data);
		},
	})
}