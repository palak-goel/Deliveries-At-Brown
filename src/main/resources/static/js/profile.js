var name = "Sumit Sohani"
var phoneNumber = "(510) - 676 - 4242"
var mail = "ss@gmail.com"
var deliveryRating = 5
var requestRating = 4



$(document).ready(() => {
	$.post("/profile", {}, responseJson => {
		data = JSON.parse(responseJson)
		name = data.name
		firstName = name.split(" ")[0]
		lastName = name.split(" ")[1]
		name = capitalizeFirstLetter(firstName) + " " + capitalizeFirstLetter(lastName)
		phoneNumber = data.phoneNumber
		mail = data.email
		deliveryRating = data.deliveryRating
		requestRating = data.requestRating
			document.getElementById("name").innerText = name;
	document.getElementById("delivery-rating").innerText = "Delivery Rating: " + deliveryRating
	document.getElementById("request-rating").innerText = "Request Rating: " + requestRating
	document.getElementById("phone-number").innerText = phoneNumber
	document.getElementById("email").innerText = mail
	});
	// type is either "order" or "deliv"
	$.post("/order-history", {}, responseJson => {
		data = JSON.parse(responseJson)
		deliveries = data.deliver // past deliveries
		order = data.order // past orders
		//TODO
	});


});

function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}