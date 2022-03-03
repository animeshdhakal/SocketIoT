import React, { useEffect, useRef } from "react";

interface Props {
  alert: string;
  setAlert: React.Dispatch<React.SetStateAction<string>>;
}

const ErrorAlert: React.FC<Props> = ({ alert, setAlert }) => {
  const alertRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (alert) {
      if (alertRef.current?.style) {
        alertRef.current.style.transform = "translateY(45px)";
        let ftimeout: NodeJS.Timeout, stimeout: NodeJS.Timeout;
        ftimeout = setTimeout(() => {
          if (alertRef.current?.style) {
            alertRef.current.style.transform = "translateY(-45px)";
          }
          stimeout = setTimeout(() => {
            setAlert("");
          }, 500);
        }, 1000);

        return () => {
          clearTimeout(ftimeout);
          clearTimeout(stimeout);
        };
      }
    }
  }, [alert]);

  return (
    <div
      className={`p-2 bg-indigo-800 items-center text-indigo-100 leading-none lg:rounded-full absolute transition-all duration-500 ease-in-out -top-0 mx-auto left-[51%] ${
        alert ? "flex" : "hidden"
      }`}
      style={{ transform: "translateY(-45px)" }}
      role="alert"
      ref={alertRef}
    >
      <span className="flex rounded-full bg-red-500 uppercase px-2 py-1 text-md font-bold mr-3">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          className="h-7 w-7 rotate-45"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
          strokeWidth={2}
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            d="M12 6v6m0 0v6m0-6h6m-6 0H6"
          />
        </svg>
      </span>
      <span className="font-semibold mr-2 text-left flex-auto">{alert}</span>
    </div>
  );
};

export default ErrorAlert;
