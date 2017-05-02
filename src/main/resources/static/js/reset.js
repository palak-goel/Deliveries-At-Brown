let phoneNumber;
function reset() {
	if (document.getElementById("phone").value != "") {
		//check phone number here
		$("#reset").hide();
		returnPackage = {
			cell: document.getElementById("phone").value
		}
		phoneNumber = returnPackage.cell;
		$.post("send-code", returnPackage, responseJSON => {
			$("#loader").hide();
			$("#enter_code").show();
		});
	}
}

function validate() {
	console.log("validating")
	code = document.getElementById("b1").value + "" + document.getElementById("b2").value + "" + document.getElementById("b3").value
		+ "" + document.getElementById("b4").value + "" + document.getElementById("b5").value + "" + document.getElementById("b6").value
	console.log(code)
	returnPackage = {
		cell: phoneNumber,
		code: code
	}
	$.post("/validate-code", returnPackage, responseJSON => {
		console.log('received')
		status = JSON.parse(responseJSON).goodCode;
		console.log(status)
		if (status == "true") {
			console.log("here")
			$("#enter_code").hide();
			$("#enter_password").show();
		}
	});
}
function update() {
	console.log("updating");
	returnPackage = {
		email: document.getElementById("account_email").value,
		password: document.getElementById("password").value
	}
	$.post("/reset-password", returnPackage, responseJSON => {
		console.log("received");
		status = JSON.parse(responseJSON).status;
		if (status == "true") {
			$("#enter_password").hide();
			$("#success").show();
		}
	});
}
$(document).ready(() => {
	$("#loader").hide();
	$("#enter_code").hide();
	$("#enter_password").hide();
	$("#success").hide();
});