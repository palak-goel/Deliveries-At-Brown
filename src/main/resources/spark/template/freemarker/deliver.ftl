<#assign content>
<div id = "top-banner">
    <h1>Deliveries @ Brown</h1>
    <ul>
      <li><a href="http://localhost:4567/profile">Profile</a></li>
      <li><a href="http://localhost:4567/request">Request</a></li>
      <li><a href="http://localhost:4567/deliver">Deliver</a></li>
    </ul>
    <button type="button" id = "logout" class="btn btn-default">Logout</button>
</div>
<div id="queue">
    <br><br>
    <table id="table">
      <tr>
        <th>Pick-Up Location</th>
        <th>Drop-Off Location</th>
        <th>Time</th>
        <th>Price</th>
        <th>Item</th>
        <th>Accept</th>
      </tr>
      <tbody>
      </tbody>
    </table>
</div>

<div id="form">
    <h3>Filters</h3>
    <div class="form-group row">
      <label for="example-text-input" class="col-2 col-form-label">Price</label>
      <div class="col-10">
        <input class="form-control" type="text" id="price">
      </div>
    </div>
    <div class="form-group row">
      <label for="example-text-input" class="col-2 col-form-label">Distance</label>
      <div class="col-10">
        <input class="form-control" type="text" id="distance">
      </div>
    </div>
    <div class="form-group row">
      <label for="example-text-input" class="col-2 col-form-label">Time</label>
      <div class="col-10">
        <input class="form-control" type="text" id="time">
      </div>
    </div>
    <center>
       <button onclick = "sendPreferencesToServer();"> Submit Preferences </button>
    </center>
</div>
</#assign>
<#include "main.ftl">
<script src="http://code.jquery.com/jquery-1.11.0.min.js"></script>
<script type="text/javascript" src = "/js/font-awesome.js"></script>
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
