import { HEARTBEAT_INTERVAL, MsgType } from "./config/Protocol";
import { create_message, parse_message } from "./utils/MsgUtil";

class WSClient {
  events: any;
  socket: WebSocket | null;
  token: string;
  onAuthenticated: () => void;

  constructor() {
    this.events = {};
    this.socket = null;
    this.token = "";
    this.onAuthenticated = () => {};
  }

  addEventListener(event: string, action: any) {
    this.events[event] = this.events[event] || [];
    if (!this.events[event].includes(action)) {
      this.events[event].push(action);
    }
  }

  removeEventListener(event: string) {
    this.events[event] = [];
  }

  dispatchEvent(event: any, details: any) {
    this.events[event] = this.events[event] || [];
    this.events[event].forEach((eventHandler: any) => {
      eventHandler(details);
    });
  }

  send(data: ArrayBuffer) {
    this.socket?.send(data);
  }

  connected() {
    return this.socket?.readyState === WebSocket.OPEN;
  }

  sendWrite(deviceID: number, pin: number, value: string) {
    this.send(create_message(MsgType.WRITE, deviceID, pin, value));
  }

  syncAll(deviceID: number) {
    this.send(create_message(MsgType.SYNC, deviceID));
  }

  login() {
    this.send(create_message(MsgType.AUTH, this.token));
  }

  init({ uri, token }: { uri: string; token: string }) {
    this.token = token;
    this.socket = new WebSocket(uri);
    this.socket.binaryType = "arraybuffer";
    this.socket.onmessage = (e) => {
      if (e.data instanceof ArrayBuffer) {
        this.handleMessage(e.data);
      }
    };
    this.socket.onopen = () => {
      this.login();
    };
  }

  handleMessage(message: ArrayBuffer) {
    const { type, body } = parse_message(message);
    switch (type) {
      case MsgType.AUTH:
        if (body[0] == "1") {
          console.log("Authenticated");
          this.onAuthenticated();
          setTimeout(() => {
            this.send(create_message(MsgType.PING));
          }, HEARTBEAT_INTERVAL);
        } else {
          console.log("Authentication Failed");
        }
        break;

      case MsgType.WRITE:
        this.dispatchEvent("write", {
          deviceID: body[0],
          pin: body[1],
          value: body[2],
        });
        break;

      case MsgType.DEVICE_STATUS:
        let status = body[1] == "1" ? true : false;
        this.dispatchEvent("status", { deviceID: body[0], status });
        break;

      case MsgType.PING:
        break;
    }
  }
}

const wsClient = new WSClient();

export { wsClient };
