package edu.hm.cs.projektstudium.findlunch.webapp.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.model.validation.CustomCourseTypeValidator;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.CourseTypeRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.OfferRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.model.CourseTypes;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Offer;

/**
 * The class CourseTypesController
 * The class is responsible for handling http calls related to the coursetype page
 * 
 * @author Niklas Klotz
 *
 */
@Controller
public class CourseTypesController {

	@Autowired
	private CourseTypeRepository courseTypeRepository;
	
	@Autowired
	private OfferRepository offerRepository;

	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(OfferController.class);
	
	/**
	 * Gets the page, that displays the courseType overview.
	 * 
	 * @param model Model in which necessary object are placed to be displayed on the website.
	 * @param principal currently logged in user
	 * @param request http request
	 * @return the page coursetype
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
			model.addAttribute("courseTypes", courseTypes);
			return "coursetype";	
	}
	
	/**
	 * Deletes a coursetype.
	 * @param courseId the coursetype to be deleted
	 * @param model Model in which necessary object are placed to be displayed on the website.
	 * @param principal the currently logged in user
	 * @param request http request
	 * @return the page coursetype with parameter
	 */
	@RequestMapping(path="/coursetype/delete/{courseId}", method=RequestMethod.GET)
	public String delteCourseType(@PathVariable("courseId") Integer courseId, Model model, Principal principal, HttpServletRequest request){
		LOGGER.info(LogUtils.getDefaultInfoStringWithPathVariable(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "courseId", courseId.toString()));
		
		User authenticatedUser = (User) ((Authentication) principal).getPrincipal();
		if(authenticatedUser.getAdministratedRestaurant() == null) {
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The user " + authenticatedUser.getUsername() + " has no restaurant. A restaurant has to be added before offers can be selected."));
			return "redirect:/restaurant/add?required";
		}
		
		// if the given id of coursetyope is not in the database
		CourseTypes coursetype = courseTypeRepository.findByIdAndRestaurantId(courseId, authenticatedUser.getAdministratedRestaurant().getId());
		if(coursetype == null) {
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The coursetype with id " + courseId + " could not be found for the given restaurant with id " + authenticatedUser.getAdministratedRestaurant().getId() + "."));
			return "redirect:/coursetype?invalid_id";
		}
		
		List<Offer> offersInCourse = offerRepository.findByCourseTypeOrderByOrderAsc(coursetype.getId());
		// if the coursetype contains offers
		if(!offersInCourse.isEmpty()){
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The coursetype with id " + courseId + " contains " + offersInCourse.size() + " offers."));
			return "redirect:/coursetype?containsOffers";
		}
		// if the restaurant deleted its last coursetype
		if(offerRepository.findByCourseTypeOrderByOrderAsc(coursetype.getId()).size()==1){
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The restaurant with the Id " + authenticatedUser.getAdministratedRestaurant().getId() + " deleted his last coursetype."));
			courseTypeRepository.delete(coursetype);
			return "/coursetype/add";
		}
			
		courseTypeRepository.delete(coursetype);
		return "redirect:/coursetype?deleted";
	}
	
	/**
	 * Sets the value for the coursetype lower within the list of coursetypes.
	 * @param courseId the coursetype
	 * @param model Model in which necessary object are placed to be displayed on the website.
	 * @param principal the currently logged in user
	 * @param session the session
	 * @param request http request
	 * @return redirect to the page coursetype
	 */
	@RequestMapping(path="coursetype/up/{courseId}", method=RequestMethod.GET)
	public String offerUp(@PathVariable("courseId") Integer courseId, Model model, Principal principal, HttpSession session, HttpServletRequest request){
		LOGGER.info(LogUtils.getDefaultInfoStringWithPathVariable(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "courseId ", courseId.toString()));
		
		User authenticatedUser = (User)((Authentication) principal).getPrincipal();
		
		CourseTypes course = courseTypeRepository.findById(courseId);
		List<CourseTypes> restaurantCourses = courseTypeRepository.findByRestaurantIdOrderBySortByAsc(authenticatedUser.getAdministratedRestaurant().getId());
		
		int position = course.getSortBy();
		
		// it the current possition of the coursetype is not 0, which is the lowest availabile, its order will be changed
		if(position!=0){
			for(CourseTypes type : restaurantCourses){
				if(type.getSortBy()==position-1){
					type.setSortBy(position);
					courseTypeRepository.save(type);
					course.setSortBy(position-1);
					courseTypeRepository.save(course);
				}
			}
		}

		return "redirect:/coursetype";
	}
	
	/**
	 * Sets the value for coursetype higher within the list.
	 * @param courseId the coursetype
	 * @param model Model in which necessary object are placed to be displayed on the website.
	 * @param principal the currently logged in user
	 * @param session the session
	 * @param request http request
	 * @return redirect to the page coursetype
	 */
	@RequestMapping(path="coursetype/down/{courseId}", method=RequestMethod.GET)
	public String offerDown(@PathVariable("courseId") Integer courseId, Model model, Principal principal, HttpSession session, HttpServletRequest request){
		LOGGER.info(LogUtils.getDefaultInfoStringWithPathVariable(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "courseId ", courseId.toString()));
		
		User authenticatedUser = (User)((Authentication) principal).getPrincipal();
		
		CourseTypes course = courseTypeRepository.findById(courseId);
		List<CourseTypes> restaurantCourses = courseTypeRepository.findByRestaurantIdOrderBySortByAsc(authenticatedUser.getAdministratedRestaurant().getId());
		
		int position = course.getSortBy();
		
		// if the current possition of the coursetype is not the highest avaliabile
		if(position!=restaurantCourses.size()){
			for(CourseTypes type : restaurantCourses){
				if(type.getSortBy()==position+1){
					type.setSortBy(position);
					courseTypeRepository.save(type);
					course.setSortBy(position+1);
					courseTypeRepository.save(course);
				}
			}
		}

		return "redirect:/coursetype";
	}
	
	/**
	 * Checks if a coursetype can be deleted
	 * @param courseId the coursetype
	 * @return true if can be deleted
	 */
	@RequestMapping(path="coursetype/checkDelete/{courseId}", method=RequestMethod.GET)
	public Boolean checkDelete(@PathVariable("courseId") Integer courseId){
		
		List<Offer> offersInCourse = offerRepository.findByCourseTypeOrderByOrderAsc(courseId);
		if(offersInCourse.isEmpty()){
			return true;
		}
		return false;
	}
}
