//pick up location
var pickup = {lat: parseFloat(localStorage.pickupLat), lng: parseFloat(localStorage.pickupLon)}
//drop off location
var dropoff = {lat: parseFloat(localStorage.dropoffLat), lng: parseFloat(localStorage.dropoffLon)}
//deliverer location
var delivererLocation = {lat: parseFloat(localStorage.dLat), lng: parseFloat(localStorage.dLng)}
//deliverer name
var delivererName = capitalizeFirstLetter(localStorage.name.split(" ")[0]) + " " + capitalizeFirstLetter(localStorage.name.split(" ")[1]);
//deliverer number
var delivererNumber = localStorage.cell;

function initMap() {
    document.getElementById("deliverer-name").innerText = delivererName;

    //Initial Map Centered Around Brown University
    var brown = {lat: 41.826820, lng: -71.402931};
    map = new google.maps.Map(document.getElementById('map'), {
      zoom: 16,
      center: brown
    });

    userLocation(map, 'Blue');
    displayDeliverer(map, delivererLocation)
    addPickup(pickup);
    addDropoff(dropoff);

    pickupDirs = {}
    new Promise(function(resolve, reject) {
        calcRoute(delivererLocation, pickup, pickupDirs, resolve);
    }).then(function() {
      dropoffDirs = {}
      new Promise(function(resolve, reject) {
          calcRoute(pickup, dropoff, dropoffDirs, resolve);
      }).then(function() {
        var durationMessage = "Your order will take ";
        var duration = parseFloat(pickupDirs["duration"]) + parseFloat(dropoffDirs["duration"])
        durationMessage += duration
        durationMessage += " minutes to complete"
        var dropoffTime = new Date();
        dropoffTime.setMinutes(dropoffTime.getMinutes() + duration);
        document.getElementById("duration").innerText = durationMessage
      })
    })

    

    //console.log(coord.length);
    //calcRoute("Kabob And Curry", {lat: 41.830556, lng: -71.402381});

}

function sendTextToDeliverer() {
    var message = document.getElementById("text-message").value;
    const postParameters = {
        number : delivererNumber,
        content : message
    }
    $.post("/sendText", postParameters, responseJSON => {
        });    
}

conn.onmessage = msg => {
  console.log(msg.data)
  const data = JSON.parse(msg.data)
  switch(data.type) {
    case MESSAGE_TYPE.CONNECT:
        console.log("HI");
        myId = data.id
        conn.send(JSON.stringify({jid: getJid(), type: MESSAGE_TYPE.CONNECT}))
        break;
    case MESSAGE_TYPE.COMPLETED:
      console.log("REQUESTED");
      window.location.href = '/ordercompleted';
      break;
  }
}

function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}
