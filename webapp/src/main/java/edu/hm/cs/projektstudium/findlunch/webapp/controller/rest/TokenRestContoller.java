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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.PushToken;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.PushTokenRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserRepository;

/**
 * The Class TokenRestController.
 * The class is respinsible for handling API calls to store the customers Firebase Token into the database.
 * 
 * @author Niklas Klotz
 *
 */
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
    
    /**
     * Puts the token into the database
     * 
     * @param pushToken the customers token
     * @param principal the customer
     * @param request the http request
     * @return response entity representing a status code
     */
    @CrossOrigin
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(path="api/submitToken/{pushToken}", method = RequestMethod.PUT)
    ResponseEntity<Integer> submitToken(@PathVariable("pushToken") String pushToken, Principal principal, HttpServletRequest request){
    	LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
    	
    	User authenticatedUser = (User) ((Authentication) principal).getPrincipal();
    	authenticatedUser = userRepository.findOne(authenticatedUser.getId());
    	PushToken oldToken = pushTokenRepository.findByUserId(authenticatedUser.getId());
    	
    	// if there is no token stored for the customer yet
    	if(oldToken==null){
    		PushToken newToken = new PushToken();
        	newToken.setUser_id(authenticatedUser.getId());
        	newToken.setFcm_token(pushToken);
        	pushTokenRepository.save(newToken);
        	return new ResponseEntity<>(0, HttpStatus.ACCEPTED);
    	}
    	// refresh the customers token
    	else if(!oldToken.equals(pushToken)){
    		pushTokenRepository.delete(oldToken.getId());
    		PushToken newToken = new PushToken();
    		newToken.setUser_id(authenticatedUser.getId());
        	newToken.setFcm_token(pushToken);
        	pushTokenRepository.save(newToken);
        	return new ResponseEntity<>(1, HttpStatus.OK);
    	}
    	// if the customer token is already in the database
    	else {
    		return new ResponseEntity<>(2, HttpStatus.ALREADY_REPORTED);
    	}
    }
	
}
