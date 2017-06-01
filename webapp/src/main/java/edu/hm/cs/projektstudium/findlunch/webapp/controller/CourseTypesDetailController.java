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
import edu.hm.cs.projektstudium.findlunch.webapp.model.CourseTypes;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Offer;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Restaurant;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.model.validation.CustomCourseTypeValidator;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.CourseTypeRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.OfferRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.RestaurantRepository;

@Controller
public class CourseTypesDetailController {

	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(OfferController.class);
	
	@Autowired
	private CourseTypeRepository courseTypeRepository;
	
	/** The restaurant repository. */
	@Autowired
	private RestaurantRepository restaurantRepository;
	
	@Autowired
	private OfferRepository offerRepository;
	
	@Autowired
	private CustomCourseTypeValidator courseTypeValidator;
	
	/**
	 * Gets the page for adding a new courseType.
	 * @param model 
	 * 			Model in which necessary object are placed to be displayed on the website.
	 * @param principal
	 * 			Currently logged in user.
	 * @param session
	 * @param request
	 * @return the string for the corresponding HTML page
	 */
	@RequestMapping(path="/coursetype/add", method=RequestMethod.GET)
	public String getCourseTypesDetailNew(Model model, Principal principal, HttpSession session, HttpServletRequest request){
		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		User authenticatedUser = (User)((Authentication)principal).getPrincipal();
		if(authenticatedUser.getAdministratedRestaurant() == null) {
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The user " + authenticatedUser.getUsername() + " has no restaurant. A restaurant has to be added before offers can be added."));
			return "redirect:/restaurant/add?required";
		}
		
		CourseTypes courseType = new CourseTypes();
		model.addAttribute("courseType", courseType);
		return "coursetypeDetails";
	}
	
	/**
	 * Gets the page for editing an already existing coursetype.
	 * @param coursetypeId
	 * @param model
	 * @param principal
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping(path="/coursetype/edit/{coursetypeId}", method=RequestMethod.GET)
	public String getCourseTypesDetailUpdate(@PathVariable("coursetypeId") Integer coursetypeId, Model model, Principal principal, HttpSession session, HttpServletRequest request){
		LOGGER.info(LogUtils.getDefaultInfoStringWithPathVariable(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "coursetypeId", coursetypeId.toString()));
		
		User authenticatedUser = (User)((Authentication) principal).getPrincipal();
		
		if(authenticatedUser.getAdministratedRestaurant() == null) {
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The user " + authenticatedUser.getUsername() + " has no restaurant. A restaurant has to be added before offers can be edited."));
			return "redirect:/restaurant/add?required";
		}
		
		CourseTypes courseType = courseTypeRepository.findById(coursetypeId);
		if(courseType == null) {
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The coursetype with id " + courseType + " could not be found"));
			return "redirect:/coursetype?invalid_id";
		}
		model.addAttribute("courseType", courseType);
		return "coursetypeDetails";
	}
	
	/**
	 *  Save the coursetypes to the database. New coursetypes are stored, edited offers are updated.
	 * @param coursetype
	 * @param bindingResult
	 * @param principal
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(path={"/coursetype/edit/{coursetypesId}", "/coursetype/add"}, method=RequestMethod.POST, params={"saveCourse"})
	public String saveCourseType(@Valid @ModelAttribute("courseType") final CourseTypes courseType, BindingResult bindingResult, Principal principal, Model model, HttpServletRequest request) {
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		User authenticatedUser = (User) ((Authentication) principal).getPrincipal();
		
		Restaurant restaurant = restaurantRepository.findOne(authenticatedUser.getAdministratedRestaurant().getId());
		
		courseTypeValidator.validate(courseType, bindingResult);
		if(bindingResult.hasErrors()) {
			LOGGER.error(LogUtils.getValidationErrorString(request, bindingResult, Thread.currentThread().getStackTrace()[1].getMethodName()));
			return "coursetypeDetails";			
		}
		
		courseType.setRestaurantId(restaurant.getId());
		if(!courseTypeRepository.findByRestaurantIdOrderBySortByAsc(authenticatedUser.getAdministratedRestaurant().getId()).contains(courseType)){
			courseType.setSortBy(courseTypeRepository.findByRestaurantIdOrderBySortByAsc(authenticatedUser.getAdministratedRestaurant().getId()).size()+1);
		}
		courseTypeRepository.save(courseType);
		return "redirect:/coursetype?success";
	}
	
	/**
	 * Cancel the process for adding / editing a coursetype.
	 * @param model
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping(path={"/coursetype/edit/{coursetypesId}", "/coursetype/add"}, method=RequestMethod.POST, params={"cancel"})
	public String cancel(Model model, HttpSession session, HttpServletRequest request) {
		LOGGER.info(LogUtils.getCancelInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));

		return "redirect:/coursetype";
	}
	
	/**
	 * Gets the page for an overview of all offers within a coursetype.
	 * @param coursetypeId
	 * @param model
	 * @param principal
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping(path="/coursetype/overview/{coursetypeId}", method=RequestMethod.GET)
	public String getOffersInCourse(@PathVariable("coursetypeId") Integer coursetypeId, Model model, Principal principal, HttpSession session, HttpServletRequest request){
		LOGGER.info(LogUtils.getDefaultInfoStringWithPathVariable(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "coursetypeId", coursetypeId.toString()));
		
		User authenticatedUser = (User)((Authentication) principal).getPrincipal();
		
		if(authenticatedUser.getAdministratedRestaurant() == null) {
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The user " + authenticatedUser.getUsername() + " has no restaurant. A restaurant has to be added before offers can be edited."));
			return "redirect:/restaurant/add?required";
		}
		
		CourseTypes courseType = courseTypeRepository.findById(coursetypeId);
		if(courseType == null) {
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The coursetype with id " + courseType + " could not be found"));
			return "redirect:/coursetype?invalid_id";
		}
		List<Offer> offers = (ArrayList<Offer>) offerRepository.findByRestaurant_idOrderByOrderAsc(authenticatedUser.getAdministratedRestaurant().getId());
		List<Offer> courseOffers = new ArrayList<Offer>();
		
		for(Offer offer : offers) {
			if(offer.getCourseType() == coursetypeId){
				courseOffers.add(offer);
			}
		}
		if(courseOffers.isEmpty()){
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The restaurant with id " + authenticatedUser.getAdministratedRestaurant().getId() + " has no offers in coursetype " +courseType));
			return "redirect:/coursetype?no_offers";
		}
		
		model.addAttribute("courseType", courseType);
		model.addAttribute("offers", courseOffers);
		return "courseOverview";
	}
	
	/**
	 * Sets the value for the order within the coursetype lower.
	 * @param offerId
	 * @param model
	 * @param principal
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping(path="coursetype/overview/up/{offerId}", method=RequestMethod.GET)
	public String offerUp(@PathVariable("offerId") Integer offerId, Model model, Principal principal, HttpSession session, HttpServletRequest request){
		LOGGER.info(LogUtils.getDefaultInfoStringWithPathVariable(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "offerId", offerId.toString()));
		
		User authenticatedUser = (User)((Authentication) principal).getPrincipal();
		
		Offer offer = offerRepository.findByIdAndRestaurant_idOrderByOrderAsc(offerId, authenticatedUser.getAdministratedRestaurant().getId());
		
		int courseTypeId = offer.getCourseType();
		List<Offer> offersInCourse = offerRepository.findByCourseTypeOrderByOrderAsc(courseTypeId);
		int position = offer.getOrder();
		
		if(position!=0){
			for(Offer offerEntry : offersInCourse){
				if(offerEntry.getOrder()==position-1){
					offerEntry.setOrder(position);
					offerRepository.save(offerEntry);
				}
			}
			offer.setOrder(position-1);
			offerRepository.save(offer);
		}

		return "redirect:/coursetype/overview/"+courseTypeId;
	}
	
	/**
	 * Sets the value for the order within the coursetype higher.
	 * @param offerId
	 * @param model
	 * @param principal
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping(path="coursetype/overview/down/{offerId}", method=RequestMethod.GET)
	public String offerDown(@PathVariable("offerId") Integer offerId, Model model, Principal principal, HttpSession session, HttpServletRequest request){
		LOGGER.info(LogUtils.getDefaultInfoStringWithPathVariable(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "offerId", offerId.toString()));
		
		User authenticatedUser = (User)((Authentication) principal).getPrincipal();
		
		Offer offer = offerRepository.findByIdAndRestaurant_idOrderByOrderAsc(offerId, authenticatedUser.getAdministratedRestaurant().getId());
		
		int courseTypeId = offer.getCourseType();
		List<Offer> offersInCourse = offerRepository.findByCourseTypeOrderByOrderAsc(courseTypeId);
		int position = offer.getOrder();
		
		if(position!=offerRepository.findByCourseTypeOrderByOrderAsc(courseTypeId).size()){
			for(Offer offerEntry : offersInCourse){
				if(offerEntry.getOrder()==position+1){
					offerEntry.setOrder(position);
					offerRepository.save(offerEntry);
				}
			}
			offer.setOrder(position+1);
			offerRepository.save(offer);
		}

		return "redirect:/coursetype/overview/"+courseTypeId;
	}
}
