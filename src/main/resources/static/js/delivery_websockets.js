const MESSAGE_TYPE = {
  CONNECT: 0,
  SCORE: 1,
  UPDATE: 2
};

let conn;
let myId = -1;

let score = 0;

// Setup the WebSocket connection for live updating of scores.
const setup_socket = () => {
  if (myId === -1) {
    conn = new WebSocket("ws://localhost:4567/deliverysocket")
  }
  console.log(myId)

  conn.onerror = err => {
    console.log('Connection error:', err);
  };

  conn.onmessage = msg => {
    const data = JSON.parse(msg.data);
    console.log(data)
    switch (data.type) {
      default:
        console.log('Unknown message type!', data.type);
        break;
      case MESSAGE_TYPE.CONNECT:
        // TODO Assign myId
        myId = data.payload.id
        $("#userId").text(myId)
        break;
      case MESSAGE_TYPE.UPDATE:
         score += data.payload.score
          $("#score").text(score);
        // TODO Update the relevant row or add a new row to the scores table
        break;
    }
  };

}

setup_socket()
