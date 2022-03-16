import axios from "axios";
import React, { FormEvent, useEffect, useState } from "react";
import Alert from "../../components/Alert";
import ErrorAlert from "../../components/ErrorAlert";
import { BluePrintInterface } from "../../interfaces/IBluePrint";
import { DeviceInterface } from "../../interfaces/IDevice";

interface FirmwareInfo {
  build?: string;
  hbeat?: string;
  fv?: string;
  bid?: string;
}

const OTA = () => {
  const [bluePrints, setBluePrints] = useState<BluePrintInterface[]>([]);
  const [devices, setDevices] = useState<DeviceInterface[]>([]);
  const [checkedDevices, setCheckedDevices] = useState<string[]>([]);
  const [binPath, setBinPath] = useState<string>("");
  const [alert, setAlert] = useState<string>("");
  const [firmwareInfo, setFirmwareInfo] = useState<FirmwareInfo>(
    {} as FirmwareInfo
  );
  const [blueprintID, setBluePrintID] = useState<string>("");

  useEffect(() => {
    axios.post<BluePrintInterface[]>("/api/blueprint/all").then((res) => {
      setBluePrints(res.data);
    });
  }, []);

  const findAPattern = (pattern: string, text: string) => {
    let index = text.search(pattern);
    if (index < 0) {
      return;
    }
    const start = index + pattern.length;
    index += pattern.length;

    while (text[index] != "\0") {
      index++;
    }

    return text.substring(start, index);
  };

  const onBluePrintSelect = (blueprint_id: string) => {
    axios
      .post<DeviceInterface[]>("/api/device/all", { blueprint_id })
      .then((res) => {
        setDevices(res.data);
      });
  };

  const onSelectDevice = (checked: boolean, token: string) => {
    if (checked) {
      setCheckedDevices([...checkedDevices, token]);
    } else {
      setCheckedDevices(checkedDevices.filter((t) => t !== token));
    }
  };

  const onFileUpload = (e: any) => {
    if (e.target.files[0]) {
      const fr = new FileReader();
      fr.readAsText(e.target.files[0]);
      fr.onload = () => {
        const result = fr.result as string;
        const build = findAPattern("\0build\0", result);
        if (!build) {
          setAlert("Invalid Firmware");
          e.target.type = "text";
          e.target.type = "file";
          return;
        }
        const hbeat = findAPattern("\0hbeat\0", result);
        const fv = findAPattern("\0fv\0", result);
        const bid = findAPattern("\0bid\0", result);
        setFirmwareInfo({ build, hbeat, fv, bid });
        const formData = new FormData();
        formData.append("file", e.target.files[0]);
        axios.post("/api/upload", formData).then((res) => {
          setBinPath(res.data.message);
        });
      };
    }
  };

  const onUpdate = () => {
    axios.post("/api/ota/begin", {
      devices: checkedDevices,
      firmwarePath: binPath,
      blueprint_id: blueprintID
    });
  };

  const onSelectAll = (checked: boolean) => {
    if (checked) {
      const allCheckBoxes = document.querySelectorAll(".checkbox");
      allCheckBoxes.forEach((cb: any) => {
        cb.checked = true;
      });

      devices.forEach((d) => {
        if (d.online) {
          setCheckedDevices([...checkedDevices, d.token]);
        }
      });
    } else {
      const allCheckBoxes = document.querySelectorAll(".checkbox");
      allCheckBoxes.forEach((cb: any) => {
        cb.checked = false;
      });
      setCheckedDevices([]);
    }
  };

  return (
    <div>
      <div>
        <label>Select BluePrint: </label>
        <select onChange={(e) => onBluePrintSelect(e.target.value)}>
          <option value="">Select BluePrint</option>
          {bluePrints.map((bluePrint, index) => {
            return (
              <option key={index} value={bluePrint.id}>
                {bluePrint.name}
              </option>
            );
          })}
        </select>
      </div>
      <div className="w-full">
        <div className="border-b border-gray-200 shadow">
          <table>
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-2 text-md text-gray-500">
                  Select
                  <input
                    type="checkbox"
                    name="selected"
                    className="m-1"
                    id="selected"
                    onChange={(e) => onSelectAll(e.target.checked)}
                  />
                </th>
                <th className="px-6 py-2 text-md text-gray-500">Device Name</th>
                <th className="px-6 py-2 text-md text-gray-500">
                  Device Status
                </th>
                <th className="px-6 py-2 text-md text-gray-500">Last IP</th>
              </tr>
            </thead>
            <tbody className="bg-white">
              {devices.map((device, index) => {
                if (device.online) {
                  return (
                    <tr className="whitespace-nowrap" key={device.token}>
                      <td className="px-6 py-4 text-sm text-gray-500">
                        <input
                          type="checkbox"
                          name="selected"
                          id="selected"
                          className="checkbox"
                          onChange={(e) =>
                            onSelectDevice(e.target.checked, device.token)
                          }
                        />
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
                        <div className="text-sm text-gray-900 flex items-center">
                          {device.lastIP || "-"}
                        </div>
                      </td>
                    </tr>
                  );
                }
              })}
            </tbody>
          </table>
        </div>
      </div>
      <div>
        <div>
          {!firmwareInfo.build && (
            <div className="mb-3 w-96">
              <label
                htmlFor="formFile"
                className="form-label inline-block mb-2 text-gray-700"
              >
                Upload Firmware
              </label>
              <input
                className="form-control block w-full px-3 py-1.5 text-base font-normal text-gray-700 bg-white bg-clip-padding border border-solid border-gray-300 rounded transition ease-in-out m-0 focus:text-gray-700 focus:bg-white focus:border-blue-600 focus:outline-none"
                type="file"
                id="firmwarefile"
                // accept=".bin"
                onChange={onFileUpload}
              />
            </div>
          )}
          {firmwareInfo.build && (
            <div
              className="text-xl m-1 bg-red-200 w-80"
              onClick={() => {
                setFirmwareInfo({} as FirmwareInfo);
                setBinPath("");
              }}
            >
              <h1>Build: {firmwareInfo.build}</h1>
              <h1>HeartBeat: {firmwareInfo.hbeat}</h1>
              <h1>Firmware Version: {firmwareInfo.fv}</h1>
              <h1>BluePrint ID: {firmwareInfo.bid}</h1>
            </div>
          )}
        </div>

        {binPath && checkedDevices.length > 0 && (
          <button className="bg-green-400 p-2 rounded-md" onClick={onUpdate}>
            Update
          </button>
        )}
      </div>
      <ErrorAlert alert={alert} setAlert={setAlert} />
    </div>
  );
};

export default OTA;
