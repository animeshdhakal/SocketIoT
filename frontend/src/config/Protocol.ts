enum MsgType {
  AUTH = 1,
  WRITE = 2,
  READ = 3,
  PING = 4,
  SYNC = 5,

  DEVICE_STATUS = 60,
}

let HEARTBEAT_INTERVAL = 10000;
let HEADER_SIZE = 4;

export { MsgType, HEARTBEAT_INTERVAL, HEADER_SIZE };
