import { BrowserRouter, Route, Routes } from "react-router-dom";
import MsgType from "./protocol/MsgType";
import { EventCallBackArgs, wsClient } from "./protocol/WSClient";

const App = () => {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<h1>Home</h1>} />
                <Route path="/about" element={<h1>About</h1>} />
            </Routes>
        </BrowserRouter>
    );
};

export default App;
