package edu.hm.cs.projektstudium.findlunch.webapp.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;

/**
 * The Class GeneralErrorController.
 */
@ControllerAdvice(assignableTypes={AboutController.class, FaqCustomerController.class, FaqRestaurantController.class, HomeController.class, LoginController.class, OfferController.class, OfferDetailController.class, PrivacyController.class, RegisterController.class, RestaurantController.class, TermController.class, CourseTypesController.class, CourseTypesDetailController.class})
public class GeneralErrorController {
	
	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(GeneralErrorController.class);
	
	/**
	 * Default error handler.
	 *
	 * @param request the request
	 * @param e the Exception
	 * @return the string
	 * @throws Exception the exception
	 */
	 @ExceptionHandler(value = Exception.class)
	    public String defaultErrorHandler(HttpServletRequest request, Exception e) throws Exception {
	    	LOGGER.error(LogUtils.getExceptionMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), e));
	        return "error";
	    }

}
