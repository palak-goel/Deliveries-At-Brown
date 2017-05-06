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
    <center>
        <h2> Deliverer </h2>
        <h3 id = "deliverer-name">Deliverer</h3>
        <h4 id = "deliverer-num">Phone Number</h5>
        <textarea id = "text-message" placeholder = "Message Deliverer"></textarea>
        <br>
        <button onclick = "sendTextToDeliverer();">Send</button>
    </center>
</div>
</#assign>
<#include "main.ftl">
<link rel="stylesheet" type="text/css" href="css/requested.css">
<script type="text/javascript" src = "/js/font-awesome.js"></script>
<script async defer
src="https://maps.googleapis.com/maps/api/js?key=AIzaSyClZcJ2MUKJqouuGJRDIGqM1r9om9057sc&callback=initMap">
</script>
<script src="http://code.jquery.com/jquery-1.11.0.min.js"></script>
<script type="text/javascript" src="js/requested.js"></script>
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