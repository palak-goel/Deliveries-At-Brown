<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="css/profile.css">
    <script src="https://use.fontawesome.com/312d65a2e7.js"></script>
    <script src="http://code.jquery.com/jquery-1.11.0.min.js"></script>
    <script type="text/javascript" src="js/profile.js"></script>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    <script type="text/javascript" src="js/delivery_websockets.js"></script>
    <style>
    h1 {
        color: white;
        margin-left: 15px;
    }
    </style>
</head>
<body style="margin: 0px;">
    <div id = "top-banner">
        <h1>Deliveries @ Brown</h1>
        <ul>
          <li><a href="http://localhost:4567/profile">Profile</a></li>
          <li><a href="http://localhost:4567/request">Request</a></li>
          <li><a href="http://localhost:4567/deliver">Deliver</a></li>
        </ul>
    </div>
	<h1>Profile</h1>
    <div id = "user-profile" class = "col-md-5">
        <h3> Image </h3>
        <i class="fa fa-mobile fa-3x" aria-hidden="true"></i>
        <br>
        <i class="fa fa-envelope fa-2x" aria-hidden="true"></i>
        <br>
        <i class="fa fa-credit-card fa-2x" aria-hidden="true"></i>
    </div>
    <div id = "user-info" class = "col-md-7">
        <h3> Name </h3>
        <h4> Delivery Rating: </h4>
        <h4> Request Rating: </h4>
    </div>
</body>