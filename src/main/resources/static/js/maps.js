var map;
var directionsDisplay;
var directionsService;
var userLatLonLocation;
var currLocation;


function getDistanceDuration(start, end, dirs, resolve) {
    var request = {
        origin: start,
        destination: end,
        travelMode: 'WALKING'
    };
    var directionsService = new google.maps.DirectionsService();
    directionsService.route(request, function(result, status) {
        if (status == 'OK') {
            //directionsDisplay.setDirections(result);
            //distance of route
            distance = result.routes[0].legs[0].distance.text;
            duration = result.routes[0].legs[0].duration.text;
            console.log(distance)
            console.log(duration)             
            var directions = result.routes[0].legs[0].steps;
            for (var i = 0; i < directions.length; i++) {
                console.log(directions[i].instructions)
            }

            dirs["distance"] = distance
            dirs["duration"] = duration
            dirs["directions"] = directions
        }
        resolve()
    });
}


function calcRoute(start, end, dirs, resolve) {     
    var request = {
        origin: start,
        destination: end,
        travelMode: 'WALKING'
    };

    var directionsService = new google.maps.DirectionsService();
    var directionsDisplay = new google.maps.DirectionsRenderer();
    directionsDisplay.setMap(map);
    directionsDisplay.setOptions( { suppressMarkers: true } );
    directionsService.route(request, function(result, status) {
        if (status == 'OK') {
            directionsDisplay.setDirections(result);
            //distance of route
            distance = result.routes[0].legs[0].distance.text;
            duration = result.routes[0].legs[0].duration.text;
            console.log(distance)
            console.log(duration)             
            var directions = result.routes[0].legs[0].steps;
            for (var i = 0; i < directions.length; i++) {
                console.log(directions[i].instructions)
            }

            dirs["distance"] = distance
            dirs["duration"] = duration
            dirs["directions"] = directions
        }
        resolve()
    });
}

/*setInterval(function run() {
    userLocation(map, 'Blue');
}, 1000)*/

//Centers the Map Around the user's current location, creates user marker
var userMarkers = [];
function userLocation(map, color) {
    /*if (userMarkers.length > 0) {
        for(i=0; i<userMarkers.length; i++){
            userMarkers[i].setMap(null);
        }
    }*/
    infoWindow = new google.maps.InfoWindow;
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function(position) {
            var pos = {
          lat: position.coords.latitude,
          lng: position.coords.longitude
        };

        infoWindow.setPosition(pos);
        infoWindow.setContent('Location found.');

        var userPos = new google.maps.Marker({
            position: pos,
            icon: {
                path: fontawesome.markers.USER,
                scale: 0.3,
                strokeWeight: 1.0,
                fillColor: color,
                fillOpacity: 1.0
            },
            map: map
        })
        userLatLonLocation = pos;
        userMarkers.push(userPos);
        //calcRoute({lat: 41.830556, lng: -71.402381}, pos);
        bindIconListenerToUser(map, userPos, "Current Location");
        map.setCenter(pos);
        toReturn = pos;
      }, function() {
        handleLocationError(true, infoWindow, map.getCenter());
      });
    } else {
      handleLocationError(false, infoWindow, map.getCenter());
    }
}

function displayDeliverer(map, pos) {
    var delivererPos = new google.maps.Marker({
        position: pos,
        icon: {
            path: fontawesome.markers.USER,
            scale: 0.3,
            strokeWeight: 1.0,
            fillColor: 'Red',
            fillOpacity: 1.0
        },
        map: map
    })
}

function addPickup(location) {
   var marker = new google.maps.Marker({
        position: location,
        icon: {
            path: fontawesome.markers.SHOPPING_CART,
            scale: 0.7,
            strokeWeight: 1.0,
            fillColor: 'black',
            fillOpacity: 1.0
        },
        map: map
    }); 
}

function addDropoff(location) {
   var marker = new google.maps.Marker({
        position: location,
        icon: {
            path: fontawesome.markers.MAP_MARKER,
            scale: 0.8,
            strokeWeight: 1.0,
            fillColor: 'black',
            fillOpacity: 1.0
        },
        map: map
    }); 
}

//Adds Markers for Campus Eateries/Cafeterias
function addEateries() {
    var campusFoodKeys = Object.keys(campusFood);
    for (var i = 0; i < campusFoodKeys.length; i++) {
        var contentString = campusFood[campusFoodKeys[i]]["description"];
        var location = campusFood[campusFoodKeys[i]]["location"];
        var infowindow = new google.maps.InfoWindow({
            content: contentString
        });
        var marker = new google.maps.Marker({
            position: location,
            icon: {
                path: fontawesome.markers.SHOPPING_CART,
                scale: 0.6,
                strokeWeight: 1.0,
                fillColor: 'black',
                fillOpacity: 1.0
            },
            map: map
        });
        bindIconListenerToEatery(map, marker, infowindow, campusFoodKeys[i]);
    }
}

//binds infowindow to marker
function bindIconListenerToEatery(map, marker, infowindow, name) {
    marker.addListener('click', function() {
        infowindow.open(map, marker);
        document.getElementById("pick-up-loc").value = name;
    });
}

function bindIconListenerToUser(map, marker, name) {
    marker.addListener('click', function() {
        document.getElementById("drop-off-loc").value = name;
    });
}

function geocode(latLng, address) {
    console.log(latLng)
    var geocoder = new google.maps.Geocoder();
    return new Promise(function(resolve, reject) {
        geocoder.geocode({ 'address': address }, function (results, status) { // called asynchronously
            if (status == google.maps.GeocoderStatus.OK) {
                latLng.lat = results[0].geometry.location.lat();
                latLng.lng = results[0].geometry.location.lng();
                console.log(latLng)
                resolve();
            } else {
                reject(status);
            }
        });
    })
}


//READ JSON FILE INTO THESE TYPES:

var campusFood = {"Blue Room" : {location : {lat: 41.826815, lng: -71.403174}, description : "<b>Blue Room</b><br><i>Mon-Fri:</i> 7:30AM to 9PM<br><i>Sat-Sun:</i> 9AM to 5PM"},
                "Ratty" : {location : {lat: 41.825184, lng: -71.400814}, description : "<b>The Ratty</b><br><i>Mon-Sat:</i> 7:30AM to 7:30PM<br><i>Sun:</i> 10:30AM to 7:30PM"},
                "Ivy Room" : {location : {lat: 41.824884, lng: -71.401144}, description : "<b>Ivy Room</b><br><i>Sun:</i> 7:45PM to 12:00AM<br><i>Mon-Thurs:</i> 11:30AM to 1:45PM, 7:30PM to 12:00AM<br><i>Fri:</i> 11:30 AM to 1:45PM<br><i>Sun:</i> Closed"},
                "Vdub" : {location : {lat: 41.829677, lng: -71.401533}, description: "<b>The V-Dub</b><br><i>Mon-Fri:</i> 7:30AM to 2:00PM, 4:30PM to 7:30PM<br><i>Sat-Sun:</i> Closed"},
                "Andrews" : {location: {lat: 41.830556, lng: -71.402381}, description: "<b>Andrews</b><br><i>Everyday:</i> 10AM to 2AM"},
                "Joes" : {location: {lat: 41.823477, lng: -71.399632}, description: "<b>Joe's</b><br><i>Everyday:</i> 6PM to 2AM"}
            }

//Handles Location Errors
function handleLocationError(browserHasGeolocation, infoWindow, pos) {
    infoWindow.setPosition(pos);
    infoWindow.setContent(browserHasGeolocation ?
                          'Error: The Geolocation service failed.' :
                          'Error: Your browser doesn\'t support geolocation.');
    infoWindow.open(map);
}

function getUserLocation(userPositionArgs, resolve) {
    navigator.geolocation.getCurrentPosition(function(position) {
        userPositionArgs["lat"] = position.coords.latitude;
        userPositionArgs["lng"] = position.coords.longitude;
        resolve()
    })
    
}
