import React, { useEffect, useRef, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";
import UniversalWidget from "../../interfaces/IUniversalWidget";
import Loader from "../../components/Loader";
import Widget from "../../components/widgets/Widget";
import Draggable from "react-draggable";
import { wsClient } from "../../WSClient";

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
    console.log(device.id);
    wsClient.sendWrite(device.id, pin, value);
  };

  const syncWidget = ({ pin: spin, value }: any) => {
    const pin = parseInt(spin);
    setWidgets((prevState) => {
      let newWidgets = [...prevState];
      newWidgets.forEach((widget) => {
        if (widget.pin === pin) {
          let val = value;
          widget.value = `${val}`;
        }
      });
      return newWidgets;
    });
  };

  useEffect(() => {
    if (location.state.token) {
      setDevice({
        token: location.state.token,
        blueprint_id: location.state.blueprint_id,
        id: location.state.id,
      });
    } else {
      navigate("/dashboard/devices");
    }

    return () => {
      wsClient.removeEventListener("write");
      wsClient.removeEventListener("sync");
      wsClient.onAuthenticated = () => {};
    };
  }, []);

  const wsOnOpen = () => {
    setTimeout(() => wsClient.syncAll(device.id), 100);
    wsClient.addEventListener("write", ({ deviceID, pin, value }: any) => {
      if (device.id == deviceID) {
        syncWidget({ pin, value });
      }
      if (loading) {
        setLoading(false);
      }
    });
    wsClient.addEventListener("sync", ({}) => {
      setLoading(false);
    });
  };

  useEffect(() => {
    if (device.id) {
      if (wsClient.connected()) {
        wsOnOpen();
      } else {
        wsClient.onAuthenticated = wsOnOpen;
      }
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
