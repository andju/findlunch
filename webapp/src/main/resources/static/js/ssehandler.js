/**
 * 
 */

var source = new EventSource('https://localhost:8443/sse');


	source.onopen = function(e) {
		alert('open');
	}
	
	source.onmessage = function(e) {
		alert('message');
	}

	/*
	source.addEventListener('message', function(e) {
	  alert('message');
	}, false);

	source.addEventListener('open', function(e) {
	  alert('open');
	}, false);

	source.addEventListener('error', function(e) {
	  if (e.readyState == EventSource.CLOSED) {
	    // Connection was closed.
	  }
	}, false);
	*/