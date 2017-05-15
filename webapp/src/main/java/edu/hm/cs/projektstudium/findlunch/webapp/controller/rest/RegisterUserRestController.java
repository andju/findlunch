package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.CaptchaController;
import edu.hm.cs.projektstudium.findlunch.webapp.controller.NotificationController;
import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.model.validation.CustomUserValidator;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * The Class RegisterUserRestController. The class is responsible for handling
 * rest calls related to registering users
 */
@RestController
public class RegisterUserRestController {

	/** The request. */
	@Autowired
	private HttpServletRequest request;

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
	@CrossOrigin
	@RequestMapping(path = "/api/register_user", method = RequestMethod.POST)
	public ResponseEntity<Integer> registerUser(@RequestBody(required = true) User user, HttpServletRequest request) {
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));

		//TODO Captcha - get Key from google (https://www.google.com/recaptcha)
		/*
		if (user.getCaptcha() == null || user.getCaptcha().getAnswer() == null) {
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(),
					"The Captcha: " + " was empty."));
			// Todo Ggf. Architekturdokument_public.pdf entsprechend neuem Statuscode anpassen
			NotificationController.sendMessageToTelegram("The CAPTCHA wasn't solved correctly in the mobile application."
					+ "The Captcha answer was: Empty" + " The IP of the user was: " + getClientIP());
			return new ResponseEntity<>(4, HttpStatus.CONFLICT);
		}

		//if (user.getCaptcha() == null || user.getCaptcha().getAnswer() == null) {
			if (!CaptchaController.verifyCaptcha(user.getCaptcha(), getClientIP())) {
				LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(),
						"The Captcha: " +
								user.getCaptcha().getAnswer() + " was not solved correctly." +
								" Token: " + user.getCaptcha().getImageToken()));
				// Todo Ggf. Architekturdokument_public.pdf entsprechend neuem Statuscode anpassen
				NotificationController.sendMessageToTelegram("The CAPTCHA wasn't solved correctly in the mobile application."
						+ "The Captcha answer was: " + user.getCaptcha().getAnswer() + " The token was: "
						+ user.getCaptcha().getImageToken() + " The IP of the user was: " + getClientIP());
				return new ResponseEntity<>(4, HttpStatus.CONFLICT);
			}
		//}
		 */

		if (userRepository.findByUsername(user.getUsername()) != null) {
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(),"The user with username " + user.getUsername() + " could not be found in the databse."));
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

	/**
	 * This method gets the client's IP-address and pays attention for the X-Forwarded-For header which could
	 * identify a proxy user. See for example: https://tools.ietf.org/html/rfc7239
	 *
	 * @return the client's IP-address
	 */
	private String getClientIP() {
		final String xffHeader = request.getHeader("X-Forwarded-For");
		if (xffHeader == null){
			return request.getRemoteAddr();
		}
		return xffHeader.split(",")[0];
	}

}
