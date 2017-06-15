package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.push.SseSend;

@RestController
public class SseRestController {
	
	private final Map<Integer, SseEmitter> sses = new ConcurrentHashMap<>();
	private final Map<Integer, Long> timestamp = new ConcurrentHashMap<>();
 	
	@RequestMapping(path="/sse", method = RequestMethod.GET)
	SseEmitter emitter(Principal principal) {
		System.out.println("SSE: "+principal);
		
		SseEmitter emitter = new SseEmitter(60000L);
		User authenticatedUser = (User)((Authentication)principal).getPrincipal();
		sses.put(authenticatedUser.getId(), emitter);
		if(timestamp.containsKey(authenticatedUser.getId())){
			Long diff = System.currentTimeMillis() - timestamp.get(authenticatedUser.getId());
			System.out.println("Diff: "+diff);
			timestamp.remove(authenticatedUser.getId());
		}
		timestamp.put(authenticatedUser.getId(), System.currentTimeMillis());
		
		authenticatedUser.setEmitter(emitter);
		//SseSend sseSend = new SseSend(emitter);
		//sseSend.run();
		return emitter;
	}
	
	public SseEmitter getEmitter(int id){
		return sses.get(id);
	}
	
}
