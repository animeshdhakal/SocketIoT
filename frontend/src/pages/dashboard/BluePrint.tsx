import axios from "axios";
import React, { useEffect, useRef, useState } from "react";
import Draggable, { DraggableData, DraggableEvent } from "react-draggable";
import { Location, Navigate, useLocation, useNavigate } from "react-router-dom";
import WidgetSettingModal from "../../components/modals/widgets/WigetSettingModal";
import Button from "../../components/widgets/Button";
import Widget from "../../components/widgets/Widget";

interface Widget {
  type: string;
  [key: string]: any;
}

export interface Modal {
  type: string;
  index: number;
  widgets: Widget[];
  setWidgets: React.Dispatch<React.SetStateAction<Widget[]>>;
}

const BluePrint = () => {
  const isMounted = useRef(false);
  const location: any = useLocation();
  const navigate = useNavigate();
  const [id, setId] = useState<string>("");
  const [widgets, setWidgets] = useState<Widget[]>([]);
  const [allWidgets, setAllWidgets] = useState<Widget[]>([{ type: "buttton" }]);
  const [modalState, setModalState] = useState<Modal>({} as Modal);
  let mouseOverDropBox = useRef(false);
  const dropbox = document.querySelector(".dropbox");

  useEffect(() => {
    if (location.state && location.state.id) {
      setId(location.state.id);
    } else {
      navigate("/dashboard/blueprints");
    }

    isMounted.current = true;

    dropbox?.addEventListener("mouseover", () => {
      mouseOverDropBox.current = true;
    });

    dropbox?.addEventListener("mouseout", () => {
      mouseOverDropBox.current = false;
    });

    return () => {
      dropbox?.removeEventListener("mouseover", () => {
        mouseOverDropBox.current = true;
      });

      dropbox?.removeEventListener("mouseout", () => {
        mouseOverDropBox.current = false;
      });

      isMounted.current = false;
    };
  });

  const onDropEnd = (e: DraggableEvent, data: DraggableData, type: string) => {
    if (mouseOverDropBox.current) {
      setAllWidgets(allWidgets);
      setWidgets([...widgets, { type, x: data.x, y: data.y }]);
    }
  };

  const onDragEnd = (e: DraggableEvent, data: DraggableData, index: number) => {
    const rect = data.node.getBoundingClientRect();

    const newWidgets = [...widgets];

    for (let i = 0; i < newWidgets.length; i++) {
      if (i === index || !newWidgets[i].node) {
        continue;
      }

      const newRect = newWidgets[i].node.getBoundingClientRect();

      if (
        rect.left < newRect.right &&
        rect.right > newRect.left &&
        rect.top < newRect.bottom &&
        rect.bottom > newRect.top
      ) {
        // move the current widget to the end x
        data.x = newWidgets[i].x + rect.width + 20;
      }
    }

    newWidgets[index] = {
      ...newWidgets[index],
      x: data.x,
      y: data.y,
      node: data.node,
    };

    setWidgets(newWidgets);
  };

  const removeWidget = (index: number) => {
    const newWidgets = [...widgets];
    newWidgets.splice(index, 1);
    setWidgets(newWidgets);
  };

  return (
    <div className="w-full h-full flex">
      <div className="w-52 h-full bg-red-500 flex justify-center">
        {allWidgets.map((widget, index) => {
          return (
            <Draggable
              onStop={(e, d) => onDropEnd(e, d, widget.type)}
              key={Math.floor(Math.random() * 100000)}
            >
              <div className="inline-block m-1">
                <Widget {...widget} disabled={true} />
              </div>
            </Draggable>
          );
        })}
      </div>
      <div className="w-full h-full relative dropbox">
        {widgets.map((widget: Widget, index) => (
          <Draggable
            bounds="parent"
            key={index}
            position={{ x: widget.x || 0, y: widget.y || 0 }}
            onStop={(e, d) => onDragEnd(e, d, index)}
          >
            <div className="inline-block relative group">
              <div className="hidden h-7 bg-white bg-opacity-80 w-full absolute top-0 group-hover:flex justify-end items-center transition-all duration-1000 rounded-b-md shadow-gray-400 shadow-sm">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  className="w-5 m-1"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                  onClick={() => {
                    setModalState({
                      type: widget.type,
                      index: index,
                      widgets,
                      setWidgets,
                    });
                  }}
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
        ))}
      </div>
      <WidgetSettingModal
        state={modalState}
        show={Boolean(modalState.type)}
        onClose={() => {
          setModalState({} as Modal);
        }}
        onCreate={() => {}}
      />
    </div>
  );
};

export default BluePrint;
