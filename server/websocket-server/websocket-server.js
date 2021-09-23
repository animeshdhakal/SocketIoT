const { WebSocketServer, WebSocket } = require("ws");

const port = 3222;

const wss = new WebSocketServer({ port });

console.log(`WebSocket server started on port ${port}`);

const { processMsg, removeSocket } = require("../core/core");

WebSocket.prototype.write = function write(message) {
    this.send(message);
};

wss.on("connection", function connection(ws) {
    let values = { token: "" };

    ws.on("message", (message) => processMsg(ws, message, values));

    ws.on("close", () => removeSocket(ws, values.token));
});
