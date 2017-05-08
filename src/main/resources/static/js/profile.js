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
		ordTable = $("#ordTable")
		delivTable = $("#delivTable")
		for (var i = 0; i < order.length; i++) {
			let pickup = order[i].data.pickupL.data.name
			let dropoff = order[i].data.dropoffL.data.name
			if (dropoff === "Current Location") {
				dropoff = "User Location"
			}
			let item = order[i].data.items[0]
			let tip = order[i].data.price
			ordTable.append("<tr><td>" +  pickup + '</td><td>' + dropoff + '</td><td>' + item + '</td><td>' + tip + "</td></tr>")
		}
		for (var i = 0; i < deliveries.length; i++) {
			let pickup = deliveries[i].data.pickupL.data.name
			let dropoff = deliveries[i].data.dropoffL.data.name
			if (dropoff === "Current Location") {
				dropoff = "User Location"
			}
			let item = deliveries[i].data.items[0]
			let tip = deliveries[i].data.price
			delivTable.append("<tr><td>" +  pickup + '</td><td>' + dropoff + '</td><td>' + item + '</td><td>' + tip + "</td></tr>")
		}
		//TODO
		
		// table.append('<tr><td>'+ pickup + '</td><td>' + usr_rating + '</td><td>' + duration + " min" + '</td><td>' + 
  //             distance.toFixed(2) + " mi" + "</td><td>" + time + '</td><td>' + "$" + price + '</td><td>'+ items+ 
  //         '</td><td><button id="takeorder" onclick = "takeOrder(\'' + order.id + 
  //         '\');"> Take Order</button></td></tr>');
		console.log(deliveries)
		console.log(order)
	});


});

function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}