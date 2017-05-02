const MESSAGE_TYPE = {
  UPDATE: 1
};

let conn;
let myId = -1;


const table_up = () => {
 
	conn = new WebSocket("ws://localhost:4567/deliver");

  conn.onerror = err => {
    console.log('Connection error:', err);
  };

  conn.onmessage = msg => {
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
}

function submitPrefencesToServer(){
	let pri = document.getElementbyID("price");
	let dist = document.getElementbyID("distance");
	let tim = document.getElementbyID("time");
	const postParameters{
		price: pri,
		distance: dist,
		time: time;
	}
}

