package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.OfferView;
import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.RestaurantView;
import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Additives;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Allergenic;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Offer;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.AdditivesRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.AllergenicRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.OfferRepository;

/**
 * The Class IngredientRestController. The class is responsible for handling rest calls related to Ingredients.
 * Rest controllers mapping api.
 */
@RestController
public class IngredientRestController {

	/** The Allergenic repository. */
	@Autowired
	private AllergenicRepository allergenicRepository;

	/** The Additives repository. */
	@Autowired
	private AdditivesRepository additivesRepository;

	/** The Additives repository. */
	@Autowired
	private OfferRepository offerRepository;

	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(IngredientRestController.class);

	/**
	 * Gets all allergenic.
	 *
	 * @param request the HttpServletRequest
	 * @return all allergenic
	 */
	@CrossOrigin
	@JsonView(RestaurantView.RestaurantRest.class)
	@RequestMapping(path = "/api/all_allergenic", method = RequestMethod.GET)
	public List<Allergenic> getAllAllergenic(HttpServletRequest request) {
		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		return allergenicRepository.findAll();
	}

	/**
	 * Gets all allergenic for offer.
	 *
	 * @param request the HttpServletRequest
	 * @return all allergenic
	 */
	@CrossOrigin
	@JsonView(RestaurantView.RestaurantRest.class)
	@RequestMapping(path = "/api/allergenicForOfferId/{offerId}", method = RequestMethod.GET)
	public List<Allergenic> getAllergenicForOffer(@PathVariable("offerId") Integer offerId,
			Principal principal, HttpServletRequest request) {

		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		Offer offer = offerRepository.findOne(offerId);
		
		return offer.getAllergenic();
	}

	/**
	 * Gets all additives.
	 *
	 * @param request the HttpServletRequest
	 * @return all additives
	 */
	@CrossOrigin
	@JsonView(RestaurantView.RestaurantRest.class)
	@RequestMapping(path = "/api/all_additives", method = RequestMethod.GET)
	public List<Additives> getAllAdditives(HttpServletRequest request) {
		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		return additivesRepository.findAll();
	}
	
	/**
	 * Gets all additives for offer.
	 *
	 * @param request the HttpServletRequest
	 * @return all additives
	 */
	@CrossOrigin
	@JsonView(RestaurantView.RestaurantRest.class)
	@RequestMapping(path = "/api/additivesForOfferId/{offerId}", method = RequestMethod.GET)
	public List<Additives> getAdditivesForOffer(@PathVariable("offerId") Integer offerId,
			Principal principal, HttpServletRequest request) {

		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		Offer offer = offerRepository.findOne(offerId);
		
		return offer.getAdditives();
	}
}
