const MsgType = {
    AUTH: 1,
    WRITE: 2,
    READ: 3,
};

const Clients = new Object();

const HEADER_SIZE = 4;

module.exports = { Clients, MsgType, HEADER_SIZE };
