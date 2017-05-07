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
<div id="map"></div>
<div id="form">
    <center>
    <h3>Make a Request</h3>
    </center>
            <div class="form-group">
                <label for="formGroupExampleInput">Pick Up</label>
                <input id = "pick-up-loc" type="text" list="pickups" class="form-control" id="formGroupExampleInput" placeholder="Enter a pickup location ...">
                <datalist id="pickups">
                </datalist>
            </div>
            <div class="form-group">
                <label for="formGroupExampleInput">Drop Off</label>
                <input id = "drop-off-loc" type="text" list="dropoffs" class="form-control" id="formGroupExampleInput" placeholder="Enter a drop off location ...">
                <datalist id="dropoffs">
                </datalist>
            </div>
            <div class="form-group">
                <label for="formGroupExampleInput">Item</label>
                <input id = "item" type="text" list="items" class="form-control" id="formGroupExampleInput" placeholder="What do you want to get?">
                <datalist id="items">
                </datalist>
            </div>
            <div class="form-group">
                <label for="formGroupExampleInput">Time</label>
                <span><i>(Expiration time for request)</i></span>
                <input type="time" id="time" name="usr_time" class="form-control" id="formGroupExampleInput">
            </div>
            <div class="form-group">
                <label for="formGroupExampleInput">Tip</label> 
                <span><i>(How much extra would you be willing to pay?)</i></span>
                <input id = "price" type="number" min = "0" max = "20" step = "1" class="form-control" id="formGroupExampleInput" placeholder="Insert amount">
            </div>
        <center>
           <input type = "submit" id = "submit_order" value = "Submit Order">
           <br>
           <a id = "backLink" href = "/requesting" hidden>Go back to pending request</a>
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
    <script type="text/javascript" src="js/request.js"></script>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">

    <link rel="stylesheet" type="text/css" href="css/request.css">


    <style>
    h1 {
        color: white;
        padding-left: 15px;
    }
    h3 {
        color: #C0B283;
    }
    </style>