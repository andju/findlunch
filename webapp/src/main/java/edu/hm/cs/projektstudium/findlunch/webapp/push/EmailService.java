package edu.hm.cs.projektstudium.findlunch.webapp.push;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.plaf.synth.SynthSeparatorUI;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;



@Component
public class EmailService {

	@Autowired
	public JavaMailSender emailSender;
	
	@Autowired
	JavaMailSenderImpl mailSender;
	
	static Properties mailProps;
	static Session session;
	static MimeMessage mime;
	
	public void sendSimpleMessage(String to, String subject, String text) {
		
		SimpleMailMessage message = new SimpleMailMessage(); 
		message.setFrom("order.findlunch@gmail.com");
        message.setTo(to); 
        message.setSubject(subject); 
        message.setText(text);
        try{
        emailSender.send(message);
        } catch (MailException e) {
        	System.out.println(e.getMessage());
        }
	}
	
	public void mimMessage(String to, String subject, String text) throws AddressException, MessagingException {
		
		mailProps = System.getProperties();
		mailProps.put("mail.smtp.port", "587");
		mailProps.put("mail.smtp.auth", "true");
		mailProps.put("mail.smtp.starttls.enable", "true");
		
		session = Session.getDefaultInstance(mailProps, null);
		mime = new MimeMessage(session);
		mime.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		mime.setSubject(subject);
		mime.setContent(text, "text/html");
		
		Transport transport = session.getTransport("smtp");
		
		transport.connect("smtp.gmail.com", "order.findlunch@gmail.com", "hplvjtgpuzmgidcf");
		transport.sendMessage(mime, mime.getAllRecipients());
		transport.close();
	}
}
