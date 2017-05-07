//pick up location
var pickup = {lat: parseFloat(localStorage.pickupLat), lng: parseFloat(localStorage.pickupLon)}
//drop off location
var dropoff = {lat: parseFloat(localStorage.dropoffLat), lng: parseFloat(localStorage.dropoffLon)}
//user location
var uLocation = {lat: parseFloat(localStorage.ulat), lng: parseFloat(localStorage.ulng)}

console.log(pickup)
console.log(dropoff)


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

    var distance = 0;
    var duration = 0;
    var directionObject1 = {}
    new Promise(function(resolve, reject) {
      calcRoute(uLocation, pickup, directionObject1, resolve)
    }).then(function() {
      var dist1 = directionObject1["distance"]
      var dur1 = directionObject1["duration"]
      var directionObject2 = {}
      new Promise(function(resolve, reject) {
        calcRoute(pickup, dropoff, directionObject2, resolve)
      }).then(function() {
        distance = parseFloat(dist1) + parseFloat(directionObject2["distance"]);
        duration = parseFloat(dur1) + parseFloat(directionObject2["duration"]);
        $("#pickup-loc").attr("placeholder", localStorage.pickup)
        $("#dropoff-loc").attr("placeholder", localStorage.dropoff)
        $("#item").attr("placeholder", localStorage.item)
        $("#time").attr("placeholder", distance)
        $("#price").attr("placeholder", "" + duration + " minutes")
      })
    })
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
      if (data.error === "TIME") {
        console.log("error")
        window.location.href = "/deliver"
      } else {
        console.log(data)
        localStorage.pLat = data.pLat;
        localStorage.pLng = data.pLng;
        localStorage.dLat = data.dLat;
        localStorage.dLng = data.dLng;
        localStorage.dcell = data.dcell;
        localStorage.rcell = data.rcell
        window.location.href = '/delivered';
      }
      break;
  }
}

function submitOrder() {
  data = {type: MESSAGE_TYPE.REMOVE_ORDER, id: localStorage.id, jid: getJid(), dLat: localStorage.ulat, dLng: localStorage.ulng}
  console.log(data)
  conn.send(JSON.stringify(data))
}

function cancelOrder() {
  window.location.href = '/deliver';
}


