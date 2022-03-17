import axios, { AxiosError } from "axios";
import React, { useState } from "react";

interface Props {
  show: boolean;
  id: number;
  onClose: () => void;
  onCreate: () => void;
}

interface DeviceRes {
  token: string;
}

const AddDeviceModal: React.FC<Props> = ({ show, onClose, onCreate, id }) => {
  const [deviceName, setDeviceName] = useState<string>("");
  const [deviceNameError, setDeviceNameError] = useState<string>("");
  const [bluePrintID, setBluePrintID] = useState<string>("");
  const [bluePrintIDError, setBluePrintIDError] = useState<string>("");
  const [token, setToken] = useState<string>("");

  const onModalClose = () => {
    setDeviceNameError("");
    setDeviceName("");
    setBluePrintIDError("");
    setBluePrintID("");
    setToken("");
    onCreate();
    onClose();
  };

  const handleSubmit = () => {
    let validated = true;

    if (!deviceName) {
      setDeviceNameError("Device Name is required");
      validated = false;
    } else if (deviceName.length < 8) {
      setDeviceNameError("Device Name must be at least 8 characters");
      validated = false;
    }

    if (!bluePrintID) {
      setBluePrintIDError("BluePrint ID is required");
      validated = false;
    } else if (bluePrintID.length < 4) {
      setBluePrintIDError("BluePrint ID must be at least 4 characters");
      validated = false;
    }

    if (validated) {
      axios
        .post<DeviceRes>("/api/device/add", {
          name: deviceName,
          blueprint_id: bluePrintID,
          id,
        })
        .then((res) => {
          setToken(res.data.token);
        })
        .catch((e) => {
          let err: AxiosError = e as AxiosError;
          if (err.response) {
            let message: string = err.response.data.message;
            if (message.toLowerCase().includes("blueprint")) {
              setBluePrintIDError("BluePrint ID is invalid");
            } else if (message.toLowerCase().includes("device")) {
              setDeviceNameError("Device Name should be unique");
            } else {
              setDeviceNameError(message);
            }
          }
        });
    }
  };

  return (
    <div
      className={`modal w-screen h-screen absolute inset-0 bg-black bg-opacity-50 flex justify-center items-center flex-row transition duration-1000 ${
        !show && "hidden"
      }`}
    >
      <div className="container h-auto w-[30rem] bg-white rounded-xl">
        <div className="flex justify-between mt-6 mx-7">
          <h1 className="text-xl">Add Device</h1>
          <svg
            xmlns="http://www.w3.org/2000/svg"
            className="w-7 rotate-45 "
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
            onClick={() => onModalClose()}
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
          {!token ? (
            <>
              <div className="flex flex-col justify-center my-4">
                <label htmlFor="email" className="font-mono text-md">
                  Device Name
                </label>
                <input
                  type="text"
                  id="email"
                  placeholder="Enter Device Name"
                  autoComplete="off"
                  className={`outline-none bg-none p-1 rounded-md w-70 outline-1 outline-gray-300 focus:ring focus:ring-blue-300 focus:outline-2 ${
                    deviceNameError ? "outline-red-500" : ""
                  }`}
                  value={deviceName}
                  onChange={(e) => {
                    setDeviceName(e.target.value);
                    setDeviceNameError("");
                  }}
                />
                {deviceNameError && (
                  <div className="text-[0.8rem] mt-1 px-1 text-red-500">
                    {deviceNameError}
                  </div>
                )}
              </div>
              <div className="flex flex-col justify-center my-4">
                <label htmlFor="email" className="font-mono text-md">
                  BluePrint ID
                </label>
                <input
                  type="text"
                  id="email"
                  placeholder="Enter BluePrint ID"
                  autoComplete="off"
                  className={`outline-none bg-none p-1 rounded-md w-70 outline-1 outline-gray-300 focus:ring focus:ring-blue-300 focus:outline-2 ${
                    bluePrintIDError ? "outline-red-500" : ""
                  }`}
                  value={bluePrintID}
                  onChange={(e) => {
                    setBluePrintID(e.target.value);
                    setBluePrintIDError("");
                  }}
                />
                {bluePrintIDError && (
                  <div className="text-[0.8rem] mt-1 px-1 text-red-500">
                    {bluePrintIDError}
                  </div>
                )}
              </div>
              <div className="m-5">
                <button
                  className="bg-green-500 p-2 px-4 rounded-md"
                  disabled={Boolean(deviceNameError || bluePrintIDError)}
                  onClick={handleSubmit}
                >
                  Create
                </button>
              </div>
            </>
          ) : (
            <div className="p-10">
              <h1 className="font-lg">Device Token is : {token}</h1>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default AddDeviceModal;
