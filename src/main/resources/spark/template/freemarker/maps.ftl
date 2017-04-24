<!DOCTYPE html>
<html>
  <head>
    <style>
      #map {
        height: 600px;
        width: 100%;
       }
    </style>
  </head>
  <body>
    <h3>Deliveries At Brown</h3>
    <div id="map"></div>
    <script>

    var map;
    var directionsDisplay;
    var directionsService;

    function initMap() {
    	//Initial Map Centered Around Brown University
        var brown = {lat: 41.826820, lng: -71.402931};
        map = new google.maps.Map(document.getElementById('map'), {
          zoom: 16,
          center: brown
        });

        addEateries(map, eateries, names);
        userLocation(map, 'Blue');
        displayDeliverer(map, {lat: 41.826920, lng: -71.402731})

        

        calcRoute(brown, {lat: 41.830556, lng: -71.402381});

    }

    function calcRoute(start, end) {		
		var request = {
			origin: start,
			destination: end,
			travelMode: 'WALKING'
		};

		var directionsService = new google.maps.DirectionsService();
        var directionsDisplay = new google.maps.DirectionsRenderer();
        directionsDisplay.setMap(map);

		directionsService.route(request, function(result, status) {
			if (status == 'OK') {
				directionsDisplay.setDirections(result);
				//distance of route				
				console.log(result.routes[0].legs[0].distance.text);
				//duration of route
				console.log(result.routes[0].legs[0].duration.text);
			}
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
            userMarkers.push(userPos);
            //calcRoute({lat: 41.830556, lng: -71.402381}, pos);

            map.setCenter(pos);
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

    //Adds Markers for Campus Eateries/Cafeterias
    function addEateries(map, locations, labels) {
    	for (var i = 0; i < locations.length; i++) {
    		var contentString = descriptions[i];
    		var infowindow = new google.maps.InfoWindow({
          		content: contentString
        	});
        	var marker = new google.maps.Marker({
        		position: locations[i],
        		//label: labels[i],
        		icon: {
        			path: fontawesome.markers.CUTLERY,
        			scale: 0.4,
        			strokeWeight: 1.0,
        			fillColor: 'black',
        			fillOpacity: 1.0
        		},
        		map: map
        	});
        	bindIconListener(map, marker, infowindow);
        }
    }

    //binds infowindow to marker
    function bindIconListener(map, marker, infowindow) {
    	marker.addListener('click', function() {
          	infowindow.open(map, marker);
        });
    }


    //READ JSON FILE INTO THESE TYPES:
    var eateries = [
        {lat: 41.826815, lng: -71.403174},
        {lat: 41.825184, lng: -71.400814},
        {lat: 41.829677, lng: -71.401533},
        {lat: 41.830556, lng: -71.402381}
    ]
		
	var names = ['Blue Room', 'Ratty', 'Vdub', 'Andrews'];
	var descriptions = ['Blue Room', 'Ratty', 'Vdub', 'Andrews'];


	//Handles Location Errors
	function handleLocationError(browserHasGeolocation, infoWindow, pos) {
        infoWindow.setPosition(pos);
        infoWindow.setContent(browserHasGeolocation ?
                              'Error: The Geolocation service failed.' :
                              'Error: Your browser doesn\'t support geolocation.');
        infoWindow.open(map);
      }
    </script>
    <script async defer
    src="https://maps.googleapis.com/maps/api/js?key=AIzaSyClZcJ2MUKJqouuGJRDIGqM1r9om9057sc&callback=initMap">
    </script>
    <script type="text/javascript" src = "/js/font-awesome.js"></script>
  </body>
</html>

