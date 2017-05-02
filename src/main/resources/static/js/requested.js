//pick up location
var pickup = {lat: 41.830556, lng: -71.402381}
//drop off location
var dropoff = {lat: 41.826997, lng: -71.400221}
//deliverer location
var delivererLocation = {lat: 41.826920, lng: -71.402731}
//deliverer name
var delivererName = localStorage.name;
//deliverer number
var delivererNumber = localStorage.cell;


function initMap() {
    document.getElementById("deliverer-name").innerText = delivererName;
    document.getElementById("deliverer-num").innerText= delivererNumber;


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
    console.log(calcRoute(delivererLocation, pickup));
    console.log(calcRoute(pickup, dropoff));
    //console.log(coord.length);
    //calcRoute("Kabob And Curry", {lat: 41.830556, lng: -71.402381});

}

function sendTextToDeliverer() {
    function sendTextToDeliverer() {
    var message = document.getElementById("text-message").value;
    const postParameters = {
        number = delivererNumber,
        content = message
    }
    $.post("/sendText", postParameters, responseJSON => {
        });

    
}
}


