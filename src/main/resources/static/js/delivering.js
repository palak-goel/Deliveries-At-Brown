//pick up location
var pickup = {lat: parseFloat(localStorage.pickupLat), lng: parseFloat(localStorage.pickupLon)}
//drop off location
var dropoff = {lat: parseFloat(localStorage.dropoffLat), lng: parseFloat(localStorage.dropoffLon)}
console.log(pickup)
console.log(dropoff)
$(document).ready(() => {
	$("#pickup-loc").attr("placeholder", localStorage.pickup)
      $("#dropoff-loc").attr("placeholder", localStorage.dropoff)
    $("#item").attr("placeholder", localStorage.item)
    $("#time").attr("placeholder", "TODO")
    $("#price").attr("placeholder", "TODO")
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

