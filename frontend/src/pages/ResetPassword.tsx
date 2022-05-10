import axios, { AxiosError } from "axios";
import React, { FormEvent, useEffect, useState } from "react";
import { Link, Navigate } from "react-router-dom";
import EnterNewPassword from "../components/EnterNewPassword";
import useQuery from "../components/hooks/useQuery";
import SendResetEmail from "../components/SendResetEmail";

const ResetPassword = () => {
  const [resetToken, setResetToken] = useState<string>("");
  const query = useQuery();

  useEffect(() => {
    const token = query.get("token");
    if (token) {
      setResetToken(token);
    }
  }, []);

  return resetToken ? (
    <EnterNewPassword token={resetToken} />
  ) : (
    <SendResetEmail />
  );
};

export default ResetPassword;
