<!DOCTYPE html>
<html>
  <head>
    <link rel="stylesheet" type="text/css" href="css/request.css">
    <script type="text/javascript" src = "/js/font-awesome.js"></script>
    <script async defer
    src="https://maps.googleapis.com/maps/api/js?key=AIzaSyClZcJ2MUKJqouuGJRDIGqM1r9om9057sc&callback=initMap">
    </script>
    <script src="http://code.jquery.com/jquery-1.11.0.min.js"></script>
    <script type="text/javascript" src="js/maps.js"></script>
    <script type="text/javascript" src="js/request.js"></script>

  </head>
  <body>
    <h1>Deliveries At Brown</h1>
    <div id="map"></div>
    <div id="form">
            <center>
            <h3>Make a Request</h3>
            </center>
            
            <form action = "http://localhost:4567/requesting">
            Pick Up Location: <input id = "pick-up-loc" type = "text">
            <br>
            Drop Off Location: <input id = "drop-off-loc" type = "text">
            <br>
            Item: <input id = "item" type = "text">
            <br>
            Time: <input id = "time" type = "text">
            <br>
            Price: <input id = "price" type = "text">
            <br>
            <button onclick = "sendFormToServer();">
                Submit!
            </button>
            </form>
        </div>
     </body>
</html>

