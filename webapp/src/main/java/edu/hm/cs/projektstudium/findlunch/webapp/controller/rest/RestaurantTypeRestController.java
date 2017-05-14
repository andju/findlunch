package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.RestaurantView;
import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.RestaurantType;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.RestaurantTypeRepository;

/**
 * The Class RestaurantTypeRestController. The class is responsible for handling
 * rest calls related to RestaurantTypes
 */
@RestController
public class RestaurantTypeRestController {

	/** The restaurant type repository. */
	@Autowired
	private RestaurantTypeRepository typeRepository;

	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(RestaurantTypeRestController.class);
	
	/**
	 * Gets all RestaurantTypes.
	 *
	 * @param request the HttpServletRequest
	 * @return all RestaurantTypes
	 */
	@JsonView(RestaurantView.RestaurantRest.class)
	@RequestMapping(path = "/api/restaurant_types", method = RequestMethod.GET)
	public List<RestaurantType> getAllRestaurantTypes(HttpServletRequest request) {
		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		return typeRepository.findAll();
	}
}
