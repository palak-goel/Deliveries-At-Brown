let stripe = Stripe('pk_test_yPfZvsWaYgNY7wT5KiP0S2B5'),
elements = stripe.elements(),
loggedIn = false,
card;

function login() {
	let params = {
		id: document.getElementById("account_id").value,
		password: document.getElementById("account_password").value,
	}
	$.post("/validate-login", params, responseJSON =>{
		$responseObject = JSON.parse(responseJSON);
		if ($responseObject.result == true) {
			console.log("good login")
			//bounce to home page
			from = document.getElementById("from").value;
			console.log(from)
    		if (from == "") {
    			//go to home page
    			window.location.replace("/profile")
    		} else {
    			window.location.replace("/" + from);
    		}

		} else {
			console.log("bad login")
			//display error
		}
	});
}

function createAccount(token) {
	console.log("ca");
	let account = {
		name: document.getElementById("first_name").value + " " + document.getElementById("last_name").value,
		email: document.getElementById("new_email").value,
		stripe: token.id,
		cell: document.getElementById("cell").value,
		password: document.getElementById("password").value,
	}
	console.log(account)
	$.post("/create-account", account, responseJSON =>{
		$responseObject = JSON.parse(responseJSON);
		if ($responseObject.success == true) {
			//go to main page
			console.log("good")
			console.log(document.getElementById("from").value);
			from = document.getElementById("from").value;
    		if (from == "") {
    			//go to home page
    			window.location.replace("/profile")
    		} else {
    			window.location.replace("/" + from);
    		}		
    	} else {
			console.log("bad")
			//display error
		}
	});
}

function createToken() {
	console.log("creating token")
  stripe.createToken(card).then(function(result) {
  	console.log("created")
    if (result.error) {
    	console.log("error")
      // Inform the user if there was an error
      let errorElement = document.getElementById('card-errors');
      errorElement.textContent = result.error.message;
    } else {
      //create the new account
      console.log("no error: " + result.token.id);
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
	console.log(document.getElementById("from").value);
	card = elements.create('card');
	// Add an instance of the card UI component into the `card-element` <div>
	card.mount('#card-element');
	card.addEventListener('change', function(event) {
  		displayError = document.getElementById('card-errors');
  		if (event.error) {
    		displayError.textContent = event.error.message;
  		} else {
    		displayError.textContent = '';
  		}
	});
});

