var has_submitted = false;
var id = "";

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
      if (data.error === "CLAIMED" || data.error === "TIME") {
        console.log("ERROR");
        window.location.href = '/request'
      } else {
        localStorage.name = data.name;
        localStorage.cell = data.phone;
        localStorage.dLat = data.delivLat;
        localStorage.dLng = data.delivLng;
        window.location.href = '/requested';
      }
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
            var dr_loc = data.dropoff
            if (dr_loc === "Current Location") {
              dr_loc = "User Location"
            }
      $("#dropoff-loc").attr("placeholder", dr_loc)
    $("#item").attr("placeholder", data.item)
    $("#time").attr("placeholder", data.time)
    $("#price").attr("placeholder", data.price)
               dta = data;
      });
  $.post("/is-active", 
  {}, responseJSON => {
    data_resp = JSON.parse(responseJSON)
    console.log(data_resp)
    if (data_resp.isActive ===true) {
      $('#so').attr("disabled", true);
    }
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
  document.getElementById("status-text").innerText = "Waiting For Deliverer"
  $.post("/submit-request", dta, responseJSON => {
    data = JSON.parse(responseJSON)
    id = data.id
    localStorage.id = id;
    has_submitted = true;
          console.log(responseJSON);
          $('#so').attr("disabled", true);
          //window.location.href = '/requesting';
        });
}

function cancelOrder() {
  $.post("/is-active", 
  {}, responseJSON => {
    data_resp = JSON.parse(responseJSON)
    console.log(data_resp)
    if (data_resp.isActive ===true) {
      $.post("/delete-order", {id: localStorage.id}, responseJSON => {
 window.location.href = '/request';
    });
    } else {
      window.location.href = '/request';
    }
    });
  /*if (has_submitted) {
    console.log("POSTING")
    $.post("/delete-order", {id: id}, responseJSON => {
 window.location.href = '/request';
    })
  } else {
  window.location.href = '/request';
}*/
}
