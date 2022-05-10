import axios, { AxiosError } from "axios";
import React, { FormEvent, useEffect, useState } from "react";

const EnterNewPassword: React.FC<{ token: string }> = ({ token }) => {
  const [cpassword, setCPassword] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [cpasswordError, setCPasswordError] = useState<string>("");
  const [passwordError, setPasswordError] = useState<string>("");
  const [resetError, setResetError] = useState<string>("");
  const [resetSuccess, setResetSuccess] = useState<string>("");
  const [loading, setLoading] = useState<boolean>(false);

  const validateForm = (): boolean => {
    let isValid: boolean = true;

    if (!password) {
      setPasswordError("Password is Required");
      isValid = false;
    } else if (password.length < 5) {
      setPasswordError("Password must be greater than 5 Characters");
      isValid = false;
    }

    if (!cpassword) {
      setCPasswordError("Confirm Password is Required");
      isValid = false;
    } else if (cpassword !== password) {
      setCPasswordError("Confirm Password must be same as Password");
      isValid = false;
    }

    return isValid;
  };

  const onReset = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (validateForm()) {
      try {
        setLoading(true);
        const res = await axios.post("/api/user/reset", { token, password });
        setResetError("");
        setResetSuccess(res.data.message);
      } catch (e) {
        const res = e as AxiosError;
        if (res.response?.data.message) {
          setResetError(res.response.data.message);
          setResetSuccess("");
        }
      }
    }
  };

  return (
    <div className="w-screen h-screen flex justify-center items-center flex-col">
      <form
        className="h-auto w-96 shadow-2x flex flex-col items-center  shadow-gray-300 shadow-xl border-gray-300 border-[0.2px]"
        onSubmit={(e) => onReset(e)}
      >
        <h1 className="text-center my-5 text-xl">Reset Password</h1>

        {resetError && (
          <div className="bg-red-200 w-80 py-2 text-center border-[0.1px] border-red-400 rounded-md text-rose-600">
            {resetError}
          </div>
        )}

        {resetSuccess && (
          <div className="bg-green-200 w-80 py-2 text-center border-[0.1px] border-green-400 rounded-md text-green-600">
            {resetSuccess}
          </div>
        )}

        <div className="flex flex-col justify-center w-80 my-4">
          <label htmlFor="password" className="font-mono text-md">
            Password
          </label>
          <input
            type="password"
            id="password"
            placeholder="Enter Password"
            className={`outline-none bg-none p-1 rounded-md outline-1 outline-gray-300 focus:ring focus:ring-blue-300 ${
              passwordError ? "outline-red-500" : ""
            }`}
            value={password}
            autoComplete="off"
            onChange={(e) => {
              setPassword(e.target.value);
              setPasswordError("");
              setResetError("");
            }}
          />
          {passwordError && (
            <div className="text-[0.8rem] mt-1 px-1 text-red-500">
              {passwordError}
            </div>
          )}
        </div>

        <div className="flex flex-col justify-center w-80 my-4">
          <label htmlFor="email" className="font-mono text-md">
            Confirm Password
          </label>
          <input
            type="text"
            id="cpassword"
            placeholder="Confirm Password"
            className={`outline-none bg-none p-1 rounded-md outline-1 outline-gray-300 focus:ring focus:ring-blue-300 focus:outline-2 ${
              cpasswordError ? "outline-red-500" : ""
            }`}
            value={cpassword}
            autoComplete="off"
            onChange={(e) => {
              setCPassword(e.target.value);
              setCPasswordError("");
              setResetError("");
            }}
          />
          {cpasswordError && (
            <div className="text-[0.8rem] mt-1 px-1 text-red-500">
              {cpasswordError}
            </div>
          )}
        </div>

        <button
          className={`my-10 px-10 py-2 shadow-gray-400 shadow-md bg-green-400 hover:bg-green-300 transition-all hover:text-gray-700 rounded-md ${
            Boolean(cpasswordError || passwordError)
              ? "text-gray-700 bg-green-300"
              : ""
          }`}
          type="submit"
          disabled={Boolean(cpasswordError || passwordError || loading)}
        >
          Reset
        </button>
      </form>
    </div>
  );
};

export default EnterNewPassword;
