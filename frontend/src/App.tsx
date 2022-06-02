// 1. Import `createTheme`
import { createTheme, NextUIProvider } from "@nextui-org/react";

// 2. Call `createTheme` and pass your custom values
const lightTheme = createTheme({
  type: "light",
  theme: {
    colors: {},
  },
});

const darkTheme = createTheme({
  type: "dark",
  theme: {
    colors: {},
  },
});

// 3. Apply light or dark theme depending on localStorage and `getDocumentTheme` value
// App.jsx entry point of your app
import React, { useEffect, useState } from "react";
import { getDocumentTheme } from "@nextui-org/react";

const App = () => {
  const [isDark, setIsDark] = useState(false);

  useEffect(() => {
    // you can use any storage
    let theme = window.localStorage.getItem("data-theme");
    setIsDark(theme === "dark");

    const observer = new MutationObserver((mutation) => {
      let newTheme = getDocumentTheme(document?.documentElement);
      setIsDark(newTheme === "dark");
    });

    // Observe the document theme changes
    observer.observe(document?.documentElement, {
      attributes: true,
      attributeFilter: ["data-theme", "style"],
    });

    return () => observer.disconnect();
  }, []);

  return (
    <NextUIProvider theme={isDark ? darkTheme : lightTheme}>
      <App />
    </NextUIProvider>
  );
};

// 4. Use `changeTheme` method to control theme changing
import { Switch, changeTheme, useTheme } from "@nextui-org/react";

const Main = () => {
  const { type, isDark } = useTheme();

  const handleChange = () => {
    const nextTheme = isDark ? "light" : "dark";
    window.localStorage.setItem("data-theme", nextTheme); // you can use any storage
    changeTheme(nextTheme);
  };

  return (
    <div>
      The current theme is: {type}
      <Switch checked={isDark} onChange={handleChange} />
    </div>
  );
};

export default App;
