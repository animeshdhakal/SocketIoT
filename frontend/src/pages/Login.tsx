import React from "react";

const Login = () => {
  return (
    <div>
      <label htmlFor="email">Email: </label>
      <input
        className="focus:outline-blue-400 border-black-500"
        type="text"
        placeholder="Email"
        id="email"
      />
    </div>
  );
};

export default Login;
