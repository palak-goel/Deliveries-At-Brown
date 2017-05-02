//pick up location
var pickup = {lat: 41.830556, lng: -71.402381}
//drop off location
var dropoff = {lat: 41.826997, lng: -71.400221}
//deliverer location
var requestorLocation = {lat: 41.826920, lng: -71.402731}
//deliverer name
var requestorName = "Sumit Sohani";
//deliverer number
var requestorNumber = "(510) - 676 - 3683";


function initMap() {
    document.getElementById("requestor-name").innerText = requestorName;
    document.getElementById("requestor-num").innerText= requestorNumber;


    //Initial Map Centered Around Brown University
    var brown = {lat: 41.826820, lng: -71.402931};
    map = new google.maps.Map(document.getElementById('map'), {
      zoom: 16,
      center: brown
    });
    var styles = [{"featureType": "landscape", "stylers": [{"saturation": -100}, {"lightness": 65}, {"visibility": "on"}]}, {"featureType": "poi", "stylers": [{"saturation": -100}, {"lightness": 51}, {"visibility": "simplified"}]}, {"featureType": "road.highway", "stylers": [{"saturation": -100}, {"visibility": "simplified"}]}, {"featureType": "road.arterial", "stylers": [{"saturation": -100}, {"lightness": 30}, {"visibility": "on"}]}, {"featureType": "road.local", "stylers": [{"saturation": -100}, {"lightness": 40}, {"visibility": "on"}]}, {"featureType": "transit", "stylers": [{"saturation": -100}, {"visibility": "simplified"}]}, {"featureType": "administrative.province", "stylers": [{"visibility": "off"}]}, {"featureType": "water", "elementType": "labels", "stylers": [{"visibility": "on"}, {"lightness": -25}, {"saturation": -100}]}, {"featureType": "water", "elementType": "geometry", "stylers": [{"hue": "#ffff00"}, {"lightness": -25}, {"saturation": -97}]}];
    map.set('styles', styles);

    userLocation(map, 'Blue');
    displayDeliverer(map, Location)
    addPickup(pickup);
    addDropoff(dropoff);
    calcRoute(requestorLocation, pickup);
    calcRoute(pickup, dropoff);
    

    //console.log(coord.length);
    //calcRoute("Kabob And Curry", {lat: 41.830556, lng: -71.402381});

}

function sendTextToDeliverer() {
    var message = document.getElementById("text-message").value;
    console.log(message);
}