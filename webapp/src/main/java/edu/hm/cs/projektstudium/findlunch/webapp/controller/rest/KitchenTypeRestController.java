package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.RestaurantView;
import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.KitchenType;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.KitchenTypeRepository;

/**
 * The Class KitchenTypeRestController. The class is responsible for handling rest
 * calls related to KitchenTypes
 */
@RestController
public class KitchenTypeRestController {

	/** The type repository. */
	@Autowired
	private KitchenTypeRepository typeRepository;
	
	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(KitchenTypeRestController.class);
	
	/**
	 * Gets all KitchenTypes.
	 *
	 * @param request the HttpServletRequest
	 * @return all KitchenTypes
	 */
	@CrossOrigin
	@JsonView(RestaurantView.RestaurantRest.class)
	@RequestMapping(path = "/api/kitchen_types", method = RequestMethod.GET)
	public List<KitchenType> getAllKitchenTypes(HttpServletRequest request) {
		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		return typeRepository.findAll();
	}
}
