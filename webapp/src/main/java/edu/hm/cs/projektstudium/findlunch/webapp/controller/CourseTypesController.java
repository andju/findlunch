package edu.hm.cs.projektstudium.findlunch.webapp.controller;

import java.security.Principal;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.CourseTypeRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.model.CourseTypes;

@Controller
public class CourseTypesController {

	@Autowired
	private CourseTypeRepository courseTypeRepository;
	
	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(OfferController.class);
	
	/**
	 * Gets the page, that displays the courseType overview.
	 * 
	 * @param model
	 * @param principal
	 * @param request
	 * @return
	 */
	@RequestMapping(path = "/coursetype", method = RequestMethod.GET)
	public String getCourseTypes(Model model, Principal principal, HttpServletRequest request){
		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		

		User authenticatedUser = (User)((Authentication)principal).getPrincipal();
		if(authenticatedUser.getAdministratedRestaurant() == null) {
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The user " + authenticatedUser.getUsername() + " has no restaurant. A restaurant has to be added before offers can be selected."));
			return "redirect:/restaurant/add?required";
		}
		
			ArrayList<CourseTypes> courseTypes = (ArrayList<CourseTypes>) courseTypeRepository.findByRestaurantIdOrderBySortByAsc(authenticatedUser.getAdministratedRestaurant().getId());
			//CourseTypeList courseTypesList = new CourseTypeList();
			//courseTypesList.setCourseTypes(courseTypes);
			model.addAttribute("courseTypes", courseTypes);
			return "coursetype";	
			
	}
	
	@RequestMapping(path="/coursetype/delete/{courseId}", method=RequestMethod.GET)
	public String delteCourseType(@PathVariable("courseId") Integer courseId, Model model, Principal principal, HttpServletRequest request){
		LOGGER.info(LogUtils.getDefaultInfoStringWithPathVariable(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "courseId", courseId.toString()));
		
		User authenticatedUser = (User) ((Authentication) principal).getPrincipal();
		if(authenticatedUser.getAdministratedRestaurant() == null) {
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The user " + authenticatedUser.getUsername() + " has no restaurant. A restaurant has to be added before offers can be selected."));
			return "redirect:/restaurant/add?required";
		}
		
		CourseTypes coursetype = courseTypeRepository.findByIdAndRestaurantId(courseId, authenticatedUser.getAdministratedRestaurant().getId());
		if(coursetype == null) {
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The coursetype with id " + courseId + " could not be found for the given restaurant with id " + authenticatedUser.getAdministratedRestaurant().getId() + "."));
			return "redirect:/coursetype?invalid_id";
		}
		
		courseTypeRepository.delete(coursetype);
		return "redirect:/coursetype?deleted";
	}
	
	
}
