var lastJWT;

window.onload = function wakeUp() {
	console.log("JS file started!");

	//$("#openDiv").hide();
	$("#closedDiv").hide();
	theInfiniteLoop();
}

function theInfiniteLoop(){
	function loop() {
		//console.log("In loop.");
		sendRequest();
	}
	setInterval(function(){loop()},1000);//timer running every 1 second
}

function sendRequest(){
	console.log("Sending request.")
	$.ajax({
		url : "/statusDummy",
		type : "GET",
		//data : JSON.stringify({ username : user, password : pass }),
		//headers: { Authorization : 'Bearer '+lastJWT },
		//contentType : "application/json",
		//crossDomain : true,
		//dataType : "json",
		success : function(data, textStatus, jqXHR) {
			console.log("OK!");
			console.log(" -> data: " + data);
			updateWindow(data);
		},
		error : function(data) {
			console.log("ERROR! "+JSON.stringify(data));
		},
	})
}

function updateWindow(status){
	if(status === 'OPEN'){
		$("#openDiv").show();
		$("#closedDiv").hide();
	} else {
		$("#openDiv").hide();
		$("#closedDiv").show();
	}
}