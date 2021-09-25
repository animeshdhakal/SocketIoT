const { initDB } = require("./db/db");
const cluster = require("cluster");

require("dotenv").config();

initDB();

// const numCPUs = require("os").cpus().length;
// const process = require("process");

// if (cluster.isPrimary) {
//     console.log(`Primary ${process.pid} is running`);

//     // Fork workers.
//     for (let i = 0; i < numCPUs; i++) {
//         cluster.fork();
//     }

//     cluster.on("exit", (worker, code, signal) => {
//         console.log(`worker ${worker.process.pid} died`);
//     });
// } else {
require("./tcp-server/tcp-server");
require("./websocket-server/websocket-server");

//     console.log(`Worker ${process.pid} started`);
// }
