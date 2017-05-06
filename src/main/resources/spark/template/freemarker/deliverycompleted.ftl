<#assign content>
	<div id = "top-banner">
		<h1> Deliveries @ Brown </h1>
		<ul>
          <li><a href="/profile">Profile</a></li>
          <li><a href="/request">Request</a></li>
          <li><a href="/deliver">Deliver</a></li>
        </ul>
        <button type="button" id = "logout" class="btn btn-default">Logout</button>
	</div>
    <center>
    <h3> You have completed your delivery! </h3>
    <h4> Please give the requestor a rating below: </h4>
    <br>
    <div id = "rating">
        <input id = "ratingField" type="number" min = "0" max = "5" step = "1" class="form-control" id="formGroupExampleInput">
    </div>
    <br>
    <button type="button" class="btn btn-success" id="so" onclick="submitRating();">Confirm</button>
    </center>
</#assign>
<#include "main.ftl">
<link rel="stylesheet" type="text/css" href="css/requesting.css">
    <script type="text/javascript" src = "/js/font-awesome.js"></script>
    <script src="http://code.jquery.com/jquery-1.11.0.min.js"></script>
    <script type="text/javascript" src="js/maps.js"></script>
    <script type="text/javascript" src="js/delivery_websockets.js"></script>
    <script type="text/javascript" src="js/deliverycompleted.js"></script>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    <style>
    h1 {
        color: white;
        padding-left: 15px;
    }
    h3 {
        color: #C0B283;
    }
    #rating {
        width: 50px;
    }
    </style>