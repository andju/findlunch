/**
 * 
 */
var config = {
    apiKey: "AIzaSyA77wRwTsl8cPvxzXY5LrK_8-atYo05N9o",
    authDomain: "findlunch-1309.firebaseapp.com",
    databaseURL: "https://findlunch-1309.firebaseio.com",
    projectId: "findlunch-1309",
    storageBucket: "findlunch-1309.appspot.com",
    messagingSenderId: "343682752512"
  };
  firebase.initializeApp(config);

  const messaging = firebase.messaging();
  

  
  if ('serviceWorker' in navigator) {
	    navigator.serviceWorker.register('/js/firebase-messaging-sw.js')
	    .then(function(registration) {
	    	
	    	messaging.useServiceWorker(registration);
	    	
	    	messaging.requestPermission()
	    	  .then(function() {
	    	    console.log('Notification permission granted.');
	    	    
	    	    return messaging.getToken();
	    	  })
	    	  .then(function(token) {
	    		  console.log(token);
	    		  
	    		  if(window.XMLHttpRequest){
	    			  xmlhttpTkn=new XMLHttpRequest();
	    		  }
	    		  else{
	    			  xmlhttpTkn=new ActiveXObject("Microsoft.XMLHTTP");
	    		  }
	    		  
	    		  xmlhttpTkn.onreadystatchange=function(){
	    			  // test
	    		  }
	   
	    		  xmlhttpTkn.open("GET","/submitToken/"+token);
	    		  xmlhttpTkn.send();
	    		  
	    		  })
	    	  .catch(function(err) {
	    	    console.log('Unable to get permission to notify.', err);
	    	  });
	    })	    	 
	  } else {
	    callback('Service workers aren\'t supported in this browser.', null);
	}
  
  messaging.onTokenRefresh(function() {
	  
	  navigator.serviceWorker.register('/js/firebase-messaging-sw.js')
	    .then(function(registration) {
	    	
	    	messaging.requestPermission()
	    	  .then(function() {
	    	    console.log('Notification permission granted.');
	    	    
	    	    return messaging.getToken();
	    	  })
	    	  .then(function(token) {
	    		  console.log(token);
	    		  
	    		  if(window.XMLHttpRequest){
	    			  xmlhttpTkn=new XMLHttpRequest();
	    		  }
	    		  else{
	    			  xmlhttpTkn=new ActiveXObject("Microsoft.XMLHTTP");
	    		  }
	    		  
	    		  xmlhttpTkn.onreadystatchange=function(){
	    			  // test
	    		  }
	   
	    		  xmlhttpTkn.open("PUT","/api/submitToken/"+token, false);
	    		  xmlhttpTkn.send();
	    		  
	    		  })
	    	  .catch(function(err) {
	    	    console.log('Unable to get permission to notify.', err);
	    	  });
	    })
	  .catch(function(err) {
	    console.log('Unable to retrieve refreshed token ', err);
	    showToken('Unable to retrieve refreshed token ', err);
	  });
  });

  messaging.onMessage(function(payload) {
	  console.log("Message received. ", payload);
	  // ...
	});