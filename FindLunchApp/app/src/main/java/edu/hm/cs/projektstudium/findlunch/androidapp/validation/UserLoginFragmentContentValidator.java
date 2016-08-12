package edu.hm.cs.projektstudium.findlunch.androidapp.validation;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.hm.cs.projektstudium.findlunch.androidapp.data.UserLoginContent;


/**
 * The type User Login content validator
 * that validates the input of the
 * user at the user registration fragment.
 */
public class UserLoginFragmentContentValidator {

    /**
     * The constant VALID_USERNAME_PATTERN
     * that represents the pattern for a valid street.
     */
    private static final Pattern VALID_USERNAME_PATTERN = Pattern
            .compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    /**
     * The constant VALID_PASSWORD_PATTERN.
     * that represents the pattern for a valid street number.
     */
    private static final Pattern VALID_PASSWORD_PATTERN = Pattern
            .compile(".+", Pattern.CASE_INSENSITIVE);

    /**
     * The constant ATTRIBUTE_USERNAME.
     */
    private static final String ATTRIBUTE_USERNAME = "userName";
    /**
     * The constant ATTRIBUTE_PASSWORD.
     */
    private static final String ATTRIBUTE_PASSWORD = "password";
    /**
     * The constant ATTRIBUTE_USERNAME_INVALID.
     */
    private static final String ATTRIBUTE_USERNAME_INVALID = "userNameInvalid";
    /**
     * The constant ATTRIBUTE_USERNAME_BLANK.
     */
    private static final String ATTRIBUTE_USERNAME_BLANK = "userNameBlank";
    /**
     * The constant ATTRIBUTE_PASSWORD_INVALID.
     */
    private static final String ATTRIBUTE_PASSWORD_INVALID = "passwordInvalid";
    /**
     * The constant ATTRIBUTE_PASSWORD_BLANK.
     */
    private static final String ATTRIBUTE_PASSWORD_BLANK = "passwordBlank";

    /**
     * Method that validates the location contents.
     *
     * @param userRegistrationContent the location contents
     * @param validationError  the validation error
     */
    public void validate(UserLoginContent userRegistrationContent, ValidationError validationError) {
        checkUserName(userRegistrationContent, validationError);
        checkPassword(userRegistrationContent, validationError);
    }

    /**
     * Method that checks the validity of the username.
     *
     * @param userRegistrationContent the location contents
     * @param validationError  the validation error
     */
    private void checkUserName(UserLoginContent userRegistrationContent, ValidationError validationError) {
        if(!userNameIsNotBlank(userRegistrationContent.getUserName())) {
            validationError.rejectValue(ATTRIBUTE_USERNAME, ATTRIBUTE_USERNAME_BLANK);
        } else if(!validateUserName(userRegistrationContent.getUserName())) {
            validationError.rejectValue(ATTRIBUTE_USERNAME, ATTRIBUTE_USERNAME_INVALID);
        }
    }

    /**
     * Method that checks the validity of the password.
     *
     * @param userRegistrationContent the location contents
     * @param validationError  the validation error
     */
    private void checkPassword(UserLoginContent userRegistrationContent, ValidationError validationError) {
        if(!passwordIsNotBlank(userRegistrationContent.getPassword())) {
            validationError.rejectValue(ATTRIBUTE_PASSWORD, ATTRIBUTE_PASSWORD_BLANK);
        } else if(!validatePassword(userRegistrationContent.getPassword())) {
            validationError.rejectValue(ATTRIBUTE_PASSWORD, ATTRIBUTE_PASSWORD_INVALID);
        }
    }

    /**
     * Method that validates the userName.
     *
     * @param userName the userName
     * @return <code>true</code> if the userName is valid
     */
    private boolean validateUserName(String userName) {
        Matcher matcher = VALID_USERNAME_PATTERN.matcher(userName);
        return matcher.matches();
    }

    /**
     * Method that validates the password.
     *
     * @param password the password
     * @return <code>true</code> if the password is valid
     */
    private boolean validatePassword(String password) {
        Matcher matcher = VALID_PASSWORD_PATTERN.matcher(password);
        return matcher.matches();
    }

    /**
     * Method that checks if the userName is not blank.
     *
     * @param userName the userName
     * @return <code>true</code> if the userName is not blank
     */
    private boolean userNameIsNotBlank(String userName) {
        return !TextUtils.isEmpty(userName);
    }

    /**
     * Method that checks if the password is not blank.
     *
     * @param password the password
     * @return <code>true</code> if the password is not blank
     */
    private boolean passwordIsNotBlank(String password) {
        return !TextUtils.isEmpty(password);
    }
}
