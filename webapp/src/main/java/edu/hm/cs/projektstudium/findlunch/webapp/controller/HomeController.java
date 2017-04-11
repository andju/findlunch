package edu.hm.cs.projektstudium.findlunch.webapp.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;

/**
 * The class is responsible for handling http calls related to the main page (home) of the website..
 * Root / relinks to /home.
 */
@Controller
public class HomeController {
	
	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

	/**
	 * Gets the the "home" page for a request with the path "/home".
	 *
	 * @param request the HttpServletRequest
	 * @return the string for the corresponding HTML page
	 */
	@RequestMapping(path="/home", method=RequestMethod.GET)
	public String getHome(HttpServletRequest request) {
		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));		
		return "home";
	}
	
	/**
	 * In case a request to the root of the website "/" is received, the user gets redirected to "/home"
	 *
	 * @param request the HttpServletRequest
	 * @return the string for the corresponding HTML page
	 */
	@RequestMapping(path="/", method=RequestMethod.GET)
	public String getIndex(HttpServletRequest request) {
		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		return "redirect:/home";
	}
	
}
