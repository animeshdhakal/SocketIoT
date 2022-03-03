import React from "react";
import IButton from "../../interfaces/IButton";
import { PinMode } from "../../interfaces/IWidget";

const Button: React.FC<IButton> = ({
  value,
  offLabel,
  onLabel,
  onValue,
  offValue,
  pin,
  setValue,
}) => {
  return (
    <div
      className="bg-white w-40 h-24 flex justify-center items-center text-xl shadow-gray-600 shadow-sm"
      onClick={() => {
        if (setValue && pin && onValue && offValue)
          setValue(pin, value == onValue ? offValue : onValue);
      }}
    >
      <div className="border border-green-700 w-36 h-20 rounded-l-full rounded-r-full flex justify-center items-center select-none">
        {value === onValue ? offLabel : onLabel}
      </div>
    </div>
  );
};

Button.defaultProps = {
  type: "BUTTON",
  onValue: "1",
  offValue: "0",
  onLabel: "ON",
  offLabel: "OFF",
  pinMode: PinMode.output,
  pin: 0,
  value: "0",
};

export default Button;
