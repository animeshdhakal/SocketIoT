const socket = new WebSocket("ws://" + location.hostname + ":8080/websocket");

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
                sendMsg(socket, 4);
            } else {
                console.log("Auth Failed");
            }
            break;

        case MsgType.WRITE:
            console.log(parsedMsg);
            changeOnOffButton(parsedMsg[0], parsedMsg[1]);
            break;

        default:
            console.log("Unrecognized Type " + msg_type);
            console.log(parsedMsg);
    }
};

socket.onopen = () => {
    sendMsg(socket, 1, "animesh");
};

socket.onmessage = (event) => {
    if (event.data instanceof ArrayBuffer) {
        processMsg(event.data);
    } else {
        console.log("Unrecognized Type");
    }
};

const mainDiv = document.querySelector(".main");
const onoffbuttons = {};

function changeOnOffButton(id, value) {
    onoffbuttons[id].innerText = value == "1" ? "ON" : "OFF";
    onoffbuttons[id].classList =
        value == "1" ? "onoff stateoff" : "onoff stateon";
    onoffbuttons[id].state = value;
}

function OnOffBtnToggle(e) {
    e.target.innerText = e.target.state == "1" ? "ON" : "OFF";
    e.target.classList =
        e.target.state == "1" ? "onoff stateoff" : "onoff stateon";
    sendMsg(socket, 2, e.target.uid, e.target.state);
    e.target.state = 1 - e.target.state;
}

function createOnOffButton(num) {
    for (let i = 0; i < num; i++) {
        let newBtn = document.createElement("button");
        newBtn.innerText = "ON";
        newBtn.state = "0";
        newBtn.uid = i.toLocaleString();
        newBtn.classList.add("onoff");
        newBtn.classList.add("stateoff");
        newBtn.addEventListener("click", OnOffBtnToggle);
        mainDiv.appendChild(newBtn);
        onoffbuttons[i.toLocaleString()] = newBtn;
    }
}

createOnOffButton(2);
