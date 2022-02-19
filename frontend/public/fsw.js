importScripts("https://www.gstatic.com/firebasejs/8.2.0/firebase-app.js");
importScripts("https://www.gstatic.com/firebasejs/8.2.0/firebase-messaging.js");

const firebaseConfig = {
  apiKey: "AIzaSyDwXnQoDI2yngZGOdoY6Qk7xy82LZLpGy0",
  authDomain: "socketiot.firebaseapp.com",
  projectId: "socketiot",
  storageBucket: "socketiot.appspot.com",
  messagingSenderId: "283433854755",
  appId: "1:283433854755:web:338ea4ad2f33cc354d0e96",
};

firebase.initializeApp(firebaseConfig);

const messaging = firebase.messaging();

messaging.onBackgroundMessage(function (payload) {
  const notificationTitle = payload.notification.title;
  const notificationOptions = {
    body: payload.notification.body,
  };

  return self.registration.showNotification(
    notificationTitle,
    notificationOptions
  );
});
