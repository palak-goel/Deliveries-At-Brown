let stripe = Stripe('pk_test_yPfZvsWaYgNY7wT5KiP0S2B5'),
elements = stripe.elements(),
loggedIn = false,
card;

function login() {
	console.log('here')
	let params = {
		id: document.getElementById("account_id").value,
		password: document.getElementById("account_password").value,
	}
	if (params.id == "" && params.password == "") {
		alert("Enter your account information completely to sign in");
		return;
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
    			window.location.href = '/profile';

    		} else {
    			window.location.href = '/profile';
    		}

		} else {
			alert("Your login credentials could not be recognized. Please try again or create account.");
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
	if (account.name == "" || account.email == "" || account.stripe == "" || account.cell == "" || account.password == "") {
		alert("Please enter all fields completely to create an account");
		return;
	} 
	if (account.password != document.getElementById("password_check").value) {
		alert("Passwords must match to create an account");
		return;
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
    			window.location.href = '/profile';
    		} else {
    			window.location.href = '/profile';
    		}		
    	} else {
			if ($responseObject.error == "exists") {
				alert("There is already an account linked to this email. If you have forgot your password you can reset it above");
			} else {
				alert("There was an error creating your account, please try again and ensure credit card information is correct");
			}
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

