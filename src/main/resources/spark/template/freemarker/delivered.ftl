<#assign content> 
    <div id = "top-banner">
        <h1>Deliveries @ Brown</h1>
        <ul>
          <li><a href="/profile">Profile</a></li>
          <li><a href="/request">Request</a></li>
          <li><a href="/deliver">Deliver</a></li>
        </ul>
        <button type="button" id = "logout" class="btn btn-default">Logout</button>
    </div>
	<div id="map"></div>
	<div id="right">
        <h3> Directions </h3>
        <p id = "distance"></p>
        <p id = "duration"></p>
        <p id = "pickup-dirs"></p>
        <p id = "dropoff-dirs"></p>
        <p id = "distance2"></p>
        <p id = "duration2"></p>
        <p id = "pickup-dirs2"></p>
        <p id = "dropoff-dirs2"></p>

        <h3> Contact </h3>
        <h4 id = "deliverer-num">Phone Number</h5>
        <textarea id = "text-message" placeholder = "Message Deliverer"></textarea>
        <br>
        <button onclick = "sendTextToDeliverer();">Send</button>
        <br>
        <br>
        <form action = "/deliverycompleted">
            <button onclick = "completeDelivery();">Complete Delivery</button>
        </form>
    </div>
    </#assign>
    <#include "main.ftl">

    <link rel="stylesheet" type="text/css" href="css/delivered.css">
    <script type="text/javascript" src = "/js/font-awesome.js"></script>
    <script async defer
    src="https://maps.googleapis.com/maps/api/js?key=AIzaSyClZcJ2MUKJqouuGJRDIGqM1r9om9057sc&callback=initMap">
    </script>
    <script src="http://code.jquery.com/jquery-1.11.0.min.js"></script>
    <script type="text/javascript" src="js/delivered.js"></script>
    <script type="text/javascript" src="js/maps.js"></script>
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