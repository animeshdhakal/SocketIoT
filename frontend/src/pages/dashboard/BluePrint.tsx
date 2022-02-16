import axios from "axios";
import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import UniversalWidget from "../../interfaces/UniversalWidget";

interface WidgetRes {
  widgets: UniversalWidget[];
}

const BluePrint = () => {
  const location: any = useLocation();
  const navigate = useNavigate();
  const [id, setID] = useState<string>("");
  const [widgets, setWidgets] = useState<any[]>([]);

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

  return <div>{id}</div>;
};

export default BluePrint;
