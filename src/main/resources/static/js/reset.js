let phoneNumber;
function reset() {
	returnPackage = {
		cell: document.getElementById("area_code").value + document.getElementById("phone").value
		email: document.getElementById("acc_email").value
	}
	if (returnPackage.cell != "" && returnPackage.email != "") {
		//check phone number here
		$("#reset").hide();
		phoneNumber = returnPackage.cell;
		$.post("send-code", returnPackage, responseJSON => {
			$responseObject = JSON.parse(responseJSON);
			if ($responseObject.error == "") {
				$("#loader").hide();
				$("#enter_code").show();
			} else {
				alert("No account could be found matching the given email and phone number");
			}
		});
	} else {
		alert("Please enter all fields to reset password")
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
		error = JSON.parse(responseJSON).error;
		if (error == "") {
			$("#enter_password").hide();
			$("#success").show();
		} else {
			alert("There was an error resetting the password, please try again")
		}
	});
}
$(document).ready(() => {
	$("#loader").hide();
	$("#enter_code").hide();
	$("#enter_password").hide();
	$("#success").hide();
});