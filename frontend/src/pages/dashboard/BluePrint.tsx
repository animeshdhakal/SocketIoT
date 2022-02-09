import axios from "axios";
import React, { useEffect, useState } from "react";
import Draggable, {
  DraggableData,
  DraggableEvent,
  DraggableEventHandler,
} from "react-draggable";
import { Location, Navigate, useLocation, useNavigate } from "react-router-dom";
import Button from "../../components/widgets/Button";
import Widget from "../../components/widgets/Widget";

interface Widget {
  type: string;
  [key: string]: any;
}

const BluePrint = () => {
  const location: any = useLocation();
  const navigate = useNavigate();
  const [id, setId] = useState<string>("");
  const [widgets, setWidgets] = useState<Widget[]>([]);

  useEffect(() => {
    if (location.state && location.state.id) {
      setId(location.state.id);
    } else {
      navigate("/dashboard/blueprints");
    }
  }, []);

  const onDragStart = (type: string, e: React.DragEvent<HTMLDivElement>) => {
    e.dataTransfer.setData("type", type);
  };

  const onDragOver = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
  };

  const onDrop = (e: React.DragEvent<HTMLDivElement>) => {
    if (e.dataTransfer.getData("type")) {
      setWidgets([...widgets, { type: e.dataTransfer.getData("type") }]);
    }
  };

  const onBuildStop = (
    index: number,
    e: DraggableEvent,
    data: DraggableData
  ) => {
    // set the position of the widget
    const newWidgets = [...widgets];
    newWidgets[index] = {
      ...newWidgets[index],
      x: data.x,
      y: data.y,
    };
    setWidgets(newWidgets);
  };

  const removeWidget = (index: number) => {
    console.log(index);
    const newWidgets = [...widgets];
    newWidgets.splice(index, 1);
    setWidgets(newWidgets);
  };

  return (
    <div className="w-full h-full relative flex">
      <div>
        <div
          className="inline-block"
          draggable="true"
          onDragStart={(e) => onDragStart("button", e)}
        >
          <Button />
        </div>
      </div>
      <div
        className="bg-red-300 w-full h-full relative"
        onDrop={onDrop}
        onDragOver={onDragOver}
      >
        {widgets.map((widget: Widget, index: number) => {
          return (
            <Draggable
              bounds="parent"
              onStop={(e, data) => onBuildStop(index, e, data)}
              key={index}
              position={{ x: widget.x || 0, y: widget.y || 0 }}
              grid={[10, 10]}
            >
              <div className="inline-block group relative">
                <div className="hidden h-7 bg-white bg-opacity-80 w-full absolute top-0 group-hover:flex justify-end items-center transition-all duration-1000 rounded-b-md shadow-gray-400 shadow-sm">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    className="w-5 m-1"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M12 6V4m0 2a2 2 0 100 4m0-4a2 2 0 110 4m-6 8a2 2 0 100-4m0 4a2 2 0 110-4m0 4v2m0-6V4m6 6v10m6-2a2 2 0 100-4m0 4a2 2 0 110-4m0 4v2m0-6V4"
                    />
                  </svg>
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    className="w-5 m-1 text-red-500"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                    onClick={function (e) {
                      removeWidget(index);
                      // e.currentTarget.parentElement?.parentElement?.remove();
                    }}
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                    />
                  </svg>
                </div>
                <Widget {...widget} disabled={true} />
              </div>
            </Draggable>
          );
        })}
      </div>
    </div>
  );
};

export default BluePrint;
