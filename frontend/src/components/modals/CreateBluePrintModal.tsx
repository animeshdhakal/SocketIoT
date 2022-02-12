import axios, { AxiosError } from "axios";
import React, { useState } from "react";

interface Props {
  show: boolean;
  onClose: () => void;
  onCreate: () => void;
}

interface BluePrintRes {
  id: string;
}

const CreateBluePrintModal: React.FC<Props> = ({ show, onClose, onCreate }) => {
  const [bluePrintName, setBluePrintName] = useState<string>("");
  const [bluePrintNameError, setBluePrintNameError] = useState<string>("");
  const [id, setId] = useState<string>("");

  const onModalClose = () => {
    setBluePrintNameError("");
    setBluePrintName("");
    setId("");
    onCreate();
    onClose();
  };

  const handleSubmit = () => {
    let validated = true;

    if (!bluePrintName) {
      setBluePrintNameError("BluePrint Name is required");
      validated = false;
    } else if (bluePrintName.length < 8) {
      setBluePrintNameError("BluePrint Name must be at least 8 characters");
      validated = false;
    }

    if (validated) {
      axios
        .post<BluePrintRes>("/api/blueprint/create", { name: bluePrintName })
        .then((res) => {
          setId(res.data.id);
        })
        .catch((e) => {
          let err: AxiosError = e as AxiosError;
          setBluePrintNameError(err.response?.data.message);
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
          <h1 className="text-xl">Create BluePrint</h1>
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
          {!id ? (
            <>
              <div className="flex flex-col justify-center my-4">
                <label htmlFor="email" className="font-mono text-md">
                  Name
                </label>
                <input
                  type="text"
                  id="email"
                  placeholder="Enter BluePrint Name"
                  autoComplete="off"
                  className={`outline-none bg-none p-1 rounded-md w-70 outline-1 outline-gray-300 focus:ring focus:ring-blue-300 focus:outline-2 ${
                    bluePrintNameError ? "outline-red-500" : ""
                  }`}
                  value={bluePrintName}
                  onChange={(e) => {
                    setBluePrintName(e.target.value);
                    setBluePrintNameError("");
                  }}
                />
                {bluePrintNameError && (
                  <div className="text-[0.8rem] mt-1 px-1 text-red-500">
                    {bluePrintNameError}
                  </div>
                )}
              </div>
              <div className="m-5">
                <button
                  className="bg-green-500 p-2 px-4 rounded-md"
                  disabled={Boolean(bluePrintNameError)}
                  onClick={handleSubmit}
                >
                  Create
                </button>
              </div>
            </>
          ) : (
            <div className="p-10">
              <h1 className="font-lg">BluePrint ID is : {id}</h1>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default CreateBluePrintModal;
