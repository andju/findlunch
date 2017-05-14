package edu.hm.cs.projektstudium.findlunch.webapp.controller;

import java.security.Principal;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.PushNotification;
import edu.hm.cs.projektstudium.findlunch.webapp.model.PushToken;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.push.PushNotificationManager;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.PushTokenRepository;;

/**
 * The class is responsible for handling http calls related to the main page (home) of the website..
 * Root / relinks to /home.
 */
@Controller
public class HomeController {

	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	PushTokenRepository tokenRepo;

	/**
	 * Gets the the "home" page for a request with the path "/home".
	 *
	 * @param request the HttpServletRequest
	 * @return the string for the corresponding HTML page
	 */
	@RequestMapping(path="/home", method=RequestMethod.GET)
	public String getHome(HttpServletRequest request) {
		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));		
		return "home";
	}
	
	/**
	 * In case a request to the root of the website "/" is received, the user gets redirected to "/home"
	 *
	 * @param request the HttpServletRequest
	 * @return the string for the corresponding HTML page
	 */
	@RequestMapping(path="/", method=RequestMethod.GET)
	public String getIndex(HttpServletRequest request) {
		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		return "redirect:/home";
	}
	
	@RequestMapping(path="/home", method = RequestMethod.POST, params={"push"})
	public void sendPushAtHome(Principal principal)  {
		//System.out.println("ENTER FCM TOKEN: "+token);
		
		User authenticatedUser = (User)((Authentication)principal).getPrincipal();
		PushToken token = tokenRepo.findByUserId(authenticatedUser.getId());
		sendPush(token.getFcm_token());
		
		//return "home";
	}
	
	@RequestMapping(path="/home?token", method = RequestMethod.POST)
	public String getClientToken(@RequestParam String token) {
		System.out.println("ENTER FCM TOKEN: "+token);
		return "home";
	}
	
	private void sendPush(String token){
		
		PushNotificationManager pushManager = new PushNotificationManager();
		PushNotification push = new PushNotification();
		
		push.generateWeb();
		push.setFcmToken(token);
		
		pushManager.sendFcmNotification(push);
	}

}
