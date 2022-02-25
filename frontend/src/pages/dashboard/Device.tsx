import React, { useEffect, useRef, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { HEARTBEAT_INTERVAL, MsgType } from "../../config/Protocol";
import axios from "axios";
import UniversalWidget from "../../interfaces/IUniversalWidget";
import Loader from "../../components/Loader";
import Widget from "../../components/widgets/Widget";
import Draggable from "react-draggable";
import { create_message, parse_message } from "../../utils/MsgUtil";

const socket = new WebSocket("ws://localhost:4444/websocket");

const Device = () => {
  const location: any = useLocation();
  const navigate = useNavigate();
  const [device, setDevice] = useState<any>({});
  const [loading, setLoading] = useState<boolean>(true);
  const [widgets, setWidgets] = useState<UniversalWidget[]>([]);

  const setValue = (pin: number, value: string) => {
    const newWidgets = [...widgets];
    newWidgets.forEach((widget) => {
      if (widget.pin === pin) {
        widget.value = value;
      }
    });
    setWidgets(newWidgets);
    socket.send(create_message(MsgType.WRITE, pin, value));
  };

  const syncWidgets = () => {
    socket.send(create_message(MsgType.SYNC));
  };

  const sendPing = () => {
    socket.send(create_message(MsgType.PING));
  };

  const processMsg = (msg: ArrayBuffer) => {
    const { type, body } = parse_message(msg);
    switch (type) {
      case MsgType.AUTH:
        if (body[0] === "1") {
          console.log("Authenticated");
          setLoading(false);
          setTimeout(syncWidgets, 100);
        } else {
          console.log("Authentication failed");
        }
        break;
      case MsgType.WRITE:
        const pin = parseInt(body[0]);
        const value = body[1];
        setWidgets((prevState) => {
          const newWidgets = [...prevState];
          newWidgets.forEach((widget) => {
            if (widget.pin === pin) {
              let val = value;
              widget.value = `${val}`;
            }
          });
          return newWidgets;
        });
        break;
      case MsgType.PING:
        break;
    }
  };

  useEffect(() => {
    if (location.state.token) {
      setDevice({
        token: location.state.token,
        blueprint_id: location.state.blueprint_id,
      });
    } else {
      navigate("/dashboard/devices");
    }

    socket.binaryType = "arraybuffer";
    socket.onmessage = (e) => {
      if (e.data instanceof ArrayBuffer) {
        processMsg(e.data);
      }
    };

    setInterval(sendPing, HEARTBEAT_INTERVAL);
  }, []);

  useEffect(() => {
    if (device.token && socket.readyState === WebSocket.OPEN) {
      socket.send(create_message(MsgType.AUTH, device.token, "1"));
    }
    if (device.blueprint_id) {
      axios
        .post<{ widgets: UniversalWidget[] }>("/api/blueprint/get", {
          id: device.blueprint_id,
        })
        .then((res) => {
          setWidgets(res.data.widgets);
        });
    }
  }, [device]);

  if (loading) {
    return <Loader text="Connecting" />;
  }

  return (
    <div className="w-full h-full">
      {widgets.map((widget: UniversalWidget, index) => {
        return (
          <Draggable
            bounds="parent"
            disabled={true}
            key={index}
            position={{ x: widget.x || 0, y: widget.y || 0 }}
          >
            <div className="inline-block group">
              <Widget {...widget} setValue={setValue} />
            </div>
          </Draggable>
        );
      })}
    </div>
  );
};

export default Device;
