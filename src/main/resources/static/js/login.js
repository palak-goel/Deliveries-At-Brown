let stripe = Stripe('pk_test_yPfZvsWaYgNY7wT5KiP0S2B5'),
elements = stripe.elements(),
loggedIn = false,
url = "",
card;

function login() {
	let params = {
		id: $("#email").value,
		password: $("#account_password").value,
		ip: getIp()
	}
	$.post("/validate-login", params, responseJSON =>{
		$responseObject = JSON.parse(responseJSON);
		if ($responseObject.result == true) {
			console.log("good login")
			//bounce to home page
			url = $responseObject.url;
    		let u = "/home" + url; 
   			xmlHttp = new XMLHttpRequest(); 
    		xmlHttp.onreadystatechange = ProcessRequest;
    		xmlHttp.open("GET", u, true);
    		xmlHttp.send(params);
		} else {
			console.log("bad login")
			//display error
		}
	});
}

function createAccount(token) {
	console.log("ca");
	let account = {
		name: $("#first_name").value + " " + $("#last_name").value,
		email: $("#email").value,
		stripe: token,
		cell: $("#cell").value,
		password: $("#password").value,
		ip: getIp()
	}
	$.post("/create-account", account, responseJSON =>{
		$responseObject = JSON.parse(responseJSON);
		if ($responseObject.success == true) {
			//go to main page
			console.log("good")
			let u = "/home/" + $responseJSON.url; 
   			xmlHttp = new XMLHttpRequest(); 
    		xmlHttp.onreadystatechange = ProcessRequest;
    		xmlHttp.open( "GET", u, true );
    		xmlHttp.send(params);
		} else {
			console.log("bad")
			//display error
		}
	});
}

function createToken() {
  stripe.createToken(card).then(function(result) {
    if (result.error) {
      // Inform the user if there was an error
      let errorElement = document.getElementById('card-errors');
      errorElement.textContent = result.error.message;
    } else {
      //create the new account
      createAccount(result.token);
    }
  }).catch(e =>{
  	console.log(e);
  });
};


// Create a token when the form is submitted.
document.getElementById("create_account").addEventListener('click', function(e) {
  e.preventDefault();
  createToken();
});

$(document).ready(() => {
	card = elements.create('card');
	// Add an instance of the card UI component into the `card-element` <div>
	card.mount('#card-element');
});

