


/*conn.onmessage = msg => {
    const data = JSON.parse(msg.data);

    switch (data.type) {
      default:
        console.log('Unknown message type!', data.type);
        break;
      case MESSAGE_TYPE.UPDATE:
        console.log("updating");
        const table = $('table');
        let pay = data.payload;
        for(let i = 0; i< pay.length; i++){
          let pt = pay[i];
        }
        let pickup = pt.pickup;
        let dropoff = pt.dropoff;
        let time = pt.endTime;
        let price = pt.price;
        let items = pt.items;
        table.append('<tr><td>'+ pickup + '</td><td>' + dropoff + '</td><td>' +
           time + '</td><td>' + price '</td><td>'+ items + '</td><td><button onclick = "takeOrder();"> Take Order</button></td></tr>');
        break;
    }
  };
}*/

function initMap() {

}

var all_orders = {}
var all_funs = []
var ordering_flag = true;
var ord_info = {order: [], pickup: [], dropoff: [], day: [], time: [], price: [], items: [], distance: [], duration: [], directionObject1: [], pickupObj: [], dropoffObj: [], dist1: [], dur1: [], directionObject2: []}

function addOrders(data) {
  for (var i = 0; i < all_funs.length; i++) {
    clearTimeout(all_funs[i])
  }
  const table = $('table');
  table.find("tr:gt(0)").remove();
  var days = ["Sun","Mon","Tue","Wed","Thu","Fri","Sat"];
  var userPosition = {}
  new Promise(function(resolve, reject) {
    getUserLocation(userPosition, resolve)
  }).then(function() {
    for(let i = 0; i< data.orders.length; i++){
        let order = data.orders[i];
        all_orders[order.id] = order;
        console.log(order)
        let pickup = data.pickup[i];
        let dropoff = data.dropoff[i];
        let day = new Date(order.dropoffTime * 1000);
        let ts = day.toLocaleTimeString();
        let time = days[day.getDay()] + " " + ts.split(":")[0] + ":" + ts.split(":")[1] + ts.split(" ")[1]
        let price = order.price;
        let items = order.items[0];
        let distance = 0;
        let duration = 0;
        let directionObject1 = {}
        let pickupObj = {lat: order.pickupL.data.lat, lng: order.pickupL.data.lng}
        let dropoffObj = {lat: order.dropoffL.data.lat, lng: order.dropoffL.data.lng}
        new Promise(function(resolve, reject) {
          getDistanceDuration(userPosition, pickupObj, directionObject1, resolve)
        }).then(function() {
          let dist1 = directionObject1["distance"]
          let dur1 = directionObject1["duration"]
          let directionObject2 = {}
          new Promise(function(resolve, reject) {
            getDistanceDuration(pickupObj, dropoffObj, directionObject2, resolve)
          }).then(function() {
            console.log("THIS IS I" + i)
            console.log(time)
            distance = parseFloat(dist1) + parseFloat(directionObject2["distance"]);
            duration = parseFloat(dur1) + parseFloat(directionObject2["duration"]);
            $.post("/user-rating", {id: order.id}, responseJSON => {
              json_data = JSON.parse(responseJSON);
              usr_rating = json_data.rating
              table.append('<tr><td>'+ pickup + '</td><td>' + usr_rating + '</td><td>' + duration + " min" + '</td><td>' + 
              distance.toFixed(2) + " mi" + "</td><td>" + time + '</td><td>' + "$" + price + '</td><td>'+ items+ 
          '</td><td><button id="takeorder" onclick = "takeOrder(\'' + order.id + 
          '\');"> Take Order</button></td></tr>');
            diff = order.dropoffTime * 1000 - new Date().getTime()
            console.log(diff)
            //all_funs.push(setTimeout(refresh, order.dropoffTime * 1000));
            all_funs.push(setTimeout(refresh, diff));
            $.post("/is-active", 
          {}, responseJSON => {
            data_resp = JSON.parse(responseJSON)
            console.log(data_resp)
            if (data_resp.isActive ===true) {
         $('#takeorder').prop("disabled", true);
          }
          });
            });
          })
        }) 
      }
    })
    
}

conn.onmessage = msg => {
  const data = JSON.parse(msg.data)
  console.log(data)
  switch(data.type) {
      case MESSAGE_TYPE.CONNECT:
        console.log("HI");
        myId = data.id
        conn.send(JSON.stringify({jid: getJid(), type: MESSAGE_TYPE.CONNECT}))
          //conn.send(JSON.stringify({type: MESSAGE_TYPE.GET_ORDERS}))
          if (ordering_flag) {
          sendOrdering();
        } else {
          submitPreferencesToServer();
        }
          console.log("INNER")
        break;
    case MESSAGE_TYPE.ADD_ORDER:
      console.log("Adding order");
        addOrders(data);

      break;
    case MESSAGE_TYPE.DELIVERED:
      localStorage.pLat = data.pLat;
      localStorage.pLng = data.pLng;
      localStorage.dLat = data.dLat;
      localStorage.dLng = data.dLng;
      window.location.href = '/delivered';
      break;
  }
}

function sendOrdering(){
  ordering_flag = true
  let orderBy = $('input[name=order]:checked', '#radio').val();
  console.log(orderBy);
  const postParameters = {
    option: orderBy
  }
  $.post("/submit-Ordering", postParameters, responseJSON => {
  });
  return;
}

function submitPreferencesToServer() {
  ordering_flag = false
	let pri = $("#price").val();
	let dist = $("#distance").val();
	let time = $("#duration").val();
	const postParameters = {
		price: pri,
		distance: dist,
		time: time
	};
  $.post("/submit-Preferences", postParameters, responseJSON => {
  });
  return;
}

function takeOrder(arg) {
  var userPosition = {}
  new Promise(function(resolve, reject) {
    getUserLocation(userPosition, resolve)
  }).then(function() {
    data = {type: MESSAGE_TYPE.REMOVE_ORDER, id: arg, jid: getJid(), dLat: userPosition.lat, dLng: userPosition.lng}
    order = all_orders[arg]
    localStorage.pickupLat = order.pickupL.data.lat
    localStorage.pickupLon = order.pickupL.data.lng
    localStorage.dropoffLat = order.dropoffL.data.lat
    localStorage.dropoffLon = order.dropoffL.data.lng
    localStorage.pickup = order.pickupL.data.name
    localStorage.dropoff = order.dropoffL.data.name
    localStorage.id = arg
    localStorage.item = order.items[0]
    localStorage.ulat = userPosition.lat 
    localStorage.ulng = userPosition.lng
    localStorage.name = order.orderer.data.name;
    window.location.href = '/delivering';    
  })




  
}
  function refresh() {
    console.log("REFRESHING")
    if (ordering_flag) {
          sendOrdering();
        } else {
          submitPreferencesToServer();
        }
  }
