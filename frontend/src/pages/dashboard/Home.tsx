import React, { useContext, useEffect, useState } from "react";
import { Link, useLocation } from "react-router-dom";
import axios from "axios";
import { UserContext } from "../../App";
import MenuLink from "../../components/MenuLink";

const HEADER_SIZE = 4;

interface BluePrintInterface {
  name: string;
  id: string;
}

interface BluePrintRes {
  bluePrints: BluePrintInterface[];
}

const Home = () => {
  const { user } = useContext(UserContext);
  const [bluePrints, setBluePrints] = useState<BluePrintInterface[]>([]);

  useEffect(() => {
    axios.post<BluePrintRes>("/api/blueprint/all").then((res) => {
      setBluePrints(res.data.bluePrints);
    });
  }, []);

  return (
    <div>
      {bluePrints.map((bluePrint) => {
        return <h1>{bluePrint.id}</h1>;
      })}
    </div>
  );
};

export default Home;
