var pickupLocation = {lat: 41.826815, lng: -71.403174}
var requsterLocation = {lat: 41.826920, lng: -71.402731}
//pick up location
//var pickup = {lat: 41.830556, lng: -71.402381}
//drop off location
//var dropoff = {lat: 41.826997, lng: -71.400221}
var pickup = {lat: parseFloat(localStorage.pLat), lng: parseFloat(localStorage.pLng)}
var dropoff = {lat: parseFloat(localStorage.dLat), lng: parseFloat(localStorage.dLng)}

function initMap() {
    document.getElementById("contact-name").innerText = localStorage.name;
    var brown = {lat: 41.826820, lng: -71.402931};
    map = new google.maps.Map(document.getElementById('map'), {
      zoom: 16,
      center: brown
    });
    //deliverer location:
    pos = userLocation(map, 'red');

    addPickup(pickup);
    addDropoff(dropoff);

    userPositionArgs = {}
    new Promise(function(resolve, reject) {
        getUserLocation(userPositionArgs, resolve);
    }).then(function() {
        var pickUpDirs = {}
        new Promise(function(resolve, reject) {
            calcRoute(userPositionArgs, pickup, pickUpDirs, resolve)
        }).then(function() {
            document.getElementById("distance").innerText = pickUpDirs["distance"]
            document.getElementById("duration").innerText = pickUpDirs["duration"]
            var pickUpDirections = ""
            for (var i = 0; i < pickUpDirs["directions"].length; i++) {
                pickUpDirections += pickUpDirs["directions"][i].instructions.replace(/<[^>]*>/g, " ");
                pickUpDirections += '\n';
                console.log(pickUpDirections)
            }
            document.getElementById("pickup-dirs").innerText = pickUpDirections;
        })
        var dropoffDirs = {}
        new Promise(function(resolve, reject) {
            calcRoute(pickup, dropoff, dropoffDirs, resolve)
        }).then(function() {
            document.getElementById("distance2").innerText = dropoffDirs["distance"]
            document.getElementById("duration2").innerText = dropoffDirs["duration"]
            var dropoffDirections = ""
            for (var i = 0; i < dropoffDirs["directions"].length; i++) {
                dropoffDirections += dropoffDirs["directions"][i].instructions.replace(/<[^>]*>/g, " ");
                dropoffDirections += '\n';
            }
            document.getElementById("pickup-dirs2").innerText = dropoffDirections;
        })
    });

    //calcRoute(userPosition, pickup);
    //calcRoute(pickup, dropoff);
}

function completeDelivery() {
    $.post("/complete-order", {id: localStorage.id}, responseJson => {
        window.location.href = '/deliverycompleted';
    })
    return directions;
}

