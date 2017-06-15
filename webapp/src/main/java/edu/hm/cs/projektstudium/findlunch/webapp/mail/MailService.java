package edu.hm.cs.projektstudium.findlunch.webapp.mail;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.ResetPasswordRepository;

@Service
public class MailService {

	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	ResetPasswordRepository resetPasswordRepository;

	public void sendResetPwMail(User user, String resetLink) throws MailException{
		SimpleMailMessage mail = configureMail(user, resetLink);
		javaMailSender.send(mail);
	}
	
	private SimpleMailMessage configureMail(User user, String resetLink){
		ResourceBundle messages = getResurceBundel();
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(user.getUsername());
		mail.setSubject(messages.getString("resetpassword.title"));
		String text = MessageFormat.format(messages.getString("resetpassword.supplier.text"), resetLink);
		mail.setText(text);
		return mail;
	}
	
	private ResourceBundle getResurceBundel(){
		Locale currentLocale;
        currentLocale = new Locale("de", "DE");
        ResourceBundle messages = ResourceBundle.getBundle("messages.resetpassword", currentLocale);
		return messages;
	}
	
	public void sendPasswordRestToken(User user){
		SimpleMailMessage mail = configureCustomerMail(user);
		javaMailSender.send(mail);
		
	}

	private SimpleMailMessage configureCustomerMail(User user) {
		ResourceBundle messages = getResurceBundel();
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(user.getUsername());
		mail.setSubject(messages.getString("resetpassword.title"));
		String text = MessageFormat.format(messages.getString("resetpassword.customer.text"), user.getResetPassword().getToken());
		mail.setText(text);
		return mail;
	}
}
