/*
 * 
 */
package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.PushNotificationView;
import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.DayOfWeek;
import edu.hm.cs.projektstudium.findlunch.webapp.model.KitchenType;
import edu.hm.cs.projektstudium.findlunch.webapp.model.DailyPushNotificationData;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.DayOfWeekRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.KitchenTypeRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.PushNotificationRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserRepository;


/**
*
* The Class PushNotificationRestController.
* 
* Registers pushes at api call: /api/register_push
* Shows pushes of current user at api call: /api/get_push
* Unregisters pushes at api call: /api/unregister_push/
* Receives initial push of current user at login.
* Initial push identification specified on mobile application side.
* Updating (changed) device identification / token of all pushes of current user in database.
*  
* Extended by Maxmilian Haag on 06.02.2017.
*/

@RestController
public class PushNotificationRestController {

	/** The push_notification repository. */
	@Autowired
	private PushNotificationRepository pushNotificationRepository;

	/** The user repository. */
	@Autowired
	private UserRepository userRepository;

	/** The day_of_week repository. */
	@Autowired
	private DayOfWeekRepository dayOfWeekRepository;

	/** The kitchen_type repository. */
	@Autowired
	private KitchenTypeRepository kitchenTypeRepository;

	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(PushNotificationRestController.class);
	
	/**
	 * Register push notification.
	 *
	 * @param request the HttpServletRequest
	 * @param pushNotification the pushNotification
	 * @param principal the principal
	 * @return the response entity
	 */
	@RequestMapping(path = "/api/register_push", method = RequestMethod.POST)
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Integer> registerPush(@RequestBody DailyPushNotificationData pushNotification, Principal principal, HttpServletRequest request) {
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		User requestUser = (User) ((Authentication) principal).getPrincipal();
		User authenticatedUser = userRepository.findOne(requestUser.getId());

		//Initial push for user with current device token
		//Updating all pushes of this user with new tokens in database.
		if(pushNotification.getTitle().equals("INIT_PUSH")) {
			
			List<DailyPushNotificationData> allNotificationsOfCurrentUser = pushNotificationRepository.findByUser_id(authenticatedUser.getId());

			for(int i = 0; i < allNotificationsOfCurrentUser.size(); i++) {
				DailyPushNotificationData pushToModify = allNotificationsOfCurrentUser.get(i);
				//delete old
				pushNotificationRepository.delete(pushToModify);
			
				//add with new token
				pushToModify.setFcmToken(pushNotification.getFcmToken());
				pushToModify.setSnsToken(pushNotification.getSnsToken());
				
				//token info log
				LOGGER.info(pushNotification.getSnsToken());
				LOGGER.info(pushNotification.getFcmToken());
				
				pushNotificationRepository.save(pushToModify);
			
			}
			//update info log
			LOGGER.info("User logged in, all tokens updated to current device");

			
		} else {
			//Not initial push, operation after update.
			List<DayOfWeek> daysOfWeekComplete = new ArrayList<DayOfWeek>();
			
			if (pushNotification.getDayOfWeeks() == null || 
					pushNotification.getDayOfWeeks().size() <= 0) {
				// No DayOfWeek specified for PushNotification
				LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The push notification has no DayOfWeek."));
				return new ResponseEntity<Integer>(4, HttpStatus.CONFLICT);
			} else {
				List<DayOfWeek> daysOfWeek = pushNotification.getDayOfWeeks();
				for (DayOfWeek d : daysOfWeek) {
					String dayOfWeekName = d.getName();
					daysOfWeekComplete.add(dayOfWeekRepository.findByName(dayOfWeekName));
					pushNotification.setDayOfWeeks(daysOfWeekComplete);
				}
			}

			List<KitchenType> kitchenTypeComplete = new ArrayList<KitchenType>();
			if (pushNotification.getKitchenTypes() != null && pushNotification.getKitchenTypes().size() > 0) {
				List<KitchenType> kitchenTypesList = pushNotification.getKitchenTypes();
				for (KitchenType k : kitchenTypesList) {
					int kitchenTypeId = k.getId();
					kitchenTypeComplete.add(kitchenTypeRepository.findById(kitchenTypeId));
				}
			}
			pushNotification.setKitchenTypes(kitchenTypeComplete);

			pushNotification.setUser(authenticatedUser);

			pushNotificationRepository.save(pushNotification);
		}
		

		return new ResponseEntity<Integer>(0, HttpStatus.OK);

	}

	/**
	 * Gets the push notifications of the currently logged in user.
	 *
	 * @param request the HttpServletRequest
	 * @param principal the principal
	 * @return the kitchen types
	 */
	@PreAuthorize("isAuthenticated()")
	@JsonView(PushNotificationView.PushNotificationRest.class)
	@RequestMapping(path = "/api/get_push", method = RequestMethod.GET)
	public List<DailyPushNotificationData> getPush(Principal principal, HttpServletRequest request) {
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		User requestUser = (User) ((Authentication) principal).getPrincipal();
		User authenticatedUser = userRepository.findOne(requestUser.getId());

		return pushNotificationRepository.findByUser_id(authenticatedUser.getId());

	}

	/**
	 * Unregister push notification.
	 *
	 * @param request the HttpServletRequest
	 * @param pushId the push id
	 * @param principal the principal
	 * @return the response entity
	 */
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(path = "/api/unregister_push/{pushId}", method = RequestMethod.DELETE)
	public ResponseEntity<Integer> unregisterPush(
			@PathVariable("pushId") Integer pushId, Principal principal, HttpServletRequest request) {
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		User requestUser = (User) ((Authentication) principal).getPrincipal();
		User authenticatedUser = userRepository.findOne(requestUser.getId());
		DailyPushNotificationData pushNotificationToUnregister = pushNotificationRepository.findById(pushId);
		
		if (pushNotificationToUnregister == null) {
			// Push Notification not found
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The PushNotification with id " + pushId + " could not be found in the database."));
			return new ResponseEntity<Integer>(3, HttpStatus.CONFLICT);
		} else if (pushNotificationToUnregister.getUser().getId() == authenticatedUser.getId()) {
			// Push Notification belongs to user
			pushNotificationRepository.delete(pushNotificationToUnregister);
			return new ResponseEntity<Integer>(0, HttpStatus.OK);
		} else {
			// Push Notification does not belong to user
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The PushNotification with id " + pushId + " does not belong to the user " + authenticatedUser.getUsername()));
			return new ResponseEntity<Integer>(3, HttpStatus.CONFLICT);
		}
		


	}
}
