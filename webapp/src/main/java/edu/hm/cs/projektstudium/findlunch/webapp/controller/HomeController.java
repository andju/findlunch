package edu.hm.cs.projektstudium.findlunch.webapp.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.PushToken;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.push.EmailService;
import edu.hm.cs.projektstudium.findlunch.webapp.push.PushNotificationManager;
import edu.hm.cs.projektstudium.findlunch.webapp.push.SseSend;
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
	
	@RequestMapping(path="/home", method = RequestMethod.POST, params={"sse"})
	public void sendPushAtHome(Principal principal) throws AddressException, MessagingException  {
		
		User authenticatedUser = (User)((Authentication)principal).getPrincipal();
		/** TEST FÜR SSE */
		//EmailService service = new EmailService();
		
		PushToken token = tokenRepo.findByUserId(authenticatedUser.getId());
		
		try{
		sendPush(token.getFcm_token());
		} catch (Exception e){
			e.printStackTrace();
		}
		
		
		
		
		/*
		try{
		service.sendSimpleMessage(authenticatedUser.getUsername(), "oder", "Sie haben neue Bestellungen");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		*/
		
		//service.mimMessage(authenticatedUser.getUsername(), "Test", "Das ist ein Mime Test");
		
		
		/*
		try{
		service.mailSend(authenticatedUser.getUsername(), "Test", "Das ist ein Mime Test");
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		*/
	}
	
	private void sendPush(String token){
		
		PushNotificationManager pushManager = new PushNotificationManager();
		
		
		JSONObject notification = pushManager.generateWeb(token);
		
		//push.put("to", "eVvkYMnfv5s:APA91bHpUqLqwBXwaJlkqVQLRPA8Dbj8Hms2DaVWBhlbhbl20dpkTmpdEVBSggddg6ALNdEMfagoSOzYIA1zrBxAhTWSn5ipIKxDTlmItjE55OEwCk7F8Ve6hSBx6c7ITFG_vltwK-db");
		
		
		System.out.println(notification.toString());
		pushManager.sendFcmNotification(notification);
	}

}
