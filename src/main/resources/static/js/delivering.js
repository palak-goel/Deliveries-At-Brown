//pick up location
var pickup = {lat: parseFloat(localStorage.pickupLat), lng: parseFloat(localStorage.pickupLon)}
//drop off location
var dropoff = {lat: parseFloat(localStorage.dropoffLat), lng: parseFloat(localStorage.dropoffLon)}
//user location
var userLocation = {lat: parseFloat(localStorage.ulat), lng: parseFloat(localStorage.ulng)}

console.log(pickup)
console.log(dropoff)
$(document).ready(() => {
  var distance = 0;
  var duration = 0;
  var directionObject1 = {}
  new Promise(function(resolve, reject) {
      calcRoute(userLocation, pickup, directionObject1, resolve)
  }).then(function() {
    var dist1 = directionObject1["distance"]
    var dur1 = distanceObject1["duration"]
    var directionObject2 = {}
    new Promise(function(resolve, reject) {
      calcRoute(pickup, dropoff, directionObject2, resolve)
    }).then(function() {
      distance = dist1 + directionObject2["distance"];
      duration = dur1 + directionObject2["duration"];
    })
  })


	$("#pickup-loc").attr("placeholder", localStorage.pickup)
      $("#dropoff-loc").attr("placeholder", localStorage.dropoff)
    $("#item").attr("placeholder", localStorage.item)
    $("#time").attr("placeholder", distance)
    $("#price").attr("placeholder", duration)
})

function initMap() {
    //Initial Map Centered Around Brown University
    var brown = {lat: 41.826820, lng: -71.402931};
    map = new google.maps.Map(document.getElementById('map'), {
      zoom: 16,
      center: brown
    });
    userLocation(map, 'Red');
    addPickup(pickup);
    addDropoff(dropoff);
}

conn.onmessage = msg => {
  console.log(msg.data)
  const data = JSON.parse(msg.data)
  switch(data.type) {
          case MESSAGE_TYPE.CONNECT:
        console.log("HI");
        myId = data.id
        conn.send(JSON.stringify({jid: getJid(), type: MESSAGE_TYPE.CONNECT}))
          conn.send(JSON.stringify({type: MESSAGE_TYPE.GET_ORDERS}))
          console.log("INNER")
        break;
    /*
      case MESSAGE_TYPE.CONNECT:
        console.log("HI");
        myId = data.id
        conn.send(JSON.stringify({jid: jid, type: MESSAGE_TYPE.CONNECT}))
        break;*/
    case MESSAGE_TYPE.DELIVERED:
      localStorage.pLat = data.pLat;
      localStorage.pLng = data.pLng;
      localStorage.dLat = data.dLat;
      localStorage.dLng = data.dLng;
      window.location.href = 'http://localhost:4567/delivered';
      break;
  }
}

function submitOrder() {
  data = {type: MESSAGE_TYPE.REMOVE_ORDER, id: localStorage.id, jid: getJid(), dLat: localStorage.ulat, dLng: localStorage.ulng}
  conn.send(JSON.stringify(data))
}

function cancelOrder() {
  window.location.href = '/deliver';
}

