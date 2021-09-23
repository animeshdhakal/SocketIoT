const net = require("net");
const server = net.createServer();

const { processMsg, removeSocket } = require("../core/core");

server.on("connection", (socket) => {
    console.log("Client Connected");

    let values = { token: "" };

    socket.on("data", (data) => processMsg(socket, data, values));

    socket.on("end", () => removeSocket(socket, values));

    socket.on("error", () => {});
});

const port = process.env.PORT || 3030;

server.listen(port, () => {
    console.log(`TCP Server Running on PORT ${port}`);
});
