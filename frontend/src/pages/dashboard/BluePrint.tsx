import React, { useEffect, useState } from "react";
import { Location, Navigate, useLocation, useNavigate } from "react-router-dom";

const BluePrint = () => {
  const location: any = useLocation();
  const navigate = useNavigate();
  const [id, setId] = useState<string>("");

  useEffect(() => {
    if (location.state && location.state.id) {
      setId(location.state.id);
    } else {
      navigate("/dashboard/blueprints");
    }
  }, []);

  return <div>BluePrint ID is {id}</div>;
};

export default BluePrint;
