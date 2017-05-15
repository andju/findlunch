package edu.hm.cs.projektstudium.findlunch.webapp.controller;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.rest.LogRestController;
import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.PushToken;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.PushTokenRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserRepository;

@Controller
public class TokenController {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PushTokenRepository pushTokenRepository;
	
    /**
     * The logger.
     */
    private final Logger LOGGER = LoggerFactory.getLogger(LogRestController.class);
    
    /**
     * @param pushToken
     * @param principal
     * @param request
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(path="/submitToken/{pushToken}", method = RequestMethod.GET)
    ResponseEntity<Integer> submitToken(@PathVariable("pushToken") String pushToken, Principal principal, HttpServletRequest request){
    	LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
    	
    	System.out.println("Principal: "+principal.getName());
    	User authenticatedUser = (User) ((Authentication) principal).getPrincipal();
    	authenticatedUser = userRepository.findOne(authenticatedUser.getId());
    	PushToken oldToken = pushTokenRepository.findByUserId(authenticatedUser.getId());
    	
    	if(oldToken==null){
    		PushToken newToken = new PushToken();
        	newToken.setUser_id(authenticatedUser.getId());
        	newToken.setFcm_token(pushToken);
        	pushTokenRepository.save(newToken);
        	return new ResponseEntity<>(0, HttpStatus.ACCEPTED);
    	}
    	else if(!oldToken.equals(pushToken)){
    		pushTokenRepository.delete(oldToken.getId());
    		PushToken newToken = new PushToken();
    		newToken.setUser_id(authenticatedUser.getId());
        	newToken.setFcm_token(pushToken);
        	pushTokenRepository.save(newToken);
        	return new ResponseEntity<>(1, HttpStatus.OK);
    	}
    	else{
    		return new ResponseEntity<>(2, HttpStatus.ALREADY_REPORTED);
    	}
    }
	
}
