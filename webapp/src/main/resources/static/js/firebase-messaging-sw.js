/**
 * 
 */
// Give the service worker access to Firebase Messaging.
// Note that you can only use Firebase Messaging here, other Firebase libraries
// are not available in the service worker.
importScripts('https://www.gstatic.com/firebasejs/3.5.2/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/3.5.2/firebase-messaging.js');

var config = {
	    messagingSenderId: "343682752512"
	  };
	  firebase.initializeApp(config);

// Retrieve an instance of Firebase Messaging so that it can handle background
// messages.
const messaging = firebase.messaging();

messaging.setBackgroundMessageHandler(function(payload) {
	  console.log('[firebase-messaging-sw.js] Received background message ', payload);
	  // Customize notification here
	  const notificationTitle = 'Background Message Title';
	  const notificationOptions = {
	    body: 'Background Message body.',
	    //icon: '/firebase-logo.png'
	  };

	  return self.registration.showNotification(notificationTitle,
	      notificationOptions);
	});
