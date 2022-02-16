import React, { useEffect, useRef, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import {
  MsgType,
  HEADER_SIZE,
  HEARTBEAT_INTERVAL,
} from "../../config/Protocol";
import axios from "axios";
import UniversalWidget from "../../interfaces/UniversalWidget";

const Device = () => {
  const location: any = useLocation();
  const navigate = useNavigate();
  const [device, setDevice] = useState<any>({});
  let socket = useRef<WebSocket | null>(null);

  const create_message = (msg_type: number, ...args: any) => {
    let msg = args.map(String).join("\0");
    let msgBuff = new ArrayBuffer(msg.length + HEADER_SIZE);
    let msgDataView = new DataView(msgBuff);

    msgDataView.setInt16(0, msg.length);
    msgDataView.setInt16(2, msg_type);

    for (var i = 0; i < msg.length; i++) {
      msgDataView.setUint8(i + HEADER_SIZE, msg.charCodeAt(i));
    }
    return msgBuff;
  };

  const onOpen = () => {
    socket.current?.send(create_message(MsgType.AUTH, device.token, "1"));
  };

  const onClose = () => {};

  const onMessage = (e: MessageEvent) => {
    if (e.data instanceof ArrayBuffer) {
      let dataView = new DataView(e.data);
      let msg_length = dataView.getInt16(0);
      let msg_type = dataView.getInt16(2);
      let decodedMessage = String.fromCharCode.apply(
        null,
        Array.from(new Uint8Array(e.data, HEADER_SIZE, msg_length))
      );

      let parsedMsg = decodedMessage.split("\0");

      switch (msg_type) {
        case MsgType.AUTH:
          if (parsedMsg[0] === "1") {
            console.log("Auth Success");
          } else {
            console.log("Auth Failed");
          }
          break;
        case MsgType.WRITE:
          console.log("Write Message", parsedMsg);
          break;
        case MsgType.PING:
          break;
        default:
          console.log("Unknown Message", parsedMsg);
      }
    }
  };

  useEffect(() => {
    if (location.state && location.state.token) {
      setDevice({
        token: location.state.token,
        blueprint_id: location.state.blueprint_id,
      });
    } else {
      navigate("/dashboard/devices");
    }

    let intervalID = setInterval(() => {
      socket.current?.send(create_message(MsgType.PING));
    }, HEARTBEAT_INTERVAL);

    return () => {
      clearInterval(intervalID);
      socket.current?.close();
    };
  }, []);

  useEffect(() => {
    if (device.token) {
      socket.current = new WebSocket(
        window.location.origin.replace("http", "ws") + "/websocket"
      );
      socket.current.binaryType = "arraybuffer";
      socket.current.onclose = onClose;
      socket.current.onopen = onOpen;
      socket.current.onmessage = onMessage;
    }
    if (device.blueprint_id) {
      axios
        .post<{ widgets: UniversalWidget[] }>("/api/blueprint/get", {
          id: device.blueprint_id,
        })
        .then((res) => {});
    }
  }, [device]);

  return <div>Device</div>;
};

export default Device;
