import React from "react";

export interface UserInterface {
  email: string;
}

export interface AuthContextInterface {
  user: UserInterface;
  setUser: React.Dispatch<React.SetStateAction<UserInterface>>;
}
