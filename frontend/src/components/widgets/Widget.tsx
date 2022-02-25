import React from "react";
import IUniversalWidget from "../../interfaces/IUniversalWidget";
import Button from "./Button";
import Label from "./Label";

const Widget: React.FC<IUniversalWidget> = ({ type, ...props }) => {
  if (type === "LABEL") {
    return <Label {...props} />;
  }
  if (type === "BUTTON") {
    return <Button {...props} />;
  }
  return null;
};

export default Widget;
