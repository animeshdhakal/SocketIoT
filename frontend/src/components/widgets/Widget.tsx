import React from "react";
import IUniversalWidget from "../../interfaces/IUniversalWidget";
import Button from "./Button";

const Widget: React.FC<IUniversalWidget> = ({ ...props }) => {
  return <Button {...props} />;
};

export default Widget;
