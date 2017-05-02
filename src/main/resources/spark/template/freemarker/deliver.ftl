<!DOCTYPE html>
<html>
  <head>
    <script type="text/javascript" src = "/js/font-awesome.js"></script>
    <script async defer
    src="https://maps.googleapis.com/maps/api/js?key=AIzaSyClZcJ2MUKJqouuGJRDIGqM1r9om9057sc&callback=initMap">
    </script>
    <script type="text/javascript" src="js/maps.js"></script>
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
    <div id="queue">
        <br><br>
        <table>
          <tr>
            <th>Pickup Location</th>
            <th>Dropoff Location</th>
            <th>Distance</th>
            <th>Duration</th>
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
                <input class="form-control" type="text" id="example-text-input">
              </div>
            </div>
            <div class="form-group row">
              <label for="example-text-input" class="col-2 col-form-label">Distance</label>
              <div class="col-10">
                <input class="form-control" type="text" id="example-text-input">
              </div>
            </div>
            <div class="form-group row">
              <label for="example-text-input" class="col-2 col-form-label">Time</label>
              <div class="col-10">
                <input class="form-control" type="text" id="example-text-input">
              </div>
            </div>
        </div>
     </body>
</html>
