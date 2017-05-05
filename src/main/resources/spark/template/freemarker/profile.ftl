<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="css/profile.css">
    <script src="https://use.fontawesome.com/312d65a2e7.js"></script>
    <script src="http://code.jquery.com/jquery-1.11.0.min.js"></script>
    <script type="text/javascript" src="js/profile.js"></script>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    <script type="text/javascript" src="js/delivery_websockets.js"></script>
    <style>
    h1 {
        color: white;
        margin-left: 15px;
    }
    h2 {
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
    <div id = "user-profile">
        <h2>Profile</h2>
        <h3 id = "name"> Name </h3>
        <h4 id = "delivery-rating"> Delivery Rating: </h4>
        <h4 id = "request-rating"> Request Rating: </h4>
        <br>
        <h3> Contact Information </h3>
        <i class="fa fa-mobile fa-3x" aria-hidden="true"></i>
        <span id = "phone-number" class = "contact-info"></span>
        <br>
        <i class="fa fa-envelope fa-2x" aria-hidden="true"></i>
        <span id = "email" class = "contact-info"></span>
    </div>

    <div id = "history">
        <h2>History</h2>
        <h3>Order History</h3>
        <table id="table">
          <tr>
            <th>Pick-Up</th>
            <th>Drop-Off</th>
            <th>Item</th>
            <th>Price</th>
            <th>Rating</th>
          </tr>
          <tbody>
          </tbody>
        </table>
        <h3>Deliver History</h3>
        <table id="table">
          <tr>
            <th>Pick-Up</th>
            <th>Drop-Off</th>
            <th>Item</th>
            <th>Price</th>
            <th>Rating</th>
          </tr>
          <tbody>
          </tbody>
        </table>
    </div>
</body>