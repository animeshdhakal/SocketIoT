import React from "react";
import { DraggableProvided } from "react-beautiful-dnd";

interface Props {
  onLabel?: string;
  offLabel?: string;
  onValue?: string;
  offValue?: string;
  initialState?: string;
  onClick?: (value: string | undefined) => void;
}

const Button: React.FC<Props> = ({
  onClick,
  initialState,
  onLabel,
  offLabel,
  onValue,
  offValue,
}) => {
  const [state, setState] = React.useState<string | undefined>(
    initialState || "0"
  );

  return (
    <div
      className="h-20 bg-blue-400 px-1 flex justify-center items-center group select-none"
      onClick={() => {
        setState(state === onValue ? offValue : onValue);
        onClick && onClick(state === onValue ? offValue : onValue);
      }}
    >
      <div className="border-green-500 border-2 p-4 w-28 rounded-r-full rounded-l-full text-white text-xl group-hover:text-gray-300 transition-all text-center">
        {state === onValue ? offLabel : onLabel}
      </div>
    </div>
  );
};

Button.defaultProps = {
  onLabel: "ON",
  offLabel: "OFF",
  onValue: "1",
  offValue: "0",
  initialState: "0",
};

export default Button;
