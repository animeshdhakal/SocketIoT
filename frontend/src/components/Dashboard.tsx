import React, { useContext, useState } from "react";
import { Outlet } from "react-router-dom";
import { UserContext } from "../App";
import { UserInterface } from "../interfaces";
import MenuLink from "./MenuLink";
import ProfileMenuItem from "./ProfileMenuItem";

const Dashboard: React.FC = () => {
  const [isUserMenuActive, setIsUserMenuActive] = useState<boolean>(false);
  const { user, setUser } = useContext(UserContext);

  return (
    <div
      className="flex"
      onClick={() => {
        if (isUserMenuActive) {
          setIsUserMenuActive(false);
        }
      }}
    >
      <div className="bg-sidebar w-80 h-screen flex flex-col justify-between">
        <div className="menu">
          <h1 className="text-white text-4xl text-center m-4">SocketIoT</h1>
          <MenuLink name="Home" to="/dashboard/home">
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth="2"
              d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"
            />
          </MenuLink>
          <MenuLink name="BluePrints" to="/dashboard/blueprints">
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7"
            />
          </MenuLink>

          <MenuLink name="Devices" to="/dashboard/devices">
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2-2H7a2 2 0 00-2 2v10a2 2 0 002 2zM9 9h6v6H9V9z"
            />
          </MenuLink>
        </div>
        <div className="footer bg-sidebarMenuHover">
          <MenuLink name="Settings" to="/dashboard/settings">
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"
            />
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"
            />
          </MenuLink>
        </div>
      </div>
      <div className="w-full h-screen overflow-y-auto">
        <div className="header w-full h-14 bg-gray-300 flex justify-between items-center relative">
          <div>
            <h1 className="text-gray-700 m-5">Dashboard</h1>
          </div>

          <div className="group">
            <div
              className="m-4 bg-blue-400 w-10 h-10 rounded-md text-white text-xl flex justify-center items-center cursor-pointer hover:bg-blue-300 transition-all duration-200 select-none"
              onClick={(e) => {
                e.stopPropagation();
                setIsUserMenuActive(!isUserMenuActive);
              }}
            >
              {user.email.charAt(0).toUpperCase()}
            </div>

            <div
              className={`right-0 w-32 border-[0.1px] rounded-md shadow-lg shadow-gray-400 border-gray-300 mr-2 flex-col items-center transition-all duration-200 ${
                isUserMenuActive ? "flex absolute" : "hidden"
              }`}
              onClick={(e) => e.stopPropagation()}
            >
              <ProfileMenuItem
                name="LogOut"
                onClick={() => {
                  localStorage.removeItem("user");
                  setUser({} as UserInterface);
                }}
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"
                />
              </ProfileMenuItem>
            </div>
          </div>
        </div>
        <div className="m-2">
          <Outlet />
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
