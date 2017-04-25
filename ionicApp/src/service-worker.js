/**
 * Check out https://googlechrome.github.io/sw-toolbox/docs/master/index.html for
 * more info on how to use sw-toolbox to custom configure your service worker.
 */


'use strict';
importScripts('./build/sw-toolbox.js');

// Give the service worker access to Firebase Messaging.
// Note that you can only use Firebase Messaging here, other Firebase libraries
// are not available in the service worker.
importScripts('https://www.gstatic.com/firebasejs/3.5.2/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/3.5.2/firebase-messaging.js');


// Initialize the Firebase app in the service worker by passing in the
// messagingSenderId.
firebase.initializeApp({
    'messagingSenderId': '459881169137'
});

// Retrieve an instance of Firebase Messaging so that it can handle background
// messages.
const messaging = firebase.messaging();

self.toolbox.options.cache = {
  name: 'ionic-cache'
};

// pre-cache our key assets
self.toolbox.precache(
  [
    './build/main.js',
    './build/main.css',
    './build/polyfills.js',
    'index.html',
    'manifest.json'
  ]
);

// dynamically cache any other local assets
self.toolbox.router.any('/*', self.toolbox.cacheFirst);

// for any other requests go to the network, cache,
// and then only use that cached resource if your user goes offline
self.toolbox.router.default = self.toolbox.networkFirst;