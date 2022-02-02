import React from "react";
import { Link, useLocation } from "react-router-dom";

interface Props {
  children?: React.ReactNode;
  name: string;
  to: string;
}

const MenuLink: React.FC<Props> = ({ name, to, children }) => {
  const location = useLocation();

  return (
    <Link
      className={`link flex text-white items-center p-1 text-center group hover:text-gray-400 hover:bg-sidebarMenuHover ${
        location.pathname === to ? "bg-sidebarMenuHover text-gray-400" : ""
      }`}
      to={to}
    >
      <svg
        xmlns="http://www.w3.org/2000/svg"
        className="w-6 m-3 ml-12 cursor-pointer "
        fill="none"
        viewBox="0 0 24 24"
        stroke="currentColor"
      >
        {children}
      </svg>
      {name}
    </Link>
  );
};

export default MenuLink;
