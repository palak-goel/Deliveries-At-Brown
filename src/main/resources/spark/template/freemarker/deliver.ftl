<#assign content>
<div id = "top-banner">
    <h1>Deliveries @ Brown</h1>
    <ul>
      <li><a href="/profile">Profile</a></li>
      <li><a href="/request">Request</a></li>
      <li><a href="/deliver">Deliver</a></li>
    </ul>
    <div id = "logout-div">
            <button type="button" id = "logout" class="btn btn-default">Logout</button>
        </div>
</div>
<div id="queue">
    <br><br>
    <table id="table">
      <tr>
        <th>Pick-Up Location</th>
        <th>Rating</th>
        <th>Duration</th>
        <th>Distance</th>
        <th>Claim By</th>
        <th>Tip</th>
        <th>Item</th>
        <th>Accept</th>
      </tr>
      <tbody>
      </tbody>
    </table>
</div>

<div id="form">
    <h3>Order by:</h3>
    <form id="radio">
    <input type="radio" name="order" value="tip" checked> Tip<br>
    <input type="radio" name="order" value="distance"> Distance<br>
    <input type="radio" name="order" value="time"> Time
    </form>
    <center>
       <button onclick = "sendOrdering();"> Sort </button>
    </center>
    <h3>Custom</h3>
    <div class="form-group">
        <label for="formGroupExampleInput">Tip</label> 
        <span><i>(How much would you like for the delivery?)</i></span>
        <input id = "price" type="number" min = "0" max = "20" step = "1" class="form-control" id="formGroupExampleInput" placeholder="Desired tip">
    </div>
    <div class="form-group">
      <label for="formGroupExampleInput">Distance</label>
      <span><i>(In miles)</i></span>
        <input id = "distance" type="number" min = "0" max = "10" step = "0.1" class="form-control" id="formGroupExampleInput" placeholder="How far?">
    </div>
    <div class="form-group">
      <label for="formGroupExampleInput">Duration</label>
      <span><i>(In minutes)</i></span>
        <input id = "duration" type="number" min = "0" max = "60" step = "0.1" class="form-control" id="formGroupExampleInput" placeholder="How long?">
    </div>
    <center>
       <button onclick = "submitPreferencesToServer();"> Submit Preferences </button>
    </center>
</div>
</#assign>
<#include "main.ftl">
<script src="http://code.jquery.com/jquery-1.11.0.min.js"></script>
<script type="text/javascript" src = "/js/font-awesome.js"></script>
<script async defer
    src="https://maps.googleapis.com/maps/api/js?key=AIzaSyClZcJ2MUKJqouuGJRDIGqM1r9om9057sc&callback=initMap">
    </script>
<script type="text/javascript" src="js/maps.js"></script>
<script type="text/javascript" src="js/delivery_websockets.js"></script>
<script type="text/javascript" src="js/deliver.js"></script>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
<link rel="stylesheet" type="text/css" href="css/deliver.css">

<style>


h1 {
    color: white;
    padding-left: 15px;
}
h3 {
    color: #C0B283;
}
</style>

