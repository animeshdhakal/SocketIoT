import axios, { AxiosError } from "axios";
import React, { FormEvent, useState } from "react";
import useQuery from "../components/hooks/useQuery";

const Login = () => {
  const [email, setEmail] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [emailError, setEmailError] = useState<string>("");
  const [passwordError, setPasswordError] = useState<string>("");
  const [loginError, setLoginError] = useState<string>("");
  const [loading, setLoading] = useState<boolean>(false);

  const query = useQuery();

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

    if (!password) {
      setPasswordError("Password is Required");
      isValid = false;
    } else if (password.length < 5) {
      setPasswordError("Password must be greater than 5 Characters");
      isValid = false;
    }

    return isValid;
  };

  const onLogin = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (validateForm()) {
      const redirect_uri = query.get("redirect_uri");
      const state = query.get("state");
      if (!redirect_uri || !state) {
        setLoginError("Invalid Request");
        return;
      }

      try {
        setLoading(true);
        const res = await axios.post("/api/user/login", { email, password });
        const token = res.data.token;

        const url = new URL(redirect_uri);
        url.searchParams.set("code", token);

        window.location.replace(url.toString());
      } catch (e) {
        const error = e as AxiosError;
        if (error.response?.data.message) {
          setLoginError(error.response.data.message);
        } else {
          setLoginError("Login Failed");
        }
      }
      setLoading(false);
    }
  };

  return (
    <div className="w-screen h-screen flex justify-center items-center flex-col">
      <form
        className="h-auto w-96 shadow-2x flex flex-col items-center  shadow-gray-300 shadow-xl border-gray-300 border-[0.2px]"
        onSubmit={(e) => onLogin(e)}
      >
        <h1 className="text-3xl my-6">SocketIoT</h1>
        <h1 className="text-center my-5 text-xl">
          Allow this app to access your account information.
        </h1>

        {loginError && (
          <div className="bg-red-200 w-80 py-2 text-center border-[0.1px] border-red-400 rounded-md text-rose-600">
            {loginError}
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
              setLoginError("");
            }}
          />
          {emailError && (
            <div className="text-[0.8rem] mt-1 px-1 text-red-500">
              {emailError}
            </div>
          )}
        </div>

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
              setLoginError("");
            }}
          />
          {passwordError && (
            <div className="text-[0.8rem] mt-1 px-1 text-red-500">
              {passwordError}
            </div>
          )}
        </div>

        <button
          className={`my-10 px-10 py-2 shadow-gray-400 shadow-md bg-green-400 hover:bg-green-300 transition-all hover:text-gray-700 rounded-md ${
            Boolean(emailError || passwordError)
              ? "text-gray-700 bg-green-300"
              : ""
          }`}
          type="submit"
          disabled={Boolean(emailError || passwordError || loading)}
        >
          Login
        </button>
      </form>
    </div>
  );
};

export default Login;
