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

function setItemSuggests(arr){
    console.log(arr)
    for(let i = 0; i< arr.length; i++){
        if(i < 3){
        $('#items').append("<option value=" + arr[i] + ">");
    }
    }
    
}

function setItemCorrects(arr){
    $('#items').empty();
    for(let i = 0; i< arr.length; i++){
        if(i < 4){
        $('#items').append("<option value=" + arr[i] + ">");
    }
    } 
}

function setPickupCorrects(arr){
    $('#pickups').empty();
    for(let i = 0; i< arr.length; i++){
        if(i < 4){
        $('#pickups').append("<option value=" + arr[i] + ">");
    }
    } 
}

function setDropoffCorrects(arr){
    $('#dropoffs').empty();
    for(let i = 0; i< arr.length; i++){
        if(i < 4){
        $('#dropoffs').append("<option value=" + arr[i] + ">");
    }
    } 
}

function setPickupSuggests(arr){
    for(let i = 0; i< arr.length; i++){
        if(i < 4){
        $('#pickups').append("<option value=" + arr[i] + ">");
    }
    }

}
function setDropOffSuggests(arr){
    for(let i = 0; i< arr.length; i++){
        if(i < 4){
        $('#dropoffs').append("<option value=" + arr[i] + ">");
    }
    }

}

$(document).ready(() => {
    $.post("/suggest", {}, responseJSON => {
        const responseObject = JSON.parse(responseJSON);
        console.log(responseObject)
        setPickupSuggests(responseObject.pickup);
        setItemSuggests(responseObject.items);
        setDropOffSuggests(responseObject.dropoff);

    });
    
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
    $.post("/is-active", 
      {}, responseJSON => {
        data = JSON.parse(responseJSON)
        console.log(data)
        if (data.isActive === true) {
    $('.form-control').attr("disabled", true);
     $('#submit_order').attr("disabled", true);
     $("#backLink").attr("hidden", false);
}
});
    $(document).keyup(event => {
       $.post("/correct", {}, responseJSON => {
        const responseObject = JSON.parse(responseJSON);
        console.log(responseObject)
        setPickupCorrects(responseObject.pick);
        setItemCorrects(responseObject.items);
        setDropOffCorrects(responseObject.drop);

    });
        
         // $.post("/mapCorrect", postParameters, responseJSON => {

         //     const responseObject = JSON.parse(responseJSON);   
         //     let one = responseObject.results;
         //     let toPassOne = document.getElementById("opt");
         //     let two = responseObject.second;
         //     let toPassTwo = document.getElementById("opt1");
         //     let three = responseObject.third;
         //     let toPassThree = document.getElementById("opt2");
         //     let four = responseObject.fourth;
         //     let toPassFour = document.getElementById("opt3");
             
         //     updateBox(toPassOne, one);
         //     updateBox(toPassTwo, two);
         //     updateBox(toPassThree, three);
         //     updateBox(toPassFour, four);
             
             
             
        });
        
  //  })
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


    if (pickUpLoc == null || dropOffLoc == null || item == null || time == null || price == null) {
        return;
    }
    if (pickUpLoc.length == 0 || dropOffLoc.length == 0 || item.length == 0 || time.length == 0 || price.length == 0) {
        return;
    }

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
    


    Promise.all(promises).then(function() {

        //set cookie
        localStorage.pickupLat = pickUpLocation.lat
        localStorage.pickupLon = pickUpLocation.lng 
        localStorage.dropoffLat = dropOffLocation.lat
        localStorage.dropoffLon = dropOffLocation.lng

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
   
    }); 
}