import React, { useEffect } from "react";
import axios from "axios";

interface DeviceInterface {
  name: string;
  token: string;
  blueprint_id: string;
  online: boolean;
}

interface DeviceRes {
  devices: DeviceInterface[];
}

export const Devices = () => {
  const [devices, setDevices] = React.useState<DeviceInterface[]>([]);

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

  return (
    <div className="container flex justify-center mx-auto">
      <div className="flex flex-col">
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
                  <th className="px-6 py-2 text-md text-gray-500">
                    Device Token
                  </th>
                  <th className="px-6 py-2 text-md text-gray-500">
                    BluePrint ID
                  </th>
                  <th className="px-6 py-2 text-md text-gray-500">Edit</th>
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
                          {device.name}
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
                        <div className="text-sm text-gray-500">
                          {device.token}
                        </div>
                      </td>
                      <td className="px-6 py-4">
                        <div className="text-sm text-gray-500">
                          {device.blueprint_id}
                        </div>
                      </td>
                      <td className="px-6 py-4">
                        <button className="px-4 py-1 text-sm text-white bg-blue-400 rounded">
                          Edit
                        </button>
                      </td>
                      <td className="px-6 py-4">
                        <button
                          className="px-4 py-1 text-sm text-white bg-red-400 rounded"
                          onClick={() => deleteDevice(device.token)}
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
              <h1 className="text-center text-gray-500 text-xl w-full mt-10">
                Nothing to Show. Please Add a Device
              </h1>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
