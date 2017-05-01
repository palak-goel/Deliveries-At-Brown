<!DOCTYPE html>
<html>
  <head>
  <script src="http://code.jquery.com/jquery-1.11.0.min.js"></script>
    <script type="text/javascript" src = "/js/font-awesome.js"></script>
    <script async defer
    src="https://maps.googleapis.com/maps/api/js?key=AIzaSyClZcJ2MUKJqouuGJRDIGqM1r9om9057sc&callback=initMap">
    </script>
    <script type="text/javascript" src="js/maps.js"></script>
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
    <div id="map"></div>
    <div id="form">
        <center>
        <h3>Make a Request</h3>
        </center>
        <form action = "http://localhost:4567/requesting">
            <div class="form-group">
                <label for="formGroupExampleInput">Pick Up</label>
                <input id = "pick-up-loc" type="text" class="form-control" id="formGroupExampleInput" placeholder="Example input">
            </div>
            <div class="form-group">
                <label for="formGroupExampleInput">Drop Off</label>
                <input id = "drop-off-loc" type="text" class="form-control" id="formGroupExampleInput" placeholder="Example input">
            </div>
            <div class="form-group">
                <label for="formGroupExampleInput">Item</label>
                <input id = "item" type="text" class="form-control" id="formGroupExampleInput" placeholder="Example input">
            </div>
            <div class="form-group">
                <label for="formGroupExampleInput">Time</label>
                <input id = "time" type="text" class="form-control" id="formGroupExampleInput" placeholder="Example input">
            </div>
            <div class="form-group">
                <label for="formGroupExampleInput">Price</label>
                <input id = "price" type="text" class="form-control" id="formGroupExampleInput" placeholder="Example input">
            </div>
            <center>
                <button onclick = "sendFormToServer();"> Submit Order </button>
            </center>
            </form>
        </div>
     </body>
</html>

