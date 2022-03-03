import React, { useEffect, useRef, useState } from "react";
import axios from "axios";
import AreYouSureModal from "../../components/modals/AreYouSureModal";
import AddDeviceModal from "../../components/modals/AddDeviceModal";
import { Link } from "react-router-dom";
import Alert from "../../components/Alert";
import { DeviceInterface, DeviceRes } from "../../interfaces/IDevice";

export const Devices = () => {
  const [devices, setDevices] = useState<DeviceInterface[]>([]);
  const [deviceDelete, setDeviceDelete] = useState<string>("");
  const [showAddDeviceModal, setShowAddDeviceModal] = useState<boolean>(false);
  const [alert, setAlert] = useState<string>("");
  let alerRef = useRef<HTMLDivElement>(null);

  const fetchBluePrints = () => {
    axios.post<DeviceRes>("/api/device/all").then((res) => {
      setDevices(res.data.devices);
    });
  };

  useEffect(() => {
    fetchBluePrints();
  }, []);

  const deleteDevice = (token: string) => {
    axios.post("/api/device/remove", { token }).then(() => {
      fetchBluePrints();
    });
  };

  const clickToCopy = (e: React.MouseEvent<HTMLElement>) => {
    navigator.clipboard.writeText(e.currentTarget.innerText);
    setAlert("Copied to clipboard");
  };

  return (
    <div className="container flex justify-center mx-auto">
      <div className="flex flex-col">
        <button
          className="my-5 bg--500 w-40 h-10 rounded-md text-black font-light text-center mx-auto bg-green-400 flex justify-center items-center hover:bg-green-300 hover:text-gray-700 group shadow-gray-400 shadow-md"
          onClick={() => setShowAddDeviceModal(true)}
        >
          Add Device
          <svg
            xmlns="http://www.w3.org/2000/svg"
            className="h-6 w-6 ml-1 group-hover:text-gray-500"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M12 6v6m0 0v6m0-6h6m-6 0H6"
            />
          </svg>
        </button>
        <div className="w-full">
          <div className="border-b border-gray-200 shadow">
            <table>
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-2 text-md text-gray-500">S.N</th>
                  <th className="px-6 py-2 text-md text-gray-500">
                    Device Name
                  </th>
                  <th className="px-6 py-2 text-md text-gray-500">
                    Device Status
                  </th>

                  <th className="px-6 py-2 text-md text-gray-500">Last IP</th>

                  <th className="px-6 py-2 text-md text-gray-500">
                    Device Token
                  </th>
                  <th className="px-6 py-2 text-md text-gray-500">
                    BluePrint ID
                  </th>
                  <th className="px-6 py-2 text-md text-gray-500">Delete</th>
                </tr>
              </thead>
              <tbody className="bg-white">
                {devices.map((device, index) => {
                  return (
                    <tr className="whitespace-nowrap" key={device.token}>
                      <td className="px-6 py-4 text-sm text-gray-500">
                        {index + 1}
                      </td>
                      <td className="px-6 py-4">
                        <div className="text-sm text-gray-900">
                          <Link
                            to="/dashboard/device"
                            state={{
                              token: device.token,
                              blueprint_id: device.blueprint_id,
                            }}
                          >
                            {device.name}
                          </Link>
                        </div>
                      </td>
                      <td className="px-6 py-4">
                        <div className="text-sm text-gray-900 flex items-center">
                          {device.online ? "Online" : "Offline"}
                          <div
                            className={`w-3 h-3 ${
                              device.online ? "bg-green-600" : "bg-red-600"
                            } ml-3 rounded-full`}
                          ></div>
                        </div>
                      </td>
                      <td className="px-6 py-4">
                        <div className="text-sm text-gray-900 flex items-center">
                          {device.lastIP || "-"}
                        </div>
                      </td>
                      <td className="px-6 py-4">
                        <div
                          className="text-sm text-gray-500 select-none cursor-pointer"
                          onClick={clickToCopy}
                        >
                          {device.token}
                        </div>
                      </td>
                      <td className="px-6 py-4">
                        <div
                          className="text-sm text-gray-500 select-none cursor-pointer"
                          onClick={clickToCopy}
                        >
                          {device.blueprint_id}
                        </div>
                      </td>
                      <td className="px-6 py-4">
                        <button
                          className="px-4 py-1 text-sm text-white bg-red-400 rounded"
                          onClick={() => setDeviceDelete(device.token)}
                        >
                          Delete
                        </button>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
            {devices.length === 0 && (
              <h1 className="text-center text-gray-500 text-xl w-full my-10">
                Nothing to Show. Please Add a Device
              </h1>
            )}
          </div>
        </div>
      </div>
      <Alert setAlert={setAlert} alert={alert} />
      <AddDeviceModal
        show={showAddDeviceModal}
        onCreate={fetchBluePrints}
        onClose={() => setShowAddDeviceModal(false)}
      />
      <AreYouSureModal
        show={Boolean(deviceDelete)}
        onClose={() => setDeviceDelete("")}
        onYes={() => {
          deleteDevice(deviceDelete);
          setDeviceDelete("");
        }}
      />
    </div>
  );
};
