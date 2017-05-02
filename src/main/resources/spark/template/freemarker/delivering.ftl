<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="css/requested.css">
    <script type="text/javascript" src = "/js/font-awesome.js"></script>
    <script async defer
    src="https://maps.googleapis.com/maps/api/js?key=AIzaSyClZcJ2MUKJqouuGJRDIGqM1r9om9057sc&callback=initMap">
    </script>
    <script src="http://code.jquery.com/jquery-1.11.0.min.js"></script>
    <script type="text/javascript" src="js/maps.js"></script>
    <script type="text/javascript" src="js/delivering.js"></script>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    <style>
    h1 {
        color: white;
    }
    h3 {
        color: #C0B283;
    }
    </style>

</head>
<body>
	<div id = "top-banner">
        <h1>Deliveries @ Brown</h1>
    </div>
	<div id="map"></div>
	<div id="right">
        <h3 id = "requestor-name">Requestor</h3>
        <h4 id = "requestor-num">Phone Number</h4>
        <h5 id = "requestor-items">Items</h5>
        <h6 id = "requestor-start-time">Pick-up Time</h6>
        <h7 id = "requestor-end-time">Dropoff Time</h7>
        <br>
        <textarea id = "text-message" placeholder = "Message Requestor"></textarea>
        <br>
        <button type = "Submit" onclick = "sendTextToRequestor();">Send</button>
    </div>
</body>
</html>