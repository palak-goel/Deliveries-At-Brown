function logout() {
	$.post("/logout", {}, responseJSON => {
	});
	window.location.replace("/login");
}

$(document).ready(() => {
	if (document.getElementById("logout") != null) {
		document.getElementById("logout").addEventListener('click', function() {
			logout();
		});
	}
});