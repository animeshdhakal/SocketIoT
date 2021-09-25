const { WebSocketServer, WebSocket } = require("ws");

const port = process.env.WEBSOCKET_PORT || 3222;

const wss = new WebSocketServer({ port });

console.log(`WebSocket Server Running on PORT ${port}`);

const { processMsg, removeSocket } = require("../core/core");

WebSocket.prototype.write = function write(message) {
    this.send(message);
};

wss.on("connection", function connection(ws) {
    console.log("Client Connected (WS)");

    ws.token = "";

    ws.on("message", (message) => processMsg(ws, message));

    ws.on("close", () => removeSocket(ws));
});
