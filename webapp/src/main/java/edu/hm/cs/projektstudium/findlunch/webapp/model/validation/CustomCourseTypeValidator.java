package edu.hm.cs.projektstudium.findlunch.webapp.model.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import edu.hm.cs.projektstudium.findlunch.webapp.model.CourseTypes;

/**
 * The class CustomCourseTypeValidator.
 */
@Component
public class CustomCourseTypeValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		return CourseTypes.class.equals(clazz);
	}

	@Override
	public void validate(Object objectToValidate, Errors bindingResult) {
		
		CourseTypes courseTypes = (CourseTypes) objectToValidate;
		checkName(courseTypes.getName(), bindingResult);
	}

	/**
	 * Check name.
	 *
	 * @param name the name
	 * @param bindingResult the binding result
	 */
	private void checkName(String name, Errors bindingResult) {
		if (!validateName(name))
			bindingResult.rejectValue("name", "courseType.name.NameInvalid");
			System.out.println("Binding: "+bindingResult.getAllErrors().toString());
	}
	
	/** The Constant VALID_NAME_PATTERN. */
	private static final Pattern VALID_NAME_PATTERN = Pattern.compile("([ÖöÄäÜüßA-Z0-9]+[,&() ]*)*",
			Pattern.CASE_INSENSITIVE);
	
	/**
	 * Validate name.
	 *
	 * @param name the name
	 * @return true, if successful
	 */
	private boolean validateName(String name) {
		Matcher matcher = VALID_NAME_PATTERN.matcher(name);
		return matcher.matches();
	}

}
