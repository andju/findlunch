package edu.hm.cs.projektstudium.findlunch.webapp.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.mail.MailService;
import edu.hm.cs.projektstudium.findlunch.webapp.model.ResetPassword;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.model.validation.CustomUserResetPasswordValidator;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.ResetPasswordRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserRepository;

/**
 * The Class ResetPasswordController
 * 
 * @author Deniz Mardin
 *
 */
@Controller
public class ResetPasswordController {

	private final Logger LOGGER = LoggerFactory.getLogger(ResetPasswordController.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ResetPasswordRepository resetPasswordRepository;
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private CustomUserResetPasswordValidator customUserResetPasswordValidator;
	
	private static final String HTTP = "http://";
	
	private static final String HTTPS= "https://";
	
	@RequestMapping(path ="/resetpassword", method = RequestMethod.GET)
	public String getResetPassword(Model model, HttpServletRequest request){
		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		model.addAttribute("user", new User());
		
		return "resetpassword";
	}
	
	@RequestMapping(method = RequestMethod.POST, path="/resetpassword", params = "resetUserPassword")
	public String resetPassword(HttpServletRequest request, User user){
		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		User u = userRepository.findByUsername(user.getUsername());
		if(u == null){
			LOGGER.info(LogUtils.getErrorMessage(Thread.currentThread().getStackTrace()[1].getMethodName(),"Not existed Username(E-Mail) was entered."));
			//send always a success Mail because of IT-Sec reasons.Also add the difference of the needed time to send a Mail.
			return "redirect:/home?mailSend";
		}
		ResetPassword resetPasswordLast = resetPasswordRepository.findByUser(u);
		
		
		//try to reset pw more then one time in 24hours
		if(resetPasswordLast != null && validatePasswordDate(resetPasswordLast.getDate())){
			String resetLink = getPasswordResetUrl(request, u);
			try{
				mailService.sendResetPwMail(u,resetLink);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			return "redirect:/home?mailSend";
		}
		
		//first try to reset pw in 24h
		ResetPassword resetPassword = new ResetPassword();
		resetPassword.setDate(new Date());
		resetPassword.setToken(UUID.randomUUID().toString());
		resetPassword.setUser(u);
		u.setResetPassword(resetPassword);
		resetPasswordRepository.save(resetPassword);
		userRepository.save(u);
		String resetLink = getPasswordResetUrl(request, u);
		try{
			mailService.sendResetPwMail(u,resetLink);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return "redirect:/home?mailSend";
	}
	
	private String getPasswordResetUrl(HttpServletRequest request, User user) {
		String url = getProtocol(request.isSecure()) + request.getServerName()+":"+request.getServerPort()+"/resetpassword/"+user.getResetPassword().getToken();
		return url;
	}

	private boolean validatePasswordDate(Date dateToValidate){
		LocalDateTime dtv = LocalDateTime.ofInstant(dateToValidate.toInstant(), ZoneId.systemDefault());
		LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
		if (dtv.isAfter(yesterday))
			return true;		
		return false;
	}
	
	@RequestMapping(method = RequestMethod.POST, path="/resetpassword", params = "resetUserPasswordcancel")
	public String resetPasswordCancel(HttpServletRequest request, User user){
		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		return "redirect:/home";
	}
	
	@RequestMapping(path ="/resetpassword/{token}", method = RequestMethod.GET)
	public String setNewPassword(@PathVariable("token") String token, HttpServletRequest request, final Model model){
		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		ResetPassword resetPassword = resetPasswordRepository.findByToken(token);
		User user = userRepository.findOne(resetPassword.getUser().getId());
		
		model.addAttribute("user", user);
		model.addAttribute("resetPassword",resetPassword);
		
		return "resetpassword";
	}
	
	@RequestMapping(path="/resetpassword/{token}", method = RequestMethod.POST)
	public String saveNewPassword(@PathVariable("token") String token, HttpServletRequest request, User user, BindingResult bindingResult, final Model model){
		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		User userPW = userRepository.findOne(user.getId());
		
		userPW.setPassword(user.getPassword());
		userPW.setPasswordconfirm(user.getPasswordconfirm());
		customUserResetPasswordValidator.validate(userPW, bindingResult);
		userPW.setPassword(passwordEncoder.encode(user.getPassword()));
		userPW.setPasswordconfirm(passwordEncoder.encode(user.getPasswordconfirm()));
		
		ResetPassword resetPassword = resetPasswordRepository.findByToken(token);
		if (bindingResult.hasErrors()) {
			LOGGER.error(LogUtils.getValidationErrorString(request, bindingResult, Thread.currentThread().getStackTrace()[1].getMethodName()));
			
			model.addAttribute("user", user);
			model.addAttribute("resetPassword",resetPassword);
			return "resetpassword";
		}
		
		resetPasswordRepository.delete(resetPassword);
		userRepository.save(userPW);
		
		return "redirect:/home?resetSuccessful";
	}
	
	@RequestMapping(path="/resetpassword/{token}", method = RequestMethod.POST, params = "cancel")
	public String saveNewPasswordCancel(HttpServletRequest request, User user){
		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		return "redirect:/home?resetCancel";
	}
	
	private String getProtocol(boolean https){
		return https ? HTTPS : HTTP;
	}
}
