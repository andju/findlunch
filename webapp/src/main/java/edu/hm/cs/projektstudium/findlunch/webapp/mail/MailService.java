package edu.hm.cs.projektstudium.findlunch.webapp.mail;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import edu.hm.cs.projektstudium.findlunch.webapp.model.Reservation;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.ResetPasswordRepository;

/**
 * The class MailService
 * The class is responsible for sending emails to the customers and restaurants
 * 
 * @author Deniz Mardin, Niklas Klotz
 *
 */
@Service
public class MailService {

	/** The MailSender */
	@Autowired
	private JavaMailSender javaMailSender;
	
	/** The resetpassword repository*/
	@Autowired
	ResetPasswordRepository resetPasswordRepository;
	
	/**
	 * Sends a mail for a new reservation
	 * @param user the restaurant
	 * @param reservatin the reservation
	 * @param url the url to the reservation
	 */
	public void sendNewReservationMail(User user, Reservation reservatin, String url) {
		SimpleMailMessage mail = configureReservtionMail(user, reservatin, url);
		javaMailSender.send(mail);
	}
	
	/**
	 * Sends a mail to reset the password
	 * @param user
	 * @param resetLink
	 * @throws MailException
	 */
	public void sendResetPwMail(User user, String resetLink) throws MailException{
		SimpleMailMessage mail = configurePasswordMail(user, resetLink);
		javaMailSender.send(mail);
	}
	
	/**
	 * Builds the content of a new reservation mail
	 * @param user the restaurant
	 * @param reservation the reservation
	 * @param url the url to the reservation
	 * @return
	 */
	private SimpleMailMessage configureReservtionMail(User user, Reservation reservation, String url) {
		ResourceBundle messages = getResurceBundel();
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(user.getUsername());
		mail.setSubject(messages.getString("reservation.title"));
		String text = MessageFormat.format(messages.getString("reservation.text"), url);
		mail.setText(text);
		return mail;
		
	}
	
	/**
	 * Builds a password mail
	 * @param user
	 * @param resetLink
	 * @return
	 */
	private SimpleMailMessage configurePasswordMail(User user, String resetLink){
		ResourceBundle messages = getResurceBundel();
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(user.getUsername());
		mail.setSubject(messages.getString("resetpassword.title"));
		String text = MessageFormat.format(messages.getString("resetpassword.supplier.text"), resetLink);
		mail.setText(text);
		return mail;
	}
	
	/**
	 * Gets the rescources for the mail
	 * @return
	 */
	private ResourceBundle getResurceBundel(){
		Locale currentLocale;
        currentLocale = new Locale("de", "DE");
        ResourceBundle messages = ResourceBundle.getBundle("messages.email", currentLocale);
		return messages;
	}
	
	/**
	 * Sets the token for the password reset
	 * @param user
	 */
	public void sendPasswordRestToken(User user){
		SimpleMailMessage mail = configureCustomerPasswordMail(user);
		javaMailSender.send(mail);
		
	}

	/**
	 * Builds a reset mail for a customer
	 * @param user the customer
	 * @return the mail 
	 */
	private SimpleMailMessage configureCustomerPasswordMail(User user) {
		ResourceBundle messages = getResurceBundel();
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(user.getUsername());
		mail.setSubject(messages.getString("resetpassword.title"));
		String text = MessageFormat.format(messages.getString("resetpassword.customer.text"), user.getResetPassword().getToken());
		mail.setText(text);
		return mail;
	}
}
