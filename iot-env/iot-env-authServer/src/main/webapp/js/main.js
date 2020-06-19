var lastJWT;

window.onload = function wakeUp() {
	// jwt='eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJGcmFuY28iLCJleHAiOjE1ODMwNDM3NjQsImlhdCI6MTU4MzAwNzc2NH0.pUUK2G3mviHrsaQ6MtCBWSW2DhA26Qdc2RvLoTE_-hA';
	console.log("JS file started!");
	theInfiniteLoop();
	//checkJWT();
}

function addRow(id, name, publicKey){
	var table = document.getElementById("deviceTable");
	var row = table.insertRow(-1);
	row.insertCell(0).innerHTML = id;
	row.insertCell(1).innerHTML = name;
	row.insertCell(2).innerHTML = publicKey;	
}

function emptyTable(){
	var elmtTable = document.getElementById('deviceTable');
	var rowCount = elmtTable.rows.length;
	while(--rowCount) {
		elmtTable.deleteRow(rowCount);
	}
}

function updateDeviceList(){
	console.log("Asking for registered devices.");
	$.ajax({
		url :"/auth",
		type : "GET",
		dataType : "json",
		async : false,
		success : function(data, textStatus, jqXHR) {
			console.log("OK!");
			//console.log(" -> data: " + JSON.stringify(data));
			for( i=0;  i< data.theThing.length; i++) {
				//console.log(data.theThing[i]);
				//console.log(getDevicePublicKey(data.theThing[i].id));
				data.theThing[i].publicKey=getDevicePublicKey(data.theThing[i].id);
			}
			
			emptyTable();
			for( i=0;  i< data.theThing.length; i++) {
				addRow(data.theThing[i].id, data.theThing[i].name, data.theThing[i].publicKey);
			}
			//$.notify("Response: "+data.response, "success");
		},
		error : function(data) {
			console.log("ERROR! "+JSON.stringify(data));
		},
	})
}


function getDevicePublicKey(deviceId){
	console.log("Asking for public key for device "+deviceId);
	var theResult="notReady";
	$.ajax({
		url :"/publickey?deviceId="+deviceId,
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

function theInfiniteLoop(){
	updateDeviceList();
	
	function loop() {
		//console.log("In loop.");
		$('#currentDate').html(new Date());
		
		updateDeviceList();
	}

	setInterval(function(){loop()},2000);//timer running every 2 seconds
}

function register(){
	console.log("Registering new Device ");
	var deviceId = $('#deviceId').val()
	var deviceName = $('#deviceName').val()
	var devicePublicKey = $('#devicePublicKey').val()
	
	$.ajax({
		url : "/add-auth",
		type : "POST",
		data : JSON.stringify({ device : {id :deviceId, name: deviceName}, publicKey : devicePublicKey }),
		contentType : "application/json",
		crossDomain : true,
		success : function(data, textStatus, jqXHR) {
			console.log("OK!");
			console.log(" -> data: " + JSON.stringify(data));
			
			$.notify("New device was registered!", "success");
			$('#deviceId').val('');
			var deviceName = $('#deviceName').val('');
			var devicePublicKey = $('#devicePublicKey').val('');
		},
		error : function(data) {
			console.log("ERROR!");
			console.error(data);
		},
	})
	return theResult;
}