function submitRating() {
	var rating = parseFloat(document.getElementById("ratingField").value);
	if (rating > 5) {
		rating = 5;
	} 
	if (rating < 0) {
		rating = 0;
	}
	console.log(rating)
	$.post("/rating", {id: localStorage.id, role:"deliverer", rating: rating}, responseJson => {
		window.location.href = '/deliver';
	})
}
