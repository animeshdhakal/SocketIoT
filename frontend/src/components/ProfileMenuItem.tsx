import React from "react";

interface Props {
  onClick?: () => void;
  children: React.ReactNode;
  name: string;
}

const ProfileMenuItem: React.FC<Props> = ({ children, onClick, name }) => {
  return (
    <button
      className="hover:bg-blue-200 w-[95%] transition rounded-md my-1 p-2 flex justify-center items-center"
      onClick={onClick}
    >
      <svg
        xmlns="http://www.w3.org/2000/svg"
        className="h-6 w-6"
        fill="none"
        viewBox="0 0 24 24"
        stroke="currentColor"
      >
        {children}
      </svg>
      <span className="m-1.5">{name}</span>
    </button>
  );
};

export default ProfileMenuItem;
