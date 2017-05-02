const MESSAGE_TYPE = {
  CONNECT: 0,
  ADD_ORDER: 1,
  REMOVE_ORDER: 2,
  REQUESTED: 3
};

function getCookie(cname) {
    var name = cname + "=";
    var decodedCookie = decodeURIComponent(document.cookie);
    var ca = decodedCookie.split(';');
    for(var i = 0; i <ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

let conn;
let myId = -1;

// Setup the WebSocket connection for live updating of scores.
const setup_socket = () => {
  if (myId === -1) {
    conn = new WebSocket("ws://localhost:4567/deliverysocket")
    jid = getCookie("JSESSIONID").split(".")[0]
    console.log(jid)
  }
  conn.onerror = err => {
    console.log('Connection error:', err);
  };

  /*conn.onmessage = msg => {
    const data = JSON.parse(msg.data);
    console.log(data)
    /*
    switch () {
      default:
        console.log('Unknown message type!', data.type);
        break;
      case MESSAGE_TYPE.CONNECT:
        break;
      case MESSAGE_TYPE.UPDATE:
        break;
    }*/
  };

}

setup_socket()
