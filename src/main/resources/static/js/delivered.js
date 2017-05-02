var pickupLocation = {lat: 41.826815, lng: -71.403174}
var requsterLocation = {lat: 41.826920, lng: -71.402731}
//pick up location
var pickup = {lat: 41.830556, lng: -71.402381}
//drop off location
var dropoff = {lat: 41.826997, lng: -71.400221}

function initMap() {
    var brown = {lat: 41.826820, lng: -71.402931};
    map = new google.maps.Map(document.getElementById('map'), {
      zoom: 16,
      center: brown
    });
    //deliverer location:
    pos = userLocation(map, 'red');
    console.log(userLatLonLocation)
    //calcRoute(currLocation, pickup);
    //calcRoute(pickup, currLocation);
}