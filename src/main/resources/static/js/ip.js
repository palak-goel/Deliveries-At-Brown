
function checkIp() {
	let params = {ip:getIp()};
	$.post("/check-ip", params, responseJSON => {
		$responseObject = JSON.parse(responseJSON);
		if ($responseObject.valid == false) {
			let u = "/login"; 
   			xmlHttp = new XMLHttpRequest(); 
    		xmlHttp.onreadystatechange = ProcessRequest;
    		xmlHttp.open("GET", u, true);
    		xmlHttp.send(params);
		}
	});
}

function getIp() {
	$.getJSON('//api.ipify.org?format=jsonp&callback=?', function(data) {
		return data.ip;
	});
	return "";
}
