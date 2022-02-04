import React from "react";

interface Props {
  show: boolean;
  onClose: () => void;
  onYes: () => void;
}

const AreYouSureModal: React.FC<Props> = ({ show, onClose, onYes }) => {
  return (
    <div
      className={`modal w-screen h-screen absolute inset-0 bg-black bg-opacity-50 flex justify-center items-center flex-row transition duration-1000 ${
        !show && "hidden"
      }`}
    >
      <div className="container h-auto w-[30rem] bg-white rounded-xl">
        <div className="flex justify-between mt-6 mx-7">
          <h1 className="text-xl">Are You Sure ?</h1>
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
        <div className="content flex justify-end m-5">
          <button
            className="bg-green-600 p-2 px-6 mx-3 m-1 rounded-xl hover:bg-green-400 select-none text-md text-slate-200 hover:text-gray-600"
            onClick={onYes}
          >
            Yes
          </button>
          <button
            className="bg-red-500 p-2 px-6 mx-3 m-1 rounded-xl hover:bg-red-400 select-none text-md text-slate-200 hover:text-gray-600"
            onClick={onClose}
          >
            No
          </button>
        </div>
      </div>
    </div>
  );
};

export default AreYouSureModal;
