import React from "react";
import IUniversalWidget from "../../../interfaces/IUniversalWidget";
import IWidgetSetting, {
  IWidgetSettingWidget,
} from "../../../interfaces/IWidgetSetting";

const ButtonSettingModal: React.FC<IWidgetSetting> = ({
  widget: { index },
  setWidget,
  setWidgets,
  widgets,
}) => {
  const setKeyValue = (key: string, value: any) => {
    const newWidgets = [...widgets];
    newWidgets[index] = {
      ...newWidgets[index],
      [key]: value,
    } as IUniversalWidget;
    setWidgets(newWidgets);
  };

  const setIVal = (e: React.ChangeEvent<HTMLInputElement>) => {
    setKeyValue(e.target.id, e.target.value);
  };

  return (
    <div
      className={`modal w-screen h-screen absolute inset-0 bg-black bg-opacity-50 flex justify-center items-center flex-row transition duration-1000 `}
    >
      <div className="container h-auto w-[30rem] bg-white rounded-xl">
        <div className="flex justify-between mt-6 mx-7">
          <h1 className="text-xl">Create BluePrint</h1>
          <svg
            xmlns="http://www.w3.org/2000/svg"
            className="w-7 rotate-45 "
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
            onClick={() => setWidget({} as IWidgetSettingWidget)}
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M12 4v16m8-8H4"
            />
          </svg>
        </div>
        <div className="content flex flex-col justify-center items-center">
          <div className="flex flex-col justify-center my-4">
            <label htmlFor="onValue" className="font-mono text-md">
              ON Value
            </label>
            <input
              type="text"
              id="onValue"
              value={widgets[index].onValue}
              onChange={setIVal}
              placeholder="On Value"
              autoComplete="off"
              className="outline-none bg-none p-1 rounded-md w-70 outline-1 outline-gray-300 focus:ring focus:ring-blue-300 focus:outline-2"
            />
          </div>
          <div className="flex flex-col justify-center my-4">
            <label htmlFor="offValue" className="font-mono text-md">
              OFF Value
            </label>
            <input
              type="text"
              id="offValue"
              value={widgets[index].offValue}
              onChange={setIVal}
              placeholder="Off Value"
              autoComplete="off"
              className="outline-none bg-none p-1 rounded-md w-70 outline-1 outline-gray-300 focus:ring focus:ring-blue-300 focus:outline-2"
            />
          </div>
          <div className="flex flex-col justify-center my-4">
            <label htmlFor="onLabel" className="font-mono text-md">
              ON Label
            </label>
            <input
              type="text"
              id="onLabel"
              value={widgets[index].onLabel}
              onChange={setIVal}
              placeholder="On Label"
              autoComplete="off"
              className="outline-none bg-none p-1 rounded-md w-70 outline-1 outline-gray-300 focus:ring focus:ring-blue-300 focus:outline-2"
            />
          </div>
          <div className="flex flex-col justify-center my-4">
            <label htmlFor="offLabel" className="font-mono text-md">
              OFF Label
            </label>
            <input
              type="text"
              id="offLabel"
              value={widgets[index].offLabel}
              onChange={setIVal}
              placeholder="Off Label"
              autoComplete="off"
              className="outline-none bg-none p-1 rounded-md w-70 outline-1 outline-gray-300 focus:ring focus:ring-blue-300 focus:outline-2"
            />
          </div>
          <div className="flex flex-col justify-center my-4">
            <label htmlFor="pin" className="font-mono text-md">
              Pin
            </label>
            <input
              type="number"
              id="pin"
              value={widgets[index].pin}
              onChange={setIVal}
              placeholder="Pin"
              autoComplete="off"
              className="outline-none bg-none p-1 rounded-md w-70 outline-1 outline-gray-300 focus:ring focus:ring-blue-300 focus:outline-2"
            />
          </div>
        </div>
      </div>
    </div>
  );
};

export default ButtonSettingModal;
