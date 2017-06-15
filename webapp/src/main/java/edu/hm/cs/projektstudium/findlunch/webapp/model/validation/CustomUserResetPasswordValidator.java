package edu.hm.cs.projektstudium.findlunch.webapp.model.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserRepository;

@Component
public class CustomUserResetPasswordValidator implements Validator{

	/** The user repository. */
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return User.class.equals(clazz);
	}

	@Override
	public void validate(Object objectToValidate, Errors bindingResult) {
		User user = (User)objectToValidate;
		checkUsername(user, bindingResult);
		checkPassword(user, bindingResult);
	}
	
	/**
	 * Check password.
	 *
	 * @param user the user
	 * @param bindingResult the binding result
	 */
	private void checkPassword(User user, Errors bindingResult)
	{
		if (!user.getPassword().equals(user.getPasswordconfirm()))
			bindingResult.rejectValue("password", "user.passwordMismatch");
		if(!checkPasswordRules(user.getPassword()))
			bindingResult.rejectValue("password", "user.passwordRulesNotMatched");
	}
	
	private void checkUsername(User user, Errors bindingResult)
	{
		if (!validateEmail(user.getUsername()))
			bindingResult.rejectValue("username", "user.usernameInvalid");
		else if (userRepository.findByUsername(user.getUsername()) == null)
			bindingResult.rejectValue("username", "user.resetpw.usernameNotFound");
	}

	/**
	 * Check password rules.
	 *
	 * @param password the password
	 * @return true, if successful
	 */
	public static boolean checkPasswordRules(String password)
	{
		Matcher matcher = VALID_PASSWORD_REGEX.matcher(password);
		return matcher.find();
	}
	
	/** The Constant VALID_EMAIL_ADDRESS_REGEX. */
	private static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
			Pattern.CASE_INSENSITIVE);
	
	private static final Pattern VALID_PASSWORD_REGEX = Pattern.compile("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_]).{5,})");

	/**
	 * Validate email.
	 *
	 * @param emailStr the email str
	 * @return true, if successful
	 */
	public static boolean validateEmail(String emailStr) {
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
		return matcher.find();
	}
}
