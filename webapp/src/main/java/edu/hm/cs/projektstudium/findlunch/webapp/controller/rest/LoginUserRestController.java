package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;

/**
 * The Class LoginUserRestController. The class is responsible for handling rest
 * calls related to login users
 */
@RestController
public class LoginUserRestController {

	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(LoginUserRestController.class);
	
	/**
	 * Login user.
	 *
	 * @param request the HttpServletRequest
	 * @return the response entity representing a status code
	 */
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(path="/api/login_user", method=RequestMethod.GET)
	public ResponseEntity<Integer> loginUser(HttpServletRequest request) {
		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		return new ResponseEntity<Integer>(0,HttpStatus.OK);
	}
	
}
