package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.model.validation.CustomUserValidator;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserTypeRepository;

/**
 * The Class RegisterUserRestController. The class is responsible for handling
 * rest calls related to registering users
 */
@RestController
public class RegisterUserRestController {

	/** The user repository. */
	@Autowired
	private UserRepository userRepository;

	/** The user type repository. */
	@Autowired
	private UserTypeRepository userTypeRepository;

	/** The bcrypt password encoder. */
	@Autowired
	private BCryptPasswordEncoder bcryptPasswordEncoder;

	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(RegisterUserRestController.class);
	
	/**
	 * Register a single user.
	 *
	 * @param request the HttpServletRequest
	 * @param user
	 *            the user that should be registered
	 * @return the response entity
	 */
	@RequestMapping(path = "/api/register_user", method = RequestMethod.POST)
	public ResponseEntity<Integer> registerUser(@RequestBody(required = true) User user, HttpServletRequest request) {
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		if (userRepository.findByUsername(user.getUsername()) != null) {
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The user with username " + user.getUsername() + " could not be found in the databse."));
			return new ResponseEntity<Integer>(3, HttpStatus.CONFLICT);
		}

		if (!CustomUserValidator.validateEmail(user.getUsername())) {
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The username " + user.getUsername() + " is not valid."));
			return new ResponseEntity<Integer>(1, HttpStatus.CONFLICT);
		}

		if (!CustomUserValidator.checkPasswordRules(user.getPassword())) {
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The password rules for user " + user.getUsername() + " are not fullfilled."));
			return new ResponseEntity<Integer>(2, HttpStatus.CONFLICT);
		}

		user.setUserType(userTypeRepository.findByName("Kunde"));
		user.setPassword(bcryptPasswordEncoder.encode(user.getPassword()));

		userRepository.save(user);

		return new ResponseEntity<Integer>(0, HttpStatus.OK);

	}

}
