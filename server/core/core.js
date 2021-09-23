const { Clients, MsgType, HEADER_SIZE } = require("../config/config");
const { createUser, getUser, updateUser } = require("../db/db");

const processMsg = (socket, data, values) => {
    console.log("User Connected");
    while (true) {
        if (data.length < HEADER_SIZE) break;

        const msgLen = data.readUInt16BE(0);
        const msgType = data.readUInt16BE(2);

        let msg = data.slice(HEADER_SIZE, msgLen + HEADER_SIZE).toString();

        switch (msgType) {
            case MsgType.AUTH:
                getUser(msg, (err, user) => {
                    if (user) {
                        values.token = msg;
                        addSocket(socket, values.token);
                        sendMsg(socket, MsgType.AUTH, 1);
                    } else {
                        sendMsg(socket, MsgType.AUTH, 0);
                    }
                });
                break;

            case MsgType.WRITE:
                if (values.token in Clients) {
                    getUser(values.token, (err, user) => {
                        if (user) {
                            let parsedJSON = JSON.parse(user.json);
                            let parsedMsg = parseMsg(msg);

                            if (msg.length > 1) {
                                if (parsedMsg[0] in parsedJSON) {
                                    parsedJSON[parsedMsg[0]] = parsedMsg[1];
                                    updateUser(
                                        values.token,
                                        JSON.stringify(parsedJSON)
                                    );
                                }
                            }
                        }
                    });

                    const forwardingMessage = data.slice(
                        0,
                        HEADER_SIZE + msgLen
                    );
                    Clients[values.token].forEach((s) => {
                        if (socket != s) {
                            s.write(forwardingMessage);
                        }
                    });
                } else {
                    console.log("User not found");
                }
                break;
            default:
                console.log("Invalid");
        }

        data = data.slice(HEADER_SIZE + msgLen, data.length);
    }
};

const removeSocket = (socket, token) => {
    if (token in Clients) {
        if (Clients[token].length == 1) {
            delete Clients[token];
        } else {
            let index = Clients[token].indexOf(socket);
            if (index > -1) {
                Clients[token].splice(index, 1);
            }
        }
    }
};

const sendMsg = (socket, msgType, ...args) => {
    data = args.join("\0");
    const header = Buffer.alloc(4);
    header.writeUInt16BE(data.length, 0);
    header.writeUInt16BE(msgType, 2);
    socket.write(Buffer.concat([header, Buffer.from(data)]));
};

const parseMsg = (data) => {
    return data.split("\0");
};

const addSocket = (socket, token) => {
    if (token in Clients) {
        Clients[token].push(socket);
    } else {
        Clients[token] = [socket];
    }
};

module.exports = { processMsg, removeSocket };
