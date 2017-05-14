package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.hm.cs.projektstudium.findlunch.webapp.model.PushToken;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.PushTokenRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserRepository;

@RestController
public class TokenRestContoller {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PushTokenRepository pushTokenRepository;
	
    /**
     * The logger.
     */
    private final Logger LOGGER = LoggerFactory.getLogger(LogRestController.class);
    
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(path="api/submitToken/{pushToken}")
    ResponseEntity<Integer> submitToken(@PathVariable("pushToken") String pushToken, Principal principal, HttpServletRequest request){
    	
    	User authenticatedUser = (User) ((Authentication) principal).getPrincipal();
    	authenticatedUser = userRepository.findOne(authenticatedUser.getId());	
    	
    	if(!pushTokenRepository.findByUserId(authenticatedUser.getId()).equals(pushToken) || pushTokenRepository.findByUserId(authenticatedUser.getId())==null){
    		
    		PushToken oldToken = pushTokenRepository.findByUserId(authenticatedUser.getId());
    		pushTokenRepository.delete(oldToken.getId());
    		
    		PushToken newToken = new PushToken();
        	newToken.setUser_id(authenticatedUser.getId());
        	newToken.setFcm_token(pushToken);
        	pushTokenRepository.save(newToken);
        	return new ResponseEntity<>(0, HttpStatus.OK);
    	}
    	else{
    		return new ResponseEntity<>(0, HttpStatus.ALREADY_REPORTED);
    	}
    }
	
}
