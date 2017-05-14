/**
 * 
 */
// Give the service worker access to Firebase Messaging.
// Note that you can only use Firebase Messaging here, other Firebase libraries
// are not available in the service worker.
importScripts('https://www.gstatic.com/firebasejs/3.5.2/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/3.5.2/firebase-messaging.js');

var config = {
	    apiKey: "AIzaSyA77wRwTsl8cPvxzXY5LrK_8-atYo05N9o",
	    authDomain: "findlunch-1309.firebaseapp.com",
	    databaseURL: "https://findlunch-1309.firebaseio.com",
	    projectId: "findlunch-1309",
	    storageBucket: "findlunch-1309.appspot.com",
	    messagingSenderId: "343682752512"
	  };
	  firebase.initializeApp(config);

// Retrieve an instance of Firebase Messaging so that it can handle background
// messages.
const messaging = firebase.messaging();