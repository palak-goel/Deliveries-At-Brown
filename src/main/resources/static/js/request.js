conn.onmessage = msg => {
  console.log(msg.data)
  const data = JSON.parse(msg.data)
  switch(data.type) {
    case MESSAGE_TYPE.CONNECT:
        console.log("HI");
        myId = data.id
        conn.send(JSON.stringify({jid: getJid(), type: MESSAGE_TYPE.CONNECT}))
        break;
    case MESSAGE_TYPE.REQUESTED:
      const jid = getJid()
      console.log("REQUESTED");
      break;
  }
}

$(document).ready(() => {
    document.getElementById("submit_order").addEventListener('click', function() {
        sendFormToServer();
    });
    $.post("/pending-orders", 
      {}, responseJSON => {
            data = JSON.parse(responseJSON)
            console.log(responseJSON);
            if (data.stored === "true") {
            $("#pick-up-loc").val(data.pickup)
      $("#drop-off-loc").val(data.dropoff)
    $("#item").val(data.item)
    $("#time").val(data.time)
    $("#price").val(data.price)
} else {

}
      });
});

function initMap() {
    //Initial Map Centered Around Brown University
    var brown = {lat: 41.826820, lng: -71.402931};
    map = new google.maps.Map(document.getElementById('map'), {
      zoom: 16,
      center: brown
    });


    addEateries(map);
    userLocation(map, 'Blue');
    //displayDeliverer(map, {lat: 41.826920, lng: -71.402731})

    //console.log(coord.length);
    //calcRoute("Kabob And Curry", {lat: 41.830556, lng: -71.402381});

}




function sendFormToServer() {
    //iterate through form
    var pickUpLoc = document.getElementById("pick-up-loc").value;
    console.log(pickUpLoc)
    var dropOffLoc = document.getElementById("drop-off-loc").value;
    console.log(dropOffLoc)
    var item = document.getElementById("item").value;
    var time = document.getElementById("time").value;
    var price = document.getElementById("price").value;
    //if set location, get lat lng, otherwise use geocodeAddress()

    const promises = [];

    var pickUpLocation = {
        lat: null,
        lng: null
    };
    if (Object.keys(campusFood).indexOf(pickUpLoc) >= 0) {
        pickUpLocation = campusFood[pickUpLoc]["location"];
    } else {
        promises.push(geocode(pickUpLocation, pickUpLoc + " Providence, RI"));
    }

    var dropOffLocation = {
        lat: null,
        lng: null
    };
    if (dropOffLoc == "Current Location") {
        dropOffLocation = userLatLonLocation;
    } else {
        promises.push(geocode(dropOffLocation, dropOffLoc + " Providence, RI"));
    }
    //item name, price, time

    //iterate through deliverer array, get their lat/lon position
    //time/duration for every deliverer

    //user Location
    console.log("Here")
    Promise.all(promises).then(function() {
        console.log("Pick Up:");
        console.log(pickUpLoc);
        console.log("Drop Off:");
        console.log(dropOffLoc);
        console.log("Item:");
        console.log(item);
        console.log("Time:");
        console.log(time);
        console.log("Price:");
        console.log(price);
        console.log("About to submit!!!!")
        console.log(pickUpLoc)

/*
        var brown = {lat: 41.826820, lng: -71.402931};
        var verifyPickup = {}
        new Promise(function(resolve, reject) {
            calcRoute(brown, pickUpLoc, verifyPickup, resolve)
        }).then(function() {
            var distance = verifyPickup["distance"].replace(",","")
            console.log(distance)
            console.log(parseFloat(distance))
            if (parseFloat(distance) > 10) {
                console.log("HERE")
            }

            var verifyDropoff = {}
            new Promise(function(resolve, reject) {
                calcRoute(brown, dropOffLoc, verifyDropoff, resolve)
            }).then(function() {
                console.log(verifyDropoff)
                var distance = verifyDropoff["distance"].replace(",","")
                console.log(distance)
                console.log(parseFloat(distance))
                if (parseFloat(distance) > 10) {
                    console.log("HERE")
                }*/
            


                //set cookie
                localStorage.pickupLat = pickUpLocation.lat
                localStorage.pickupLon = pickUpLocation.lng 
                localStorage.dropoffLat = dropOffLocation.lat
                localStorage.dropoffLon = dropOffLocation.lng
                /*
                localStorage.pickup = pickUpLoc
                localStorage.dropoff = dropOffLoc
                localStorage.item = item
                localStorage.time = time
                localStorage.price = price
                console.log(document.cookie)
                */
                console.log(pickUpLoc)
                $.post("/submit-request", 
                    {pickupLat: pickUpLocation.lat, 
                        pickupLon: pickUpLocation.lng, 
                        dropoffLat: dropOffLocation.lat, 
                        dropoffLon: dropOffLocation.lng, 
                        pickup: pickUpLoc,
                        dropoff: dropOffLoc,
                        item: item, time: time, price: price, phone: "1112223333", submit: false}, responseJSON => {
                        console.log(responseJSON);
                        window.location.href = '/requesting';
                       
                });
           // })
       // })   
    }); 
}