const { WebSocketServer } = require('ws');

const wss = new WebSocketServer({ port: 8080 });

wss.on('connection', function connection(ws) {
    console.log('Client connected');
  ws.on('message', function incoming(message) {
    console.log('received: %s', message);
  })
  ws.send('Hello World');
});