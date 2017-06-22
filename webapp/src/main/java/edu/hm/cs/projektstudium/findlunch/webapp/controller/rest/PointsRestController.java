package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.PointsView;
import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Points;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.PointsRepository;

/**
 * The Class PointsRestController.
 */
@RestController
public class PointsRestController {

	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(PointsRestController.class);
	
	/** The Points repository. */
	@Autowired
	private PointsRepository pointsRepository;
	
	/**
	 * Gets the points of an User.
	 * @param principal the principal to get the authenticated user
	 * @param request the HttpServletRequest
	 * @return a List from the current points of the user
	 */
	@CrossOrigin
    @JsonView(PointsView.PointsRest.class)
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(path = "/api/get_points", method = RequestMethod.GET, headers = {"Authorization"})
	public List<Points> getPointsOfAUser(/*@RequestParam(name ="user_id", required=true) int userId ,*/ Principal principal, HttpServletRequest request) {
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		/*
		User authenticatedUser = (User) ((Authentication) principal).getPrincipal();
		if(authenticatedUser.getId() == userId){
			List<Points> pointsOfUser = pointsRepository.findByCompositeKey_User_Id(userId);
			return pointsOfUser;
		}
		return null;//kein zugriffsrecht, da anderer Benutzer*/
		User authenticatedUser = (User) ((Authentication) principal).getPrincipal();
		List<Points> pointsOfUser = pointsRepository.findByCompositeKey_User_Id(authenticatedUser.getId());
		return pointsOfUser;
	}
	
	/**
	 * Gets the points of an User for a given Restaurant Id.
	 * @param principal the principal to get the authenticated user
	 * @param request the HttpServletRequest
	 * @return a List from the current points of the user
	 */
	@CrossOrigin
	@JsonView(PointsView.PointsRest.class)
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(path = "/api/get_points_restaurant/{restaurantId}", method = RequestMethod.GET, headers = {"Authorization"})
	public List<Points> getPointsOfAUserForRestaurant(@PathVariable("restaurantId") int restaurantId, Principal principal, HttpServletRequest request) {
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));

		User authenticatedUser = (User) ((Authentication) principal).getPrincipal();
		List<Points> pointsOfUser = pointsRepository.findByCompositeKey_User_IdAndCompositeKey_Restaurant_Id(authenticatedUser.getId(), restaurantId);
		return pointsOfUser;
	}
}
