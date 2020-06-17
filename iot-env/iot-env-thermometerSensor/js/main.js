var AUTH_ENDPOINT="http://localhost:8080";
var FILTER_ENDPOINT="http://localhost:8081";

// The length of the RSA key, in bits.
var Bits = 2048; 
var publicKey = undefined;
var privateKey = undefined;
var publicKeyStr = undefined;
var privateKeyStr = undefined;

window.onload = function wakeUp() {
	// jwt='eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJGcmFuY28iLCJleHAiOjE1ODMwNDM3NjQsImlhdCI6MTU4MzAwNzc2NH0.pUUK2G3mviHrsaQ6MtCBWSW2DhA26Qdc2RvLoTE_-hA';
	console.log("JS file started!");
	$('#endpoint').val('http://localhost:8080/');
	
	$('#deviceId').val('0000-0000-0000-0001');
	$('#passPhrase').val('MySuperSecretPassPhrase');
	
	generateRSAKeys();
	$('#temp').val($('#tempRange').val());
	
	/*var mySecret="huehue2";
	console.log(mySecret);
	console.log(encrypt(mySecret));
	console.log(decrypt(encrypt(mySecret)));*/
}

function generateRSAKeys() {
	console.log('Generating Keys...');
	var crypt = new JSEncrypt({ default_key_size: Bits });
	crypt.getKey();
	//publicKey="-----BEGIN PUBLIC KEY-----MFswDQYJKoZIhvcNAQEBBQADSgAwRwJAS17wx19j1TDagK5HciLtjYlv1TB7iL7FmqZdyTlCxCkcIW/KLvFt7F7FUC7Z/Jh36gA737QyuPDWQRZqPfSjswIDAQAB-----END PUBLIC KEY-----";
	//privateKey="-----BEGIN RSA PRIVATE KEY-----MIIBOQIBAAJAS17wx19j1TDagK5HciLtjYlv1TB7iL7FmqZdyTlCxCkcIW/KLvFt7F7FUC7Z/Jh36gA737QyuPDWQRZqPfSjswIDAQABAkAIO/8iSRGnIQc+N8wmdBpv9Cv7pqhYcD82fiaQ7WiV9al41sxxI0JzRoiXPxGx+KaRsNv+z8mq2diuSYzmbQ8BAiEAjkJBUA9voud9DrWXNbU+FFY78kv93fMmzRwwJk8agxECIQCHofXwixD3sVMhX659oIL8hXxKKSwo6kaevCGleKZygwIhAI2kcG1rgSb9QCo2KkFinVYYaoWcnj+wi1CsIVDzcB1RAiAay/DfOVp81VfrPBApWdEHOwg3TrMe0kppihnLq26XKQIgHlH/IWYPxWrcePiF7uB+WVhaNK2+vw+SkFjdml01VsY=-----END RSA PRIVATE KEY-----";
	publicKey=crypt.getPublicKey();
	privateKey=crypt.getPrivateKey();
	publicKeyStr=publicKey.replace(/-/g, "").replace(/BEGIN/g, "").replace(/END/g, "").replace(/KEY/g, "").replace(/PUBLIC/g, "").replace(/ /g, "").replace(/\n/g, "");
	privateKeyStr=privateKey.replace(/-/g, "").replace(/BEGIN/g, "").replace(/END/g, "").replace(/KEY/g, "").replace(/PRIVATE/g, "").replace(/RSA/g, "").replace(/ /g, "").replace(/\n/g, "");
	console.log(privateKey);
	console.log(publicKeyStr);
	$("#publicKey").val(publicKey);
	console.log('Generating Key DONE!');
};

function deviceChanged(){
	$("#publicKey").val("???");
	privateKey=undefined;
	publicKey=undefined;
	publicKeyStr=undefined;
	privateKeyStr=undefined;
}

function encrypt(text) {
	var crypt = new JSEncrypt({ default_key_size: Bits });
	crypt.setPrivateKey(privateKey);
	return crypt.encrypt(text);
};

function decrypt(cryptedText) {
	var crypt = new JSEncrypt({ default_key_size: Bits });
	crypt.setPrivateKey(privateKey);
	//crypt.setPublicKey(publicKey);
	return crypt.decrypt(cryptedText);
};

function register(){
	console.log('Registering device...');
	if(! publicKey || ! privateKey) {
		console.error('Must generate Keys first!');
		$.notify("Must generate Keys first!", "error");
		return;
	}
	
	$.ajax({
		url : AUTH_ENDPOINT+"/add-auth",
		type : "POST",
		data : JSON.stringify({ device : { id: $('#deviceId').val(), name: "TemperatureSensor" }, publicKey : publicKeyStr }),
		contentType : "application/json",
		crossDomain : true,
		//dataType : "json",
		success : function(data, textStatus, jqXHR) {
			$.notify("Register OK!", "success");
			console.log("OK!");
		},
		error : function(data) {
			console.error("ERROR! ");
			console.error(data);
			console.log(data);
			$.notify("Error occurred when registering!", "error");
			$.notify("Perhaps more info on console", "error");
			 
		},
	})
}

function tempChanged(){
	console.log("Temperature changed to "+$('#tempRange').val());
	$('#temp').val($('#tempRange').val());
	$.notify("Temperature changed to "+$('#tempRange').val(), "info");
	
	if(! publicKey || ! privateKey) {
		console.log('Thermometer not ready to send info to filter Server.');
		return;
	}
	
	console.log("Sending new info to Filter Server");
	$.notify("Sending new info to Filter Server","info");
	
	header = {
		alg: "RS512",
		typ: "JWT"
	};

	payload = {
		sub: $('#deviceId').val(),
		exp: Math.floor(new Date().getTime() / 1000) + 6000 * 30,
		devideId: $('#deviceId').val(),
		iat: Math.floor(new Date().getTime() / 1000)
	};

	signature = encrypt(btoa(JSON.stringify(header))+"."+btoa(JSON.stringify(payload)));
	
	jwt=btoa(JSON.stringify(header))+"."+btoa(JSON.stringify(payload))+"."+signature;
	
	//var jwt = jwt.replace(/=/g, "");
	
	console.log("Generated JWT: '"+jwt+"'");
	
	$.ajax({
		url : FILTER_ENDPOINT+"/temp",
		type : "POST",
		data : JSON.stringify({ temp : $('#tempRange').val() }),
		headers: { Authorization : 'Bearer '+jwt },
		contentType : "application/json",
		crossDomain : true,
		//dataType : "json",
		success : function(data, textStatus, jqXHR) {
			console.log("OK!");
			console.log(" -> data: " + JSON.stringify(data));
			//console.log(" -> data.jwt: " + data.jwt);
			//console.log(" -> textStatus: " + textStatus);
			//console.log(" -> jqXHR: " + JSON.stringify(jqXHR));
			//console.log(" -> jqXHR.header.new-jwt: " + jqXHR.getAllResponseHeaders());
			console.log(" -> jqXHR.header.new-jwt: " + jqXHR.getResponseHeader("new-jwt"));
			$.notify("Response: "+data.response, "success");
			var newjwt = jqXHR.getResponseHeader("new-jwt");
			if(newjwt){
				changeJWT(newjwt);
			}
		},
		error : function(data) {
			console.log("ERROR! "+JSON.stringify(data));
			console.error(data);
		},
	})
}

// ---------------------     ---------------------     ---------------------     --------------------- 
// 
// ---------------------     ---------------------     ---------------------     --------------------- 
// 
// ---------------------     ---------------------     ---------------------     --------------------- 
function theInfiniteLoop(){
	function loop() {
		//console.log("In loop.");
		$('#currentDate').html(new Date());
		
		if(lastJWT){
			var jwt = parseJwt(lastJWT);
			
			if(toDateTime(jwt.exp).getTime() < new Date().getTime()){
				//console.log(toDateTime(jwt.exp).getTime()+" "+new Date().getTime());
				//console.log(toDateTime(jwt.exp)+" - "+new Date());
				$("#jwtInfoDiv").addClass("alert alert-danger");
				$("#JWTexpiredDiv").show();
				$("#loginDiv").show();
				$("#getRequestDiv").hide();
			}
		}

	}

	setInterval(function(){loop()},1000);//timer running every 1 second
}

function checkJWT() {
	console.log("Checking actual jwt.");

	var jwt=getCookie("jwt");

	if (jwt) {
		lastJWT=jwt;
	}
	
	if (lastJWT) {
		console.log("jwt found.");
		// if everything's allright
		$("#JWTexpiredDiv").hide();
		$("#noJWTFoundDiv").hide();
		$("#loginDiv").hide();
		
		$('#lastKWTText').html(lastJWT);
		$('#lastKWTData').html(JSON.stringify(parseJwt(lastJWT)));
		
		$('#lastKWTDataSub').html(parseJwt(lastJWT).sub);
		$('#lastKWTDataIat').html(toDateTime(parseJwt(lastJWT).iat));
		$('#lastKWTDataExp').html(toDateTime(parseJwt(lastJWT).exp));
		
		$("#jwtInfoDiv").show();
		
		$("#jwtInfoDiv").removeClass("alert alert-danger");
		$("#jwtInfoDiv").addClass("alert alert-success");
		
		setTimeout(() => {
			$("#jwtInfoDiv").removeClass("alert alert-success");
			}, 500);
		
		
		$("#getRequestDiv").show();
		
		$.notify("New JWT aquired!", "success");
		
	} else {
		console.log("jwt not found.");
		$("#noJWTFoundDiv").show();
		$("#loginDiv").show();
	}
}

function changeJWT(jwtText){
	console.log("Changing actual jwt.");
	
	if(jwtText){
		jwt=parseJwt(jwtText);
		//console.log(jwt);
		//console.log(JSON.stringify(jwt));
		createCookie("jwt", jwtText, toDateTime(jwt.exp));
		lastJWT=jwtText;
		checkJWT();
	}
}


function toDateTime(secs) {
    var t = new Date(1970, 0, 1); // Epoch
    t.setSeconds(secs);
    t.setTime( t.getTime() - t.getTimezoneOffset()*60*1000 );
    //remember kids, work with zulu time from the beginning
    
    return t;
}

function parseJwt (token) {
    var base64Url = token.split('.')[1];
    var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    var jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));

    return JSON.parse(jsonPayload);
};

function authenticate() {
	logBar();
	console.log("Login in!");

	if (!$('#endpoint').val()) {
		console.error("The Enpoint must be specified!");
	}

	var user = $('#username').val();
	var pass = $('#password').val();
	// console.log(user+' '+pass);

	$.ajax({
		url : $('#endpoint').val() + "authenticate",
		type : "POST",
		data : JSON.stringify({ username : user, password : pass }),
		contentType : "application/json",
		crossDomain : true,
		dataType : "json",
		success : function(data, textStatus, jqXHR) {
			console.log("OK!");
			console.log(" -> data: " + JSON.stringify(data));
			//console.log(" -> data.jwt: " + data.jwt);
			//console.log(" -> textStatus: " + textStatus);
			//console.log(" -> jqXHR: " + JSON.stringify(jqXHR));
			//console.log(" -> jqXHR.header.new-jwt: " + jqXHR.getResponseHeader("new-jwt"));
			
			if(data.jwt){
				changeJWT(data.jwt);
			}
		},
		error : function() {
			console.log("ERROR!");
		},
	})
}

function sendRequest(){
	logBar();
	console.log("Sending request.")
	$.ajax({
		url : $('#endpoint').val() + "hello",
		type : "GET",
		//data : JSON.stringify({ username : user, password : pass }),
		headers: { Authorization : 'Bearer '+lastJWT },
		//contentType : "application/json",
		crossDomain : true,
		//dataType : "json",
		success : function(data, textStatus, jqXHR) {
			console.log("OK!");
			console.log(" -> data: " + JSON.stringify(data));
			//console.log(" -> data.jwt: " + data.jwt);
			//console.log(" -> textStatus: " + textStatus);
			//console.log(" -> jqXHR: " + JSON.stringify(jqXHR));
			//console.log(" -> jqXHR.header.new-jwt: " + jqXHR.getAllResponseHeaders());
			console.log(" -> jqXHR.header.new-jwt: " + jqXHR.getResponseHeader("new-jwt"));
			$.notify("Response: "+data.response, "success");
			var newjwt = jqXHR.getResponseHeader("new-jwt");
			if(newjwt){
				changeJWT(newjwt);
			}
		},
		error : function(data) {
			console.log("ERROR! "+JSON.stringify(data));
		},
	})
}

function logBar(){
	console.log("- - - - - - - - - - - - - - - - - - -");
}

function createCookie(name, value, expires, path, domain) {
	  var cookie = name + "=" + escape(value) + ";";

	  if (expires) {
	    // If it's a date
	    if(expires instanceof Date) {
	      // If it isn't a valid date
	      if (isNaN(expires.getTime()))
	       expires = new Date();
	    }
	    else
	      expires = new Date(new Date().getTime() + parseInt(expires) * 1000 * 60 * 60 * 24);

	    cookie += "expires=" + expires.toGMTString() + ";";
	  }

	  if (path)
	    cookie += "path=" + path + ";";
	  if (domain)
	    cookie += "domain=" + domain + ";";

	  document.cookie = cookie;
	}

function getCookie(name) {
	  var regexp = new RegExp("(?:^" + name + "|;\s*"+ name + ")=(.*?)(?:;|$)", "g");
	  var result = regexp.exec(document.cookie);
	  return (result === null) ? null : result[1];
	}