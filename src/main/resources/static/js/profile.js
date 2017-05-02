var name = "Sumit Sohani"
var phoneNumber = "(510) - 676 - 4242"
var mail = "ss@gmail.com"
var creditCard = "4242 4242 4242 4242"
var deliveryRating = 5
var requestRating = 4



$(document).ready(() => {
	$.post("/profile", {}, responseJson => {
		data = JSON.parse(responseJson)
		name = data.name
		phoneNumber = data.phoneNumber
		mail = data.email
		deliveryRating = data.deliveryRating
		requestRating = data.requestRating
			document.getElementById("name").innerText = name;
	document.getElementById("delivery-rating").innerText = "Delivery Rating: " + deliveryRating
	document.getElementById("request-rating").innerText = "Request Rating: " + requestRating
	document.getElementById("phone-number").innerText = phoneNumber
	document.getElementById("email").innerText = mail
	document.getElementById("card-number").innerText = creditCard
	});


});
