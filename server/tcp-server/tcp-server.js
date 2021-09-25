const net = require("net");
const server = net.createServer();

const { processMsg, removeSocket } = require("../core/core");

server.on("connection", (socket) => {
    console.log("Client Connected (TCP)");

    socket.token = "";

    socket.on("data", (data) => processMsg(socket, data));

    socket.on("end", () => removeSocket(socket));

    socket.on("error", () => {});
});

const port = process.env.TCP_PORT || 3030;

server.listen(port, "0.0.0.0", () => {
    console.log(`TCP Server Running on PORT ${port}`);
});
