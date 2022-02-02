import React, { useContext, useEffect } from "react";
import { Navigate } from "react-router-dom";
import { UserContext } from "../App";
import { UserInterface } from "../interfaces";

export const LogOut = () => {
  const { user, setUser } = useContext(UserContext);

  useEffect(() => {
    localStorage.removeItem("user");
    setUser({} as UserInterface);
  }, [setUser]);

  return <h1>LogOut</h1>;
};
