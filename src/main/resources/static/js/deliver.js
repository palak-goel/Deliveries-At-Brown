


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

var userPosition
$(document).ready(() => {
    navigator.geolocation.getCurrentPosition(function(position) {
      var pos = {
        lat: position.coords.latitude,
        lng: position.coords.longitude
      }
      userPosition = pos;
    });
});

var all_orders = {}

conn.onmessage = msg => {
  console.log(msg.data)
  const data = JSON.parse(msg.data)
  switch(data.type) {
          case MESSAGE_TYPE.CONNECT:
        console.log("HI");
        myId = data.id
        conn.send(JSON.stringify({jid: getJid(), type: MESSAGE_TYPE.CONNECT}))
          conn.send(JSON.stringify({type: MESSAGE_TYPE.GET_ORDERS}))
          console.log("INNER")
        break;
    /*
      case MESSAGE_TYPE.CONNECT:
        console.log("HI");
        myId = data.id
        conn.send(JSON.stringify({jid: jid, type: MESSAGE_TYPE.CONNECT}))
        break;*/
    case MESSAGE_TYPE.ADD_ORDER:
      console.log("Adding order");
      //console.log(data);
      const table = $('table');
      table.find("tr:gt(0)").remove();
      var days = ["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"];
        for(let i = 0; i< data.orders.length; i++){
          let order = data.orders[i];
          all_orders[order.id] = order;
          console.log(order)
          let pickup = data.pickup[i];
          let dropoff = data.dropoff[i];
          let day = new Date(order.dropoffTime * 1000);
          var time = days[day.getDay()] + " " + day.getHours() + ":" + day.getMinutes()
          let price = order.price;
          let items = order.items[0];
          table.append('<tr><td>'+ pickup + '</td><td>' + dropoff + '</td><td>' + time + 
            '</td><td>' + price + '</td><td>'+ items+ 
            '</td><td><button onclick = "takeOrder(\'' + order.id + 
            '\');"> Take Order</button></td></tr>');
        }
      break;
    case MESSAGE_TYPE.DELIVERED:
      localStorage.pLat = data.pLat;
      localStorage.pLng = data.pLng;
      localStorage.dLat = data.dLat;
      localStorage.dLng = data.dLng;
      window.location.href = 'http://localhost:4567/delivered';
      break;
  }
}


function submitPrefencesToServer() {
	let pri = document.getElementbyID("price");
	let dist = document.getElementbyID("distance");
	let tim = document.getElementbyID("time");
	const postParameters = {
		price: pri,
		distance: dist,
		time: time
	};
  return
}

function takeOrder(arg) {
  console.log(userPosition)
  data = {type: MESSAGE_TYPE.REMOVE_ORDER, id: arg, jid: getJid(), dLat: userPosition.lat, dLng: userPosition.lng}
  order = all_orders[arg]
  localStorage.pickupLat = order.pickupL.data.lat
  localStorage.pickupLon = order.pickupL.data.lon
  localStorage.dropoffLat = order.dropoffL.data.lat
  localStorage.dropoffLon = order.dropoffL.data.lon
  localStorage.pickup = order.pickupL.data.name
  localStorage.dropoff = order.dropoffL.data.name
  localStorage.id = arg
  localStorage.item = order.items[0]
  localStorage.ulat = userPosition.lat 
  localStorage.ulng = userPosition.lng
  window.location.href = 'http://localhost:4567/delivering';
  //conn.send(JSON.stringify(data))
}
