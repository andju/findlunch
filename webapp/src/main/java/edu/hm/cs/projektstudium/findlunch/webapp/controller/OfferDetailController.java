package edu.hm.cs.projektstudium.findlunch.webapp.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.CourseTypes;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Offer;
import edu.hm.cs.projektstudium.findlunch.webapp.model.OfferPhoto;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Restaurant;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.model.validation.CustomOfferValidator;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.CourseTypeRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.DayOfWeekRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.OfferRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.RestaurantRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.security.FileUploadRestrictorHelper;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.AdditivesRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.AllergenicRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.CourseTypeRepository;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.Base64;
import java.util.List;

/**
 * The class is responsible for handling http calls related to the offer details view.
 */
@Controller
public class OfferDetailController implements HandlerExceptionResolver {

	/**
	 * The logger object.
	 */
	@Autowired
	private FileUploadRestrictorHelper fileUploadRestrictorHelper;

	/** The offer repository. */
	@Autowired
	private OfferRepository offerRepository;
	
	/** The restaurant repository. */
	@Autowired
	private RestaurantRepository restaurantRepository;
	
	/** The day of week repository. */
	@Autowired
	private DayOfWeekRepository dayOfWeekRepository;
	
	/** The additive repository. */
	@Autowired
	private AdditivesRepository additivesRepository;
	
	/** The allergenic repository. */
	@Autowired
	private AllergenicRepository allergenicRepository;
	
	/** Niklas Klotz */
	@Autowired
	private CourseTypeRepository courseTypeRepository;
	
	/** The custom offer validator. Handled enhanced checks not handled by the hibernate annotation */
	@Autowired
	private CustomOfferValidator customOfferValidator;
	
	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(OfferDetailController.class);
	
	/**
	 * Gets the page for adding a new offer.
	 *
	 * @param request the HttpServletRequest
	 * @param model
	 * 			Model in which necessary object are placed to be displayed on the website.
	 * @param principal 
	 * 			Currently logged in user.
	 * @param session the session
	 * 			Session of the currently logged in user. Used to store the user photos.
	 * @return the string for the corresponding HTML page
	 */
	@RequestMapping(path="/offer/add", method=RequestMethod.GET)
	public String getOfferDetailNewOffer(Model model, Principal principal, HttpSession session, HttpServletRequest request) {
		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		User authenticatedUser = (User)((Authentication)principal).getPrincipal();
		if(authenticatedUser.getAdministratedRestaurant() == null) {
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The user " + authenticatedUser.getUsername() + " has no restaurant. A restaurant has to be added before offers can be added."));
			return "redirect:/restaurant/add?required";
		}
		List<CourseTypes> courseTypes = courseTypeRepository.findByRestaurantIdOrderBySortByAsc(authenticatedUser.getAdministratedRestaurant().getId());
		 if(courseTypes.isEmpty()){
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The restaurant with id " + authenticatedUser.getAdministratedRestaurant().getId() + " has no coursetypes."));
			return "redirect:/coursetype/add?required";
		}
		
		Offer newOffer = new Offer();
		session.setAttribute("photoList", newOffer.getOfferPhotos());
		model.addAttribute("offer", newOffer);
		model.addAttribute("dayOfWeeks", dayOfWeekRepository.findAll());
		model.addAttribute("courseTypes" , courseTypes);
		model.addAttribute("additives", additivesRepository.findAll());
		model.addAttribute("allergenic", allergenicRepository.findAll());
		model.addAttribute("restaurant",restaurantRepository.findById(authenticatedUser.getAdministratedRestaurant().getId()));
		return "offerDetail";
	}
	
	/**
	 * Gets the page for editing an already existing offer.
	 *
	 * @param request the HttpServletRequest
	 * @param offerId
	 * 			Id of the offer to be edited.
	 * @param model
	 * 			Model in which necessary object are placed to be displayed on the website.
	 * @param principal
	 * 			Currently logged in user.
	 * @param session
	 * 			Session of the currently logged in user. Used to store the user photos.		
	 * @return the string for the corresponding HTML page
	 */
	@RequestMapping(path="/offer/edit/{offerId}", method=RequestMethod.GET)
	public String getOfferDetailUpdateOffer(@PathVariable("offerId") Integer offerId, Model model, Principal principal, HttpSession session, HttpServletRequest request) {
		LOGGER.info(LogUtils.getDefaultInfoStringWithPathVariable(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "offerId", offerId.toString()));
		
		User authenticatedUser = (User)((Authentication) principal).getPrincipal();
		
		if(authenticatedUser.getAdministratedRestaurant() == null) {
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The user " + authenticatedUser.getUsername() + " has no restaurant. A restaurant has to be added before offers can be edited."));
			return "redirect:/restaurant/add?required";
		}
		List<CourseTypes> courseTypes = courseTypeRepository.findByRestaurantIdOrderBySortByAsc(authenticatedUser.getAdministratedRestaurant().getId());
		 if(courseTypes.isEmpty()){
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The restaurant with id " + authenticatedUser.getAdministratedRestaurant().getId() + " has no coursetypes."));
			return "redirect:/coursetype/add?required";
		}
		
		Offer offer = offerRepository.findByIdAndRestaurant_idOrderByOrderAsc(offerId, authenticatedUser.getAdministratedRestaurant().getId());
		if(offer == null) {
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The offer with id " + offerId + " could not be found for the given restaurant with id " + authenticatedUser.getAdministratedRestaurant().getId() + "."));
			return "redirect:/offer?invalid_id";
		}
		
		encodePhotosFromOfferToBase64(offer);
		session.setAttribute("photoList", offer.getOfferPhotos());
		model.addAttribute("offer", offer);
		model.addAttribute("dayOfWeeks", dayOfWeekRepository.findAll());
		model.addAttribute("courseTypes" , courseTypes);
		model.addAttribute("additives", additivesRepository.findAll());
		model.addAttribute("allergenic", allergenicRepository.findAll());
		model.addAttribute("restaurant",restaurantRepository.findById(authenticatedUser.getAdministratedRestaurant().getId()));
		return "offerDetail";	
	}
	
	/**
	 * Save the offer to the database. New offers are stored, edited offers are updated. Before storing the offer, a thumbail is created for all photos to be stored with the offer.
	 *
	 * @param request the HttpServletRequest
	 * @param offer
	 * 			Offer object to be saved. Populated by the content of the html form field.
	 * @param bindingResult
	 * 			Binding result in which errors for the fields are stored. Populated by hibernate validation annotation and custom validator classes.
	 * @param principal
	 * 			Currently logged in user.
	 * @param model
	 * 			Model in which necessary object are placed to be displayed on the website.
	 * @param session
	 * 			Session of the current user. Used to store offer photos.
	 * @return the string for the corresponding HTML page
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(path={"/offer/add", "/offer/edit/{offerId}"}, method=RequestMethod.POST, params={"saveOffer"})
	public String saveOffer(@Valid final Offer offer, BindingResult bindingResult, Principal principal, Model model, HttpServletRequest request) {
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		HttpSession session = request.getSession();
		User authenticatedUser = (User) ((Authentication) principal).getPrincipal();
		
		Restaurant restaurant = restaurantRepository.findOne(authenticatedUser.getAdministratedRestaurant().getId());
		restaurant.addOffer(offer);
		offer.setOfferPhotos((List<OfferPhoto>)session.getAttribute("photoList"));
		
		// Checks not handled by Hibernate annotations
		customOfferValidator.validate(offer, bindingResult);
		
		if(bindingResult.hasErrors()) {
			model.addAttribute("dayOfWeeks", dayOfWeekRepository.findAll());
			model.addAttribute("additives", additivesRepository.findAll());
			model.addAttribute("allergenic", allergenicRepository.findAll());
			model.addAttribute("restaurant",restaurantRepository.findById(authenticatedUser.getAdministratedRestaurant().getId()));
			model.addAttribute("courseTypes", courseTypeRepository.findByRestaurantIdOrderBySortByAsc(authenticatedUser.getAdministratedRestaurant().getId()));
			
			LOGGER.error(LogUtils.getValidationErrorString(request, bindingResult, Thread.currentThread().getStackTrace()[1].getMethodName()));
			return "offerDetail";			
		}
						
		for(OfferPhoto p : offer.getOfferPhotos()) {
			p.setOffer(offer);
		}
		
		try {
			createThumbnails(offer);
		} catch (IOException e) {
			model.addAttribute("invalidPicture", true);
			model.addAttribute("dayOfWeeks", dayOfWeekRepository.findAll());
			model.addAttribute("additives", additivesRepository.findAll());
			model.addAttribute("allergenic", allergenicRepository.findAll());
			model.addAttribute("restaurant",restaurantRepository.findById(authenticatedUser.getAdministratedRestaurant().getId()));
			offer.setOfferPhotos((List<OfferPhoto>)session.getAttribute("photoList"));
			
			LOGGER.error(LogUtils.getExceptionMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), e));
			return "offerDetail";
		}
		
		if(!offerRepository.findByRestaurant_idOrderByOrderAsc(authenticatedUser.getId()).contains(offer)){
			offer.setOrder(offerRepository.findByCourseTypeOrderByOrderAsc(offer.getCourseType()).size()+1);
		}
		
		offerRepository.save(offer);
		
		session.removeAttribute("photoList");
		
		return "redirect:/offer?success";
	}
	
	/**
	 * Cancel the process for adding / editing an offer.
	 *
	 * @param request the HttpServletRequest
	 * @param model
	 * 			Model in which necessary object are placed to be displayed on the website.
	 * @param session
	 * 			Session of the current user. Used to store offer photos.
	 * @return the string for the corresponding HTML page
	 */
	@RequestMapping(path={"/offer/add", "/offer/edit/{offerId}"}, method=RequestMethod.POST, params={"cancel"})
	public String cancel(Model model, HttpSession session, HttpServletRequest request) {
		LOGGER.info(LogUtils.getCancelInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		session.removeAttribute("photoList");
		return "redirect:/offer";
	}
	
	/**
	 * Handles the upload of a new offer photo. Resolves the image format, generates the base64 string for the website. Stores the newly added image to the session.
	 *
	 * @param request the HttpServletRequest
	 * @param offer
	 * 			Offer object to be saved. Populated by the content of the html form field.
	 * @param model
	 * 			Model in which necessary object are placed to be displayed on the website.
	 * @param file
	 * 			Uploaded file.
	 * @param session
	 * 			Session of the current user. Used to store offer photos.
	 * @param principal
	 * 			Currently logged in user.
	 * @return the string for the corresponding HTML page
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(path={"/offer/add", "/offer/edit/{offerId}"}, method=RequestMethod.POST, params={"addImage"})
	public String addImage(final Offer offer, Model model, @RequestParam("img") MultipartFile file, Principal principal, HttpServletRequest request) {
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		HttpSession session = request.getSession();
		
		User authenticatedUser = (User) ((Authentication)principal).getPrincipal();
		
		String imageFormat = resolveImageFormat(file.getContentType());

		fileUploadRestrictorHelper.uploadAttempt(request.getRemoteAddr(), session.getId());
		session.setAttribute("blockedFileUpload", fileUploadRestrictorHelper.isBlocked(request.getRemoteAddr(),
				session.getId()));
		if (Boolean.parseBoolean(session.getAttribute("blockedFileUpload").toString())) {
			model.addAttribute("blockedFileUpload", true);
			model.addAttribute("dayOfWeeks", dayOfWeekRepository.findAll());
			model.addAttribute("additives", additivesRepository.findAll());
			model.addAttribute("allergenic", allergenicRepository.findAll());
			model.addAttribute("restaurant",
					restaurantRepository.findById(authenticatedUser.getAdministratedRestaurant().getId()));
			offer.setOfferPhotos((List<OfferPhoto>)session.getAttribute("photoList"));

			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(),
					"FileUploadLimit reached"));
			return "offerDetail";
		}

		if (!file.getContentType().startsWith("image") || imageFormat.equals("")) {
			model.addAttribute("invalidPicture", true);
			model.addAttribute("dayOfWeeks", dayOfWeekRepository.findAll());
			model.addAttribute("additives", additivesRepository.findAll());
			model.addAttribute("allergenic", allergenicRepository.findAll());
			model.addAttribute("restaurant", restaurantRepository.findById(authenticatedUser.getAdministratedRestaurant().getId()));
			offer.setOfferPhotos((List<OfferPhoto>)session.getAttribute("photoList"));
			
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The picture type was invalid. Only images are allowed, but type was: " + file.getContentType() + " with image format: " + imageFormat));
			return "offerDetail";
		}
		
		OfferPhoto newPhoto = new OfferPhoto();
		try {
			newPhoto.setPhoto(file.getBytes());
			newPhoto.setBase64Encoded(Base64.getEncoder().encodeToString(file.getBytes()));
			newPhoto.setImageFormat(imageFormat);
		} catch (IOException e) {
			model.addAttribute("invalidPicture", true);
			model.addAttribute("dayOfWeeks", dayOfWeekRepository.findAll());
			model.addAttribute("additives", additivesRepository.findAll());
			model.addAttribute("allergenic", allergenicRepository.findAll());
			model.addAttribute("restaurant", restaurantRepository.findById(authenticatedUser.getAdministratedRestaurant().getId()));
			offer.setOfferPhotos((List<OfferPhoto>)session.getAttribute("photoList"));
			
			LOGGER.error(LogUtils.getExceptionMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), e));
			return "offerDetail";
		}
		
		List<OfferPhoto> offerPhotos = (List<OfferPhoto>) session.getAttribute("photoList");
		offer.setOfferPhotos(offerPhotos);
		offer.addOfferPhoto(newPhoto);
		model.addAttribute("dayOfWeeks", dayOfWeekRepository.findAll());
		model.addAttribute("additives", additivesRepository.findAll());
		model.addAttribute("allergenic", allergenicRepository.findAll());
		model.addAttribute("restaurant", restaurantRepository.findById(authenticatedUser.getAdministratedRestaurant().getId()));
		session.setAttribute("photoList", offer.getOfferPhotos());
		return "offerDetail";
	}

	/**
	 * In order to prevent information disclosure this method was added. Without this method a full stack trace
	 * was shown to the user when a file bigger than the defined multipart.maxFileSize was sent by the user.
	 * This stack trace could reveal some sensitive information to a potential attacker.
	 * This is a quite unclean approach but shows the problematic.
	 *
	 * @param httpServletRequest the HttpServletRequest
	 * @param httpServletResponse the HttpServletResponse
	 * @param o an Object
	 * @param ex an Exception
     *
	 * @return a defined ModelAndView
	 */
	@ResponseBody
	public ModelAndView resolveException(final HttpServletRequest httpServletRequest,
										 final HttpServletResponse httpServletResponse, final Object o,
										 final Exception ex) {
		if (ex instanceof MultipartException) {
			final ModelAndView modelAndView = new ModelAndView("filesize_error");
			NotificationController.sendMessageToTelegram("Someone tried to upload a file bigger than ten megabyte."
					+ " The IP-address was: " + httpServletRequest.getRemoteAddr() + " The session-ID was: "
					+ httpServletRequest.getSession().getId());
			ex.printStackTrace();
			return modelAndView;
		}

		// Spring-Security has to handle this exception.
		if (ex instanceof AccessDeniedException) {
			return null;
		}

		/*NotificationController.sendMessageToTelegram("Exception in OfferDetailController."
				+ " The IP-address was: " + httpServletRequest.getRemoteAddr() + " The session-ID was: "
				+ httpServletRequest.getSession().getId());
		return new ModelAndView("error");*/
		ex.printStackTrace();
		// If it is not a MultipartException, the exception should be handled by something else and not this method.
		return null;
	}
	
	/**
	 * Deletes an offer photo from the session. It is deleted from the database after the offer is saved.
	 *
	 * @param request the HttpServletRequest
	 * @param offer
	 * 			Offer object to be saved. Populated by the content of the html form field.
	 * @param model
	 * 			Model in which necessary object are placed to be displayed on the website.
	 * @param imageId
	 * 			Id of the images to be deleted.
	 * @param session
	 * 			Session of the current user. Used to store offer photos.
	 * @param principal
	 * 			Currently logged in user.
	 * @return the string for the corresponding HTML page
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(path={"/offer/add", "/offer/edit/{offerId}"}, method=RequestMethod.POST, params={"deleteImage"})
	public String deleteImage(final Offer offer, Model model, @RequestParam("deleteImage") Integer imageId, Principal principal, HttpServletRequest request) {
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		HttpSession session = request.getSession();
		User authenticatedUser = (User) ((Authentication)principal).getPrincipal();
		
		offer.setOfferPhotos((List<OfferPhoto>) session.getAttribute("photoList"));
		offer.removeOfferPhoto(offer.getOfferPhotos().get(imageId));
		
		model.addAttribute("dayOfWeeks", dayOfWeekRepository.findAll());
		model.addAttribute("additives", additivesRepository.findAll());
		model.addAttribute("allergenic", allergenicRepository.findAll());
		model.addAttribute("restaurant", restaurantRepository.findById(authenticatedUser.getAdministratedRestaurant().getId()));
		session.setAttribute("photoList", offer.getOfferPhotos());
		return "offerDetail";
	}
	
	/**
	 * Encode photos from offer to base 64.
	 *
	 * @param offer the offer
	 */
	private void encodePhotosFromOfferToBase64(Offer offer) {
		
		for(OfferPhoto photo : offer.getOfferPhotos()) {
			
			String base64Encoded = Base64.getEncoder().encodeToString(photo.getPhoto()); 
			photo.setBase64Encoded(base64Encoded);	
		}
	}
	
	/**
	 * Creates the thumbnails for photos with a size of 200*200.
	 *
	 * @param offer the offer
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void createThumbnails(Offer offer) throws IOException{
		
		for(OfferPhoto photo : offer.getOfferPhotos()) {
			if(photo.getThumbnail() == null) {
				InputStream inputStream = new ByteArrayInputStream(photo.getPhoto());
				
				BufferedImage img = ImageIO.read(inputStream);
				BufferedImage thumbNail = Scalr.resize(img, 200);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(thumbNail,photo.getImageFormat() , baos);
				photo.setThumbnail(baos.toByteArray());
				inputStream.close();
				baos.close();	
			}
		}
	}
	
	/**
	 * Resolve image format using the contentType of the image.
	 *
	 * @param imageContentType the image content type
	 * @return the string
	 */
	private String resolveImageFormat(String imageContentType) {
		
		String temp = imageContentType.toLowerCase();
		
		if(temp.endsWith("jpeg")) {
			return "JPEG";
		} else if(temp.endsWith("jpg")) {
			return "JPG";
		} else if(temp.endsWith("png")) {
			return "PNG";
		} else if(temp.endsWith("gif")) {
			return "GIF";
		} else if(temp.endsWith("tiff")) {
			return "TIFF";
		} else {
			return "";
		}
	}
}
