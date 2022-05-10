import axios, { AxiosError } from "axios";
import React, { FormEvent, useState } from "react";

const SendResetEmail = () => {
  const [email, setEmail] = useState<string>("");
  const [emailError, setEmailError] = useState<string>("");
  const [resetError, setResetError] = useState<string>("");
  const [resetSuccess, setResetSuccess] = useState<string>("");
  const [loading, setLoading] = useState<boolean>(false);

  const validateEmail = (email: string) => {
    // eslint-disable-next-line
    return /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(email);
  };

  const validateForm = (): boolean => {
    let isValid: boolean = true;

    if (!email) {
      setEmailError("Email is Required");
      isValid = false;
    } else if (!validateEmail(email)) {
      setEmailError("Invalid Email");
      isValid = false;
    }

    return isValid;
  };

  const onReset = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (validateForm()) {
      try {
        setLoading(true);
        const res = await axios.post("/api/user/reset", { email });

        setResetError("");
        setResetSuccess(res.data.message);
      } catch (e) {
        const res = e as AxiosError;
        if (res.response?.data.message) {
          setResetError(res.response.data.message);
          setResetSuccess("");
        }
      }
      setLoading(false);
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
          <label htmlFor="email" className="font-mono text-md">
            Email
          </label>
          <input
            type="text"
            id="email"
            placeholder="Enter Email"
            className={`outline-none bg-none p-1 rounded-md outline-1 outline-gray-300 focus:ring focus:ring-blue-300 focus:outline-2 ${
              emailError ? "outline-red-500" : ""
            }`}
            value={email}
            autoComplete="off"
            onChange={(e) => {
              setEmail(e.target.value);
              setEmailError("");
              setResetError("");
            }}
          />
          {emailError && (
            <div className="text-[0.8rem] mt-1 px-1 text-red-500">
              {emailError}
            </div>
          )}
        </div>

        <button
          className={`my-10 px-10 py-2 shadow-gray-400 shadow-md bg-green-400 hover:bg-green-300 transition-all hover:text-gray-700 rounded-md ${
            Boolean(emailError) ? "text-gray-700 bg-green-300" : ""
          }`}
          type="submit"
          disabled={Boolean(emailError || loading)}
        >
          Send Reset Email
        </button>
      </form>
    </div>
  );
};

export default SendResetEmail;
