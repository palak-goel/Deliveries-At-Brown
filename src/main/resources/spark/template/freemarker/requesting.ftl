<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="css/requesting.css">
    <script type="text/javascript" src = "/js/font-awesome.js"></script>
    <script async defer
    src="https://maps.googleapis.com/maps/api/js?key=AIzaSyClZcJ2MUKJqouuGJRDIGqM1r9om9057sc&callback=initMap">
    </script>
    <script type="text/javascript" src="js/maps.js"></script>

    <script type="text/javascript" src="js/requesting.js"></script>

</head>
<body>
	<h1> Requesting </h1>
	<div id="map"></div>
	<center>
	<div id="right">
		<div class="loader"></div>
		<form action = "http://localhost:4567/requested">
			<input type = "submit"></input>
		</form>
	</div>
	</center>
</body>
</html>