import axios from "axios";
import React, { useEffect, useState } from "react";
import AreYouSureModal from "../../components/modals/AreYouSureModal";
import CreateBluePrintModal from "../../components/modals/CreateBluePrintModal";
import { Link } from "react-router-dom";
import Alert from "../../components/Alert";
import { BluePrintInterface, BluePrintRes } from "../../interfaces/IBluePrint";

const BluePrints = () => {
  const [bluePrints, setBluePrints] = useState<BluePrintInterface[]>([]);
  const [showModal, setShowModal] = useState<boolean>(false);
  const [bluePrintDelete, setBluePrintDelete] = useState<string>("");
  const [alert, setAlert] = useState<string>("");

  const clickToCopy = (e: React.MouseEvent<HTMLElement>) => {
    navigator.clipboard.writeText(e.currentTarget.innerText);
    setAlert("Copied to clipboard");
  };

  const fetchBluePrints = () => {
    axios.post<BluePrintRes>("/api/blueprint/all").then((res) => {
      setBluePrints(res.data.bluePrints);
    });
  };

  const deleteBluePrint = (id: string) => {
    axios.post<BluePrintRes>("/api/blueprint/delete", { id }).then((res) => {
      fetchBluePrints();
    });
  };

  useEffect(() => {
    fetchBluePrints();
  }, []);

  return (
    <div className="container flex justify-center mx-auto">
      <div className="flex flex-col">
        <button
          className="my-5 bg--500 w-44 h-10 rounded-md text-black font-light text-center mx-auto bg-green-400 flex justify-center items-center hover:bg-green-300 hover:text-gray-700 group shadow-gray-400 shadow-md"
          onClick={() => setShowModal(true)}
        >
          Create BluePrint
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
                    BluePrint Name
                  </th>
                  <th className="px-6 py-2 text-md text-gray-500">
                    BluePrint ID
                  </th>
                  <th className="px-6 py-2 text-md text-gray-500">Delete</th>
                </tr>
              </thead>
              <tbody className="bg-white">
                {bluePrints.map((bluePrint, index) => {
                  return (
                    <tr className="whitespace-nowrap" key={bluePrint.id}>
                      <td className="px-6 py-4 text-sm text-gray-500">
                        {index + 1}
                      </td>
                      <td className="px-6 py-4">
                        <div className="text-sm text-gray-900">
                          <Link
                            to="/dashboard/blueprint"
                            state={{
                              id: bluePrint.id,
                            }}
                          >
                            {bluePrint.name}
                          </Link>
                        </div>
                      </td>

                      <td className="px-6 py-4">
                        <div
                          className="text-sm text-gray-500 select-none cursor-pointer"
                          onClick={clickToCopy}
                        >
                          {bluePrint.id}
                        </div>
                      </td>
                      <td className="px-6 py-4">
                        <button
                          className="px-4 py-1 text-sm text-white bg-red-400 rounded"
                          onClick={() => setBluePrintDelete(bluePrint.id)}
                        >
                          Delete
                        </button>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
            {bluePrints.length === 0 && (
              <h1 className="text-center text-gray-500 text-xl w-full my-10">
                Nothing to Show. Please Create a BluePrint
              </h1>
            )}
          </div>
        </div>
      </div>
      <Alert alert={alert} setAlert={setAlert} />
      <CreateBluePrintModal
        show={showModal}
        onClose={() => setShowModal(false)}
        onCreate={() => fetchBluePrints()}
      />
      <AreYouSureModal
        show={Boolean(bluePrintDelete)}
        onClose={() => {
          setBluePrintDelete("");
        }}
        onYes={() => {
          deleteBluePrint(bluePrintDelete);
          setBluePrintDelete("");
        }}
      />
    </div>
  );
};

export default BluePrints;
