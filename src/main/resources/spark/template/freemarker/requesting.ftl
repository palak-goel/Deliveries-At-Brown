<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="css/requesting.css">
    <script type="text/javascript" src = "/js/font-awesome.js"></script>
    <script async defer
    src="https://maps.googleapis.com/maps/api/js?key=AIzaSyClZcJ2MUKJqouuGJRDIGqM1r9om9057sc&callback=initMap">
    </script>
    <script src="http://code.jquery.com/jquery-1.11.0.min.js"></script>
    <script type="text/javascript" src="js/maps.js"></script>
    <script type="text/javascript" src="js/requesting.js"></script>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    <style>
    h1 {
        color: white;
        padding-left: 15px;
    }
    h3 {
        color: #C0B283;
    }
    </style>
</head>
<body style="margin: 0px;">
	<div id = "top-banner">
		<h1> Deliveries @ Brown </h1>
		<ul>
          <li><a href="http://localhost:4567/profile">Profile</a></li>
          <li><a href="http://localhost:4567/request">Request</a></li>
          <li><a href="http://localhost:4567/deliver">Deliver</a></li>
        </ul>
	</div>
	<div id="map"></div>
	<center>
	<div id="right">
		<div id = "loading">
			<div class="loader"></div>
		</div>
		<div id = "choices">
			<form action = "http://localhost:4567/requested">
				<fieldset disabled>
				    <div class="form-group">
				      <label for="disabledTextInput">Pick Up Location</label>
				      <input type="text" id = "pickup-loc" class="form-control" placeholder=${pickup}>
				    </div>
				    <div class="form-group">
				      <label for="disabledTextInput">Drop Off Location</label>
				      <input type="text" id = "dropoff-loc" class="form-control" placeholder="text">
				    </div>
				    <div class="form-group">
				      <label for="disabledTextInput">Item</label>
				      <input type="text" id = "item" class="form-control" placeholder="text">
				    </div>
				    <div class="form-group">
				      <label for="disabledTextInput">Time</label>
				      <input type="text" id = "time" class="form-control" placeholder="text">
				    </div>
				    <div class="form-group">
				      <label for="disabledTextInput">Price</label>
				      <input type="text" id = "price" class="form-control" placeholder="text">
				    </div>
			 	 </fieldset>
			 	 <input type="submit"></input>
			</form>
		</div>
	</div>
	</center>
</body>
</html>