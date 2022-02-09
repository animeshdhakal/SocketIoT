import React from "react";
import Button from "./Button";

const Widget: React.FC<any> = ({ type, ...props }) => {
  return <Button {...props} />;
};

export default Widget;
