import React from "react";
import { PinMode } from "../../interfaces/IWidget";
import ILabel from "../../interfaces/ILabel";
const Label: React.FC<ILabel> = ({ title, value }) => {
  return (
    <div className="w-40 bg-white border-l-[3px] border-lime-300 flex flex-col">
      <div className="m-1">{title}</div>
      <div className="w-40 h-14 flex justify-center items-center text-full font-bold">
        {value || "0.0"}
      </div>
    </div>
  );
};

Label.defaultProps = {
  type: "LABEL",
  title: "Label",
  pinMode: PinMode.input,
  pin: 0,
  value: "0.0",
};

export default Label;
