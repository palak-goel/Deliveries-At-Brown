let phoneNumber;
function reset() {
	if (document.getElementById("phone").value != "") {
		//check phone number here
		$("#reset").hide();
		responseObject = {
			cell: document.getElementById("phone").value
		}
		phoneNumber = responseObject.cell;
		$.post("send-code", responseObject, responseJSON => {
			$("#loader").hide();
			$("#enter_code").show();
		});
	}
}

function validate() {
	code = $("#1").value + $("#1").value + $("#2").value + $("#3").value
		+ $("#4").value + $("#5").value + $("#6").value
	responseObject = {
		cell: phoneNumber,
		code: code
	}
	$.post("/validate-code", responseObject, responseJSON => {
		status = JSON.parse(responseJSON).goodCode;
		if (status === true) {
			$("#enter_code").hide();
			$("#enter_password").show():
		}
	});
}
$(document).ready(() => {
	$("#loader").hide();
	$("#enter_code").hide():
	$("#enter_password").hide();
});