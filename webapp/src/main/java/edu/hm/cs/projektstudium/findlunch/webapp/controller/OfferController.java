package edu.hm.cs.projektstudium.findlunch.webapp.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

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
import edu.hm.cs.projektstudium.findlunch.webapp.model.Offer;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.AdditivesRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.AllergenicRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.CourseTypeRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.DayOfWeekRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.OfferRepository;

/**
 * The class is responsible for handling http calls related to offer overview page.
 */
@Controller
public class OfferController {

	/** The offer repository. */
	@Autowired
	private OfferRepository offerRepository;

	/** The day of week repository. */
	@Autowired
	private DayOfWeekRepository dayOfWeekRepository;
	
	/** The additive repository. */
	@Autowired
	private AdditivesRepository additivesRepository;
	
	/** The allergenic repository. */
	@Autowired
	private AllergenicRepository allergenicRepository;
	
	/** NIKLAS KLOTZ */
	@Autowired
	private CourseTypeRepository courserTypeRepository;
	
	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(OfferController.class);

	/**
	 * Gets the page, that displays the offer overview.
	 *
	 * @param request the HttpServletRequest
	 * @param model
	 * 			Model in which necessary object are placed to be displayed on the website.
	 * @param principal the principal
	 * 			Currently logged in user.
	 * @return the string for the corresponding HTML page
	 */
	@RequestMapping(path = "/offer", method = RequestMethod.GET)
	public String getOffers(Model model, Principal principal, HttpServletRequest request) {
		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		User authenticatedUser = (User)((Authentication)principal).getPrincipal();
		
		
		if(authenticatedUser.getAdministratedRestaurant() == null) {
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The user " + authenticatedUser.getUsername() + " has no restaurant. A restaurant has to be added before offers can be selected."));
			return "redirect:/restaurant/add?required";
		}
		
		List<Offer> offers = (ArrayList<Offer>) offerRepository.findByRestaurant_idOrderByOrderAsc(authenticatedUser.getAdministratedRestaurant().getId());
		model.addAttribute("offers", offers);
		model.addAttribute("dayOfWeeks", dayOfWeekRepository.findAll());
		model.addAttribute("additives", additivesRepository.findAll());
		model.addAttribute("allergenic", allergenicRepository.findAll());
		//model.addAttribute("courseTypes" , getCourseTypesForOffers(offers));
		model.addAttribute("courseTypes" , courserTypeRepository.findByRestaurantIdOrderBySortByAsc(authenticatedUser.getAdministratedRestaurant().getId()));
		
		return "offer";
	}
	
	/**
	 * Deletes an offer from the overview page.
	 *
	 * @param request the HttpServletRequest
	 * @param offerId 
	 * 			Id of the offer to be deleted.
	 * @param model
	 * 			Model in which necessary object are placed to be displayed on the website.
	 * @param principal
	 * 			Currently logged in user.
	 * @return the string for the corresponding HTML page
	 */
	@RequestMapping(path="/offer/delete/{offerId}", method=RequestMethod.GET)
	public String deleteOffer(@PathVariable("offerId") Integer offerId, Model model, Principal principal, HttpServletRequest request) {
		LOGGER.info(LogUtils.getDefaultInfoStringWithPathVariable(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "offerId", offerId.toString()));
		
		User authenticatedUser = (User) ((Authentication) principal).getPrincipal();
		if(authenticatedUser.getAdministratedRestaurant() == null) {
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The user " + authenticatedUser.getUsername() + " has no restaurant. A restaurant has to be added before offers can be selected."));
			return "redirect:/restaurant/add?required";
		}
		
		Offer offer = offerRepository.findByIdAndRestaurant_idOrderByOrderAsc(offerId, authenticatedUser.getAdministratedRestaurant().getId());
		if(offer == null) {
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The offer with id " + offerId + " could not be found for the given restaurant with id " + authenticatedUser.getAdministratedRestaurant().getId() + "."));
			return "redirect:/offer?invalid_id";
		}

		offerRepository.delete(offer);
		return "redirect:/offer?deleted";
	}
	
	@RequestMapping(path="/offer/soldout/{offerId}", method=RequestMethod.GET)
	public String soldoutOffer(@PathVariable("offerId") Integer offerId, Model model, Principal principal, HttpServletRequest request){
		LOGGER.info(LogUtils.getDefaultInfoStringWithPathVariable(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "offerId", offerId.toString()));
		
		User authenticatedUser = (User) ((Authentication) principal).getPrincipal();
		
		if(authenticatedUser.getAdministratedRestaurant() == null) {
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The user " + authenticatedUser.getUsername() + " has no restaurant. A restaurant has to be added before offers can be selected."));
			return "redirect:/restaurant/add?required";
		}
		
		Offer offer = offerRepository.findByIdAndRestaurant_idOrderByOrderAsc(offerId, authenticatedUser.getAdministratedRestaurant().getId());
		if(offer == null) {
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The offer with id " + offerId + " could not be found for the given restaurant with id " + authenticatedUser.getAdministratedRestaurant().getId() + "."));
			return "redirect:/offer?invalid_id";
		}

		if(!offer.getSold_out()){
			offer.setSold_out(true);
			offerRepository.save(offer);
			return "redirect:/offer?soldOut";
		}
		
		offer.setSold_out(false);
		offerRepository.save(offer);
		return "redirect:/offer?availabile";		
	}
}
