import axios from "axios";
import React, { useEffect, useState } from "react";
import Draggable, { DraggableData, DraggableEvent } from "react-draggable";
import { useLocation, useNavigate } from "react-router-dom";
import Alert from "../../components/Alert";
import WidgetSettingModal from "../../components/modals/WidgetSettingModal";
import Button from "../../components/widgets/Button";
import Label from "../../components/widgets/Label";
import Widget from "../../components/widgets/Widget";
import UniversalWidget from "../../interfaces/IUniversalWidget";
import { IWidgetSettingWidget } from "../../interfaces/IWidgetSetting";

interface WidgetRes {
  widgets: UniversalWidget[];
}

const BluePrint = () => {
  const location: any = useLocation();
  const navigate = useNavigate();
  const [id, setID] = useState<string>("");
  const [alert, setAlert] = useState<string>("");
  const [widgets, setWidgets] = useState<UniversalWidget[]>([]);
  const [widget, setWidget] = useState<IWidgetSettingWidget>(
    {} as IWidgetSettingWidget
  );

  useEffect(() => {
    if (location.state.id) {
      setID(location.state.id);
      axios
        .post<WidgetRes>("/api/blueprint/get", { id: location.state.id })
        .then((res) => {
          setWidgets(res.data.widgets);
        });
    } else {
      navigate("/dashboard/blueprints");
    }
  }, []);

  const saveBluePrint = () => {
    axios
      .post("/api/widget/add", { blueprint_id: id, widgets })
      .then((e) => {
        setAlert("BluePrint Saved");
      })
      .catch((e) => {
        setAlert("Error Saving Blueprint");
      });
  };

  const removeWidget = (index: number) => {
    const newWidgets = [...widgets];
    newWidgets.splice(index, 1);
    setWidgets(newWidgets);
  };

  const onDragEnd = (e: DraggableEvent, data: DraggableData, index: number) => {
    const newWidgets = [...widgets];
    newWidgets[index].x = data.x;
    newWidgets[index].y = data.y;
    setWidgets(newWidgets);
  };

  return (
    <div className="flex">
      <div className="w-44 h-screen bg-red-300">
        <button
          className="w-40 h-10 bg-green-400 rounded-md hover:bg-green-300 m-1"
          onClick={() => saveBluePrint()}
        >
          Save
        </button>
        <div
          className="m-1"
          onClick={() =>
            setWidgets([...widgets, Object.assign({}, Label.defaultProps)])
          }
        >
          <Label />
        </div>
        <div
          className="m-1"
          onClick={() =>
            setWidgets([...widgets, Object.assign({}, Button.defaultProps)])
          }
        >
          <Button />
        </div>
      </div>
      <div className="w-full h-screen relative">
        {widgets.map((widget: UniversalWidget, index) => {
          return (
            <Draggable
              bounds="parent"
              onStop={(e, d) => onDragEnd(e, d, index)}
              position={{ x: widget.x || 0, y: widget.y || 0 }}
            >
              <div className="inline-block group">
                <div className="hidden absolute w-full h-7 bg-gray-300 group-hover:flex rounded-b-md justify-end items-center">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    className="h-5 m-1"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                    onClick={() => {
                      setWidget({ type: widget.type || "", index });
                    }}
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"
                    />
                  </svg>
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    className="h-5 text-red-500 m-1"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                    onClick={() => removeWidget(index)}
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                    />
                  </svg>
                </div>
                <Widget {...widget} />
              </div>
            </Draggable>
          );
        })}
      </div>
      <Alert setAlert={setAlert} alert={alert} />
      <WidgetSettingModal
        widget={widget}
        setWidget={setWidget}
        widgets={widgets}
        setWidgets={setWidgets}
      />
    </div>
  );
};

export default BluePrint;
