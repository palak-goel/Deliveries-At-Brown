<#assign content> 
    <div id = "top-banner">
        <h1>Deliveries @ Brown</h1>
        <ul>
          <li><a href="/profile">Profile</a></li>
          <li><a href="/request">Request</a></li>
          <li><a href="/deliver">Deliver</a></li>
        </ul>
    </div>
	<div id="map"></div>
	<div id="right">
        <h3> Directions </h3>
        <h5 id = "pick-up-loc"></h5>
        <h5 id = "pick-up-item"></h5>
        <p id = "pickup-dirs"></p>
        <p id = "dropoff-dirs"></p>
        <p id = "pickup-dirs2"></p>
        <p id = "dropoff-dirs2"></p>

        <h3 id="contact-name"> Contact </h3>
        <textarea id = "text-message" placeholder = "Message Requester"></textarea>
        <br>
        <button onclick = "sendTextToDeliverer();">Send</button>
        <br>
        <br>
        <h3> Price </h3>
        <div class="form-group">
            <span><input id = "price" type="number" min = "0" step = "0.01" class="form-control" id="formGroupExampleInput" placeholder="Insert amount"></span>
        </div>
        <button onclick = "completeDelivery();">Complete Delivery</button>
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