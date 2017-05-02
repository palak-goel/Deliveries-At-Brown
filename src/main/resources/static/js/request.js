
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
        promises.push(geocode(pickUpLocation, pickUpLoc));
    }

    var dropOffLocation = {
        lat: null,
        lng: null
    };
    if (dropOffLoc == "Current Location") {
        dropOffLocation = userLatLonLocation;
    } else {
        promises.push(geocode(dropOffLocation, dropOffLoc));
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

        //set cookie
        localStorage.pickupLat = pickUpLocation.lat
        localStorage.pickupLon = pickUpLocation.lng 
        localStorage.dropoffLat = dropOffLocation.lat
        localStorage.dropoffLon = dropOffLocation.lng
        localStorage.pickup = pickUpLoc
        localStorage.dropoff = dropOffLoc
        localStorage.item = item
        localStorage.time = time
        localStorage.price = price
        console.log(document.cookie)
        $.post("/submit-request", 
            {pickupLat: pickUpLocation.lat, 
                pickupLon: pickUpLocation.lng, 
                dropoffLat: dropOffLocation.lat, 
                dropoffLon: dropOffLocation.lng, 
                pickup: pickUpLoc,
                dropoff: dropOffLoc,
                item: item, time: time, price: price}, responseJSON => {
                console.log(responseJSON);
                window.location.href = 'http://localhost:4567/requesting';
        });
    });
    
}