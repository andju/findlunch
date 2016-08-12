package edu.hm.cs.projektstudium.findlunch.webapp.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.model.validation.CustomUserValidator;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserTypeRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.security.RestaurantUserDetailsService;

/**
 * The class is responsible for handling http calls when registering a user.
 */
@Controller
public class RegisterController {

	/** The user repository. */
	@Autowired
	private UserRepository userRepository;
	
	/** The user type repository. */
	@Autowired
	private UserTypeRepository userTypeRepository;
	
	/** The password encoder. */
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	/** The custom user details service for restaurant users. Used to automatically login a user after registering */
	@Autowired
	private RestaurantUserDetailsService customUserDetailsService;
	
	/** The custom user validator. Handled enhanced checks not handled by the hibernate annotation */
	@Autowired
	private CustomUserValidator customUserValidator;
	
	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(RegisterController.class);

	/**
	 * Gets the page for registering a new user.
	 *
	 * @param request the HttpServletRequest
	 * @param model
	 * 			Model in which necessary object are placed to be displayed on the website.
	 * @return the string for the corresponding HTML page
	 */
	@RequestMapping(path = "/register", method = RequestMethod.GET)
	public String getRegister(Model model, HttpServletRequest request) {
		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		model.addAttribute("user", new User());

		return "register";
	}

	/**
	 * Method for saving a user to the database during registration process.
	 *
	 * @param user
	 * 			User object to be saved. Populated by the content of the html form field.
	 * @param bindingResult
	 * 			Binding result in which errors for the fields are stored. Populated by hibernate validation annotation and custom validator classes.
	 * @param model
	 * 			Model in which necessary object are placed to be displayed on the website.
	 * @param request
	 *			Request sent by the user. Used to get a new session for the user.
	 * @return the string for the corresponding HTML page
	 */
	@RequestMapping(method = RequestMethod.POST, path = { "/register" }, params = { "saveRegister" })
	public String saveRegister(@Valid final User user, BindingResult bindingResult, final Model model, HttpServletRequest request) {

		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		// Checks not handled by Hibernate annotations
		customUserValidator.validate(user, bindingResult);

		if (bindingResult.hasErrors()) {
			LOGGER.error(LogUtils.getValidationErrorString(request, bindingResult, Thread.currentThread().getStackTrace()[1].getMethodName()));
			return "register";
		}

		String encodedPass = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPass);

		// Set default role to "Anbieter"
		user.setUserType(userTypeRepository.findByName("Anbieter"));
		
		userRepository.save(user);

		authenticateUser(user, request);

		return "redirect:/restaurant/add";
	}

	/**
	 * Cancel register.
	 *
	 * @param request
	 * 			Request sent by user
	 * @return the string for the corresponding HTML page
	 */
	@RequestMapping(path = { "/register" }, method = RequestMethod.POST, params = { "cancel" })
	public String cancelRegister(HttpServletRequest request) {
		LOGGER.info(LogUtils.getCancelInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		return "redirect:/home";
	}

	/**
	 * Authenticate user.
	 *
	 * @param user
	 * 			User object that was just registered. Is used to populate the SecurityContextHolder.
	 * @param request
	 * 			Request sent by the user.
	 */
	private void authenticateUser(User user, HttpServletRequest request) {

		User userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

		// generate session if one doesn't exist
		request.getSession();

		SecurityContextHolder.getContext().setAuthentication(token);
		LOGGER.info("User with username: " + user.getUsername() + " has been successfully authenticated after registering. Session: " + request.getSession().getId());

	}

}
