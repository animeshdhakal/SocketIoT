import MsgType from "./MsgType";

export interface EventCallBackArgs {
    failed?: boolean;
    body?: string[];
}

interface EventCallbackFunction {
    (args: EventCallBackArgs): void;
}

interface Events {
    [key: string]: EventCallbackFunction;
}

class WSClient {
    events: Events;
    ws: WebSocket | null;
    headerSize = 4;

    constructor() {
        this.events = {};
        this.ws = null;
    }

    on(event: string, callback: EventCallbackFunction) {
        this.events[event] = callback;
    }

    off(event: string) {
        delete this.events[event];
    }

    dispatch(event: string, args: EventCallBackArgs) {
        if (this.events[event]) {
            this.events[event](args);
        }
    }

    parseMessage(message: ArrayBuffer) {
        const view = new DataView(message);
        const length = view.getUint16(0);
        const type = view.getUint16(2);
        const rawBody = String.fromCharCode.apply(
            null,
            Array.from(new Uint8Array(message, this.headerSize, length))
        );
        const body = rawBody.split("\0");
        return { body, type };
    }

    createMessage(type: MsgType, ...args: any[]) {
        let msg = args.map(String).join("\0");
        let msgBuff = new ArrayBuffer(msg.length + this.headerSize);
        let msgDataView = new DataView(msgBuff);

        msgDataView.setInt16(0, msg.length);
        msgDataView.setInt16(2, type);

        for (var i = 0; i < msg.length; i++) {
            msgDataView.setUint8(i + this.headerSize, msg.charCodeAt(i));
        }
        return msgBuff;
    }

    handleMessage(type: number, body: string[]) {}

    handleIncomingMessage(event: MessageEvent<WebSocket>) {
        if (event.data instanceof ArrayBuffer) {
            const { type, body } = this.parseMessage(event.data);
            this.handleMessage(type, body);
        }
    }

    call(callback: EventCallbackFunction, msgType: MsgType, ...args: string[]) {
        if (this.ws) {
            this.ws.onmessage = (e) => {
                if (e.data instanceof ArrayBuffer) {
                    const { body, type } = this.parseMessage(e.data);
                    if (msgType === type || type === MsgType.FAILED) {
                        callback({ body, failed: type === MsgType.FAILED });
                    } else {
                        this.handleMessage(type, body);
                    }
                }
                if (this.ws) {
                    this.ws.onmessage = this.handleIncomingMessage;
                }
            };
            this.ws.send(this.createMessage(msgType, ...args));
        }
    }

    connect(url: string) {
        this.ws = new WebSocket(url);
        this.ws.binaryType = "arraybuffer";
        this.ws.onopen = () => this.dispatch("connect", {});
        this.ws.onclose = () => this.dispatch("disconnect", {});
        this.ws.onmessage = this.handleIncomingMessage;
    }
}

let wsClient = new WSClient();

export { wsClient };
