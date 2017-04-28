
//pick up location
var pickup = {lat: 41.830556, lng: -71.402381}
//drop off location
var dropoff = {lat: 41.826997, lng: -71.400221}
//deliverer location
var delivererLocation = {lat: 41.826920, lng: -71.402731}
//delivery info

function initMap() {
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
    
    //console.log(coord.length);
    //calcRoute("Kabob And Curry", {lat: 41.830556, lng: -71.402381});

}
