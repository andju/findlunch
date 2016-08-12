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
import edu.hm.cs.projektstudium.findlunch.webapp.model.DayOfWeek;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.DayOfWeekRepository;

/**
 * The Class DayOfWeekRestController. The class is responsible for handling rest
 * calls related to DayOfWeeks
 */
@RestController
public class DayOfWeekRestController {

	/** The DayOfWeekRepository repository. */
	@Autowired
	private DayOfWeekRepository dowRepository;

	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(DayOfWeekRestController.class);
	
	/**
	 * Gets all days of week.
	 *
	 * @param request the HttpServletRequest
	 * @return all days of week
	 */
	@JsonView(RestaurantView.RestaurantRest.class)
	@RequestMapping(path = "/api/days_of_week", method = RequestMethod.GET)
	public List<DayOfWeek> getAllDaysOfWeek(HttpServletRequest request) {
		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		return dowRepository.findAll();
	}
}
