$(document).ready(function() {
    console.log( "ready!" );
    //addEateries(map);

});

function initMap() {
    
    //Initial Map Centered Around Brown University
    var brown = {lat: 41.826820, lng: -71.402931};
    map = new google.maps.Map(document.getElementById('map'), {
      zoom: 16,
      center: brown
    });

    var styles = [{"featureType": "landscape", "stylers": [{"saturation": -100}, {"lightness": 65}, {"visibility": "on"}]}, {"featureType": "poi", "stylers": [{"saturation": -100}, {"lightness": 51}, {"visibility": "simplified"}]}, {"featureType": "road.highway", "stylers": [{"saturation": -100}, {"visibility": "simplified"}]}, {"featureType": "road.arterial", "stylers": [{"saturation": -100}, {"lightness": 30}, {"visibility": "on"}]}, {"featureType": "road.local", "stylers": [{"saturation": -100}, {"lightness": 40}, {"visibility": "on"}]}, {"featureType": "transit", "stylers": [{"saturation": -100}, {"visibility": "simplified"}]}, {"featureType": "administrative.province", "stylers": [{"visibility": "off"}]}, {"featureType": "water", "elementType": "labels", "stylers": [{"visibility": "on"}, {"lightness": -25}, {"saturation": -100}]}, {"featureType": "water", "elementType": "geometry", "stylers": [{"hue": "#ffff00"}, {"lightness": -25}, {"saturation": -97}]}];
    map.set('styles', styles);



    addEateries(map);
    userLocation(map, 'Blue');
    //displayDeliverer(map, {lat: 41.826920, lng: -71.402731})

    //console.log(coord.length);
    //calcRoute("Kabob And Curry", {lat: 41.830556, lng: -71.402381});

}




function sendFormToServer() {
    //iterate through form
    var item = document.getElementById("distance").value;
    var time = document.getElementById("time").value;
    var payment= document.getElementById("payment").value;
    //if set location, get lat lng, otherwise use geocodeAddress()

    const promises = [];

    // var pickUpLocation = {
    //     lat: null,
    //     lng: null
    // };
    // if (Object.keys(campusFood).indexOf(pickUpLoc) >= 0) {
    //     pickUpLocation = campusFood[pickUpLoc]["location"];
    // } else {
    //     promises.push(geocode(pickUpLocation, pickUpLoc));
    // }

    // var dropOffLocation = {
    //     lat: null,
    //     lng: null
    // };
    // if (dropOffLoc == "Current Location") {
    //     dropOffLocation = userLatLonLocation;
    // } else {
    //     promises.push(geocode(dropOffLocation, dropOffLoc));
    // }
    // //item name, price, time

    // //iterate through deliverer array, get their lat/lon position
    // //time/duration for every deliverer

    // //user Location

    // Promise.all(promises).then(function() {
    //     console.log("Pick Up:");
    //     console.log(pickUpLocation);
    //     console.log("Drop Off:");
    //     console.log(dropOffLocation);
    //     console.log("Item:");
    //     console.log(item);
    //     console.log("Time:");
    //     console.log(time);
    //     console.log("Price:");
    //     console.log(price);
    //     console.log("About to submit!!!!")
    //     console.log(pickUpLoc)
    //     $.post("/submit-request", 
    //         {pickupLat: pickUpLocation.lat, 
    //             pickupLon: pickUpLocation.lng, 
    //             dropoffLat: dropOffLocation.lat, 
    //             dropoffLon: dropOffLocation.lng, 
    //             pickup: pickUpLoc,
    //             dropoff: dropOffLoc,
    //             item: item, time: time, price: price}, responseJSON => {
    //             console.log(responseJSON);
    //     });
    });
    //window.location.href = 'http://localhost:4567/requesting';
}