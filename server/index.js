const { initDB } = require("./db/db");

initDB();

require("./tcp-server/tcp-server");
require("./websocket-server/websocket-server");
