const socket = new WebSocket("ws://localhost:3222");

socket.binaryType = "arraybuffer";

const HEADER_SIZE = 4;

const MsgType = {
    AUTH: 1,
    WRITE: 2,
    READ: 3,
};

const sendMsg = (socket, msg_type, ...args) => {
    msg = args.map(String).join("\0");
    var msgBuff = new ArrayBuffer(msg.length + HEADER_SIZE);
    var msgDataView = new DataView(msgBuff);

    msgDataView.setInt16(0, msg.length);
    msgDataView.setInt16(2, msg_type);

    for (var i = 0; i < msg.length; i++) {
        msgDataView.setUint8(i + HEADER_SIZE, msg.charCodeAt(i));
    }
    socket.send(msgBuff);
};

const parseMsg = (msg) => {
    return msg.split("\0");
};

const processMsg = (msg) => {
    var msgDataView = new DataView(msg);
    var msg_length = msgDataView.getInt16(0);
    var msg_type = msgDataView.getInt16(2);
    var decodedMsg = String.fromCharCode.apply(
        null,
        new Uint8Array(msg.slice(HEADER_SIZE))
    );

    var parsedMsg = parseMsg(decodedMsg);

    switch (msg_type) {
        case MsgType.AUTH:
            if (parseInt(decodedMsg) == 1) {
                console.log("Auth Success");
            } else {
                console.log("Auth Failed");
            }
            break;
        case MsgType.WRITE:
            console.log("Write Success");
            console.log(parsedMsg);
            break;
        default:
            console.log("Unrecognized Type " + msg_type);
    }
};

socket.onopen = () => {
    sendMsg(socket, 1, "ktv5oyfgnajn8hmouib");
};

socket.onmessage = (event) => {
    if (event.data instanceof ArrayBuffer) {
        processMsg(event.data);
    } else {
        console.log("Unrecognized Type");
    }
};

const onBtnClick = (e) => {
    const btn = document.getElementById(e.target.id);
    btn.className = btn.className === "on" ? "off" : "on";
    btn.innerHTML = btn.className === "on" ? "ON" : "OFF";
    sendMsg(socket, MsgType.WRITE, btn.id, btn.className === "on" ? 1 : 0);
};

document.querySelector("button").addEventListener("click", onBtnClick);
