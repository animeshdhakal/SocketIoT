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
    socket.send(String.fromCharCode(
        (msg_type & 0xff), 
        (msg_type >> 8) & 0xff, 
        ((data.length & 0xFFF) & 0xff), 
        ((data.length & 0xFFF) >> 8) & 0xff) + data
    )
}

net.Socket.prototype.send = function(msg){
    this.write(msg)
}

server.on("connection", socket=>{
    socket.setKeepAlive(true, 100)
    socket.setEncoding("binary")


    let token = String()

    socket.on("data", buff=>{
        socket.setKeepAlive(true, 3000);

        while(true){
            if(buff.length < 1) 
                 break;
            const msgType = buff.charCodeAt(0) | buff.charCodeAt(1) << 8
            const len = buff.charCodeAt(2) | buff.charCodeAt(3) << 8
            const msg = buff.substr(4, len)
            

            switch (msgType) {
                case MSGTYPE.MSG_AUTH:
                    console.log("MSG_AUTH")
                    console.log(msg)
                    if (msg === "123"){
                        token = msg
                        if (msg in clients) {
                            clients[msg].push(socket)
                            console.log(clients[token].length)

                        } else {
                            clients[msg] = [socket]
                            console.log(clients[token].length)
                        }
                        sendMsg(socket, MSGTYPE.MSG_AUTH, "y")
                        sendMsg(socket, MSGTYPE.MSG_RW, "nw", 0, 12)
                    }
                    else{
                        sendMsg(socket, MSGTYPE.MSG_AUTH), "n"}
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

    const onClose = ()=>{
        console.log("Client Disconnected")
        if (token in clients) {
            if (clients[token].length == 1) {
                delete clients[token]

            } else {
                let index = clients[token].indexOf(socket)
                if (index > -1) {
                    clients[token].splice(index, 1);
                }
            }
        }
        socket.destroy()
    }
    
    socket.on("close", onClose)
    socket.on("end", onClose)
    socket.on("error", onClose)
    


})

server.listen(3000, "192.168.100.12", ()=>{
    console.log("server is running")
})