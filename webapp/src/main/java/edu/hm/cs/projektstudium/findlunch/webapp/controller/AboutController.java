package edu.hm.cs.projektstudium.findlunch.webapp.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;

/**
 * The class is responsible for handling http calls related to term page.
 */
@Controller
public class AboutController {
	
	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(AboutController.class);

	/**
	 * Gets the terms HTML page.
	 *
	 * @param request the HttpServletRequest
	 * @return the string for the corresponding HTML page
	 */
	@RequestMapping(path="/about_findlunch", method=RequestMethod.GET)
	public String getAboutFindLunch(HttpServletRequest request) {
		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		return "about_findlunch";
	}
	
}
