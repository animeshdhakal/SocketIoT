import axios, { AxiosError } from "axios";
import React, { useState } from "react";
import { Modal } from "../../../pages/dashboard/BluePrint";

interface Props {
  show: boolean;
  onClose: () => void;
  onCreate: () => void;
  state: Modal;
}

interface DeviceRes {
  token: string;
}

const WidgetSettingModal: React.FC<Props> = ({
  show,
  onClose,
  onCreate,
  state,
}) => {
  return (
    <div
      className={`modal w-screen h-screen absolute inset-0 bg-black bg-opacity-50 flex justify-center items-center flex-row transition duration-1000 ${
        !show && "hidden"
      }`}
    >
      <div className="container h-auto w-[30rem] bg-white rounded-xl">
        <div className="flex justify-between mt-6 mx-7">
          <h1 className="text-xl">Edit Widget</h1>
          <svg
            xmlns="http://www.w3.org/2000/svg"
            className="w-7 rotate-45 "
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
            onClick={() => onClose()}
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
          <div>
            <label htmlFor="onValue">OnValue:</label>
            <input
              type="text"
              id="onValue"
              placeholder="On Value"
              className={`outline-none bg-none p-1 rounded-md outline-1 m-5 outline-gray-300 focus:ring focus:ring-blue-300
            }`}
            />
          </div>
          <div>
            <label htmlFor="onValue">OffValue:</label>
            <input
              type="text"
              id="onValue"
              placeholder="Off Value"
              className={`outline-none bg-none p-1 rounded-md outline-1 m-5 outline-gray-300 focus:ring focus:ring-blue-300
            }`}
            />
          </div>
          <div>
            <label htmlFor="onValue">OnLabel:</label>
            <input
              type="text"
              id="onValue"
              placeholder="On Label"
              className={`outline-none bg-none p-1 rounded-md outline-1 m-5 outline-gray-300 focus:ring focus:ring-blue-300
            }`}
            />
          </div>
          <div>
            <label htmlFor="onValue">OffLabel:</label>
            <input
              type="text"
              id="onValue"
              placeholder="Off Label"
              className={`outline-none bg-none p-1 rounded-md outline-1 m-5 outline-gray-300 focus:ring focus:ring-blue-300
            }`}
            />
          </div>
        </div>
      </div>
    </div>
  );
};

export default WidgetSettingModal;
