import axios from "axios";
import React, { useEffect, useState } from "react";
import Draggable, {
  DraggableCore,
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
  const draggable = React.useRef<DraggableCore>(null);

  useEffect(() => {
    if (location.state && location.state.id) {
      setId(location.state.id);
    } else {
      navigate("/dashboard/blueprints");
    }
  }, []);

  const onDragStart = (e: DraggableEvent, data: DraggableData) => {
    console.log("onDragStart", e, data);
  };

  const onDrag = (e: DraggableEvent, data: DraggableData) => {};

  const onDragEnd = (e: DraggableEvent, data: DraggableData) => {
    console.log("onDragEnd", e, data);
  };

  return (
    <div className="w-full h-full flex relative">
      <DraggableCore
        onStart={onDragStart}
        onStop={onDragEnd}
        onDrag={onDrag}
        ref={draggable}
      >
        <div className="bg-red-300 h-20 w-20 inline-block">Animesh Dhakal</div>
      </DraggableCore>
    </div>
  );
};

export default BluePrint;
