var pickupLocation = {lat: 41.826815, lng: -71.403174}
var requsterLocation = {lat: 41.826920, lng: -71.402731}
var delivererNumber = localStorage.dcell;
var reqNumber = localStorage.rcell;
//pick up location
//var pickup = {lat: 41.830556, lng: -71.402381}
//drop off location
//var dropoff = {lat: 41.826997, lng: -71.400221}
var pickup = {lat: parseFloat(localStorage.pLat), lng: parseFloat(localStorage.pLng)}
var dropoff = {lat: parseFloat(localStorage.dLat), lng: parseFloat(localStorage.dLng)}

function initMap() {
    //TODO
    document.getElementById("pick-up-loc").innerText = localStorage.pickup
    document.getElementById("pick-up-item").innerText = localStorage.item
    document.getElementById("contact-name").innerText = capitalizeFirstLetter(localStorage.name.split(" ")[0]) + " " + capitalizeFirstLetter(localStorage.name.split(" ")[1]);
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
            var pickUpDirections = ""
            for (var i = 0; i < pickUpDirs["directions"].length; i++) {
                pickUpDirections += pickUpDirs["directions"][i].instructions.replace(/<[^>]*>/g, " ");
                pickUpDirections += '\n';
            }
            console.log(pickUpDirections);
            this.sendDirectionsToDeliverer(pickUpDirections);
            document.getElementById("pickup-dirs").innerText = pickUpDirections;
        })
        var dropoffDirs = {}
        new Promise(function(resolve, reject) {
            calcRoute(pickup, dropoff, dropoffDirs, resolve)
        }).then(function() {
            var dropoffDirections = ""
            for (var i = 0; i < dropoffDirs["directions"].length; i++) {
                dropoffDirections += dropoffDirs["directions"][i].instructions.replace(/<[^>]*>/g, " ");
                dropoffDirections += '\n';
            }
            this.sendDirectionsToDeliverer(dropoffDirections);
            console.log(dropoffDirections)
            document.getElementById("pickup-dirs2").innerText = dropoffDirections;
        })
    });

    //calcRoute(userPosition, pickup);
    //calcRoute(pickup, dropoff);
}

function sendTextToDeliverer() {
    var message = document.getElementById("text-message").value;
    const postParameters = {
        number : reqNumber,     //needs to be set
        content : message
    }
    $.post("/sendText", postParameters, responseJSON => {
        });    
}

function sendDirectionsToDeliverer(string){
    console.log("sending");
    console.log(string);
    var message = string;

    const postParameters = {
        number : delivererNumber,
        content : message
    }
    $.post("/sendText", postParameters, responseJSON => {
        });
}

function completeDelivery() {
    var priceEntered = document.getElementById("price")
    if (priceEntered == null || priceEntered.length < 1) {
        alert("Must enter a numeric price");
        return 5;
    }
    $.post("/complete-order", {id: localStorage.id, price: $("#price").val()}, responseJson => {
        window.location.href = '/deliverycompleted';
    });
}

function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

// function sendTextToDeliverer() {
//     var message = document.getElementById("text-message").value;
//     const postParameters = {
//         number : delivererNumber,
//         content : message
//     }
//     $.post("/sendText", postParameters, responseJSON => {
//         });

    

