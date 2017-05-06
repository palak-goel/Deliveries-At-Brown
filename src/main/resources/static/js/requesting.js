
conn.onmessage = msg => {
  console.log(msg.data)
  const data = JSON.parse(msg.data)
  switch(data.type) {
    case MESSAGE_TYPE.CONNECT:
        console.log("HI");
        myId = data.id
        conn.send(JSON.stringify({jid: getJid(), type: MESSAGE_TYPE.CONNECT}))
        break;
    case MESSAGE_TYPE.REQUESTED:
      const jid = getJid()
      console.log("REQUESTED");
      localStorage.name = data.name;
      localStorage.cell = data.phone;
      window.location.href = 'http://localhost:4567/requested';
      break;
  }
}

var dta = {};

$(document).ready(() => {
  $.post("/pending-orders", 
      {}, responseJSON => {
            data = JSON.parse(responseJSON)
            console.log(responseJSON);
            $("#pickup-loc").attr("placeholder", data.pickup)
      $("#dropoff-loc").attr("placeholder", data.dropoff)
    $("#item").attr("placeholder", data.item)
    $("#time").attr("placeholder", data.time)
    $("#price").attr("placeholder", data.price)
               dta = data;
      });
    
});
//pick up location
var pickup = {lat: parseFloat(localStorage.pickupLat), lng: parseFloat(localStorage.pickupLon)}
//drop off location
var dropoff = {lat: parseFloat(localStorage.dropoffLat), lng: parseFloat(localStorage.dropoffLon)}
//deliverer location
//var delivererLocation = {lat: 41.826920, lng: -71.402731}
//delivery info

function initMap() {
    //Initial Map Centered Around Brown University
    var brown = {lat: 41.826820, lng: -71.402931};
    map = new google.maps.Map(document.getElementById('map'), {
      zoom: 16,
      center: brown
    });
    userLocation(map, 'Blue');
    //displayDeliverer(map, delivererLocation)
    addPickup(pickup);
    addDropoff(dropoff);
    
    //console.log(coord.length);
    //calcRoute("Kabob And Curry", {lat: 41.830556, lng: -71.402381});

}

function submitOrder() {
  $.post("/submit-request", dta, responseJSON => {
                console.log(responseJSON);
                $('#so').attr("disabled", true);
                //window.location.href = '/requesting';
        });
}

function cancelOrder() {
  window.location.href = '/request';
}
