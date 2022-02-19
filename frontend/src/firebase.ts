import { initializeApp } from "firebase/app";
import { getMessaging, getToken } from "firebase/messaging";

const firebaseConfig = {
  apiKey: "AIzaSyDwXnQoDI2yngZGOdoY6Qk7xy82LZLpGy0",
  authDomain: "socketiot.firebaseapp.com",
  projectId: "socketiot",
  storageBucket: "socketiot.appspot.com",
  messagingSenderId: "283433854755",
  appId: "1:283433854755:web:338ea4ad2f33cc354d0e96",
};

const app = initializeApp(firebaseConfig);

const messaging = getMessaging(app);

const vapidKey =
  "BBb-k-GUz39u0AaGm4EvrOxhPuimyFr48XLwmvGfVV1nTfGW5iZn2GEjD28Y5JTg1ySE6ZgLj2YBB9q5TwfNU4Q";

export { messaging, getToken, vapidKey };
