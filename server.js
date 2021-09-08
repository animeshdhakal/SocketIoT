const net = require("net")
const server = net.createServer()

let clients = {}

const MSGTYPE = {
    MSG_AUTH: 1,
    MSG_PING: 2,
    MSG_RW: 3,
    MSG_DISCONNECT: 5
}

const sendMsg = (socket, msg_type, ...args)=>{
    data = args.join('\0')
    socket.write(String.fromCharCode((msg_type & 0xff), (msg_type >> 8) & 0xff, ((data.length & 0xFFF) & 0xff), ((data.length & 0xFFF) >> 8) & 0xff) + data)
}

server.on("connection", socket=>{
    console.log("New client connected")
    socket.setEncoding("binary")
    let token = String()
    socket.on("data", buff=>{
        while(true){
            if(buff.length < 1) 
                break;
            const msgType = buff.charCodeAt(0) | buff.charCodeAt(1) << 8
            const len = buff.charCodeAt(2) | buff.charCodeAt(3) << 8
            const msg = buff.substr(4, len)
            switch (msgType) {
                case MSGTYPE.MSG_AUTH:
                    console.log("MSG_AUTH")
                    if (msg === "123"){
                        token = msg
                        if (msg in clients) {
                            clients[msg].push(socket)
                            auth = msg;

                        } else {
                            clients[msg] = [socket]
                        }
                        sendMsg(socket, MSGTYPE.MSG_AUTH, "OK")
                    }
                    else{
                        sendMsg(socket, MSGTYPE.MSG_AUTH), "FAIL"}
                    break;

                case MSGTYPE.MSG_PING:
                    break

                default:
                    console.log(msgType)
                    console.log("Invalid Message Type");
            }
            buff = buff.slice(4 + len, buff.length)
        }
    })
    socket.on("close", ()=>{
        if (auth in clients) {
            if (clients[auth].length == 1) {
                delete clients[auth]

            } else {
                let index = clients[auth].indexOf(socket)
                if (index > -1) {
                    clients[auth].splice(index, 1);
                }
            }
        }
    })
    socket.on("error", err=>{
        if (auth in clients) {
            if (clients[auth].length == 1) {
                delete clients[auth]

            } else {
                let index = clients[auth].indexOf(socket)
                if (index > -1) {
                    clients[auth].splice(index, 1);
                }
            }
        }
    })

})

server.listen(3000, ()=>{
    console.log("server is running")
})