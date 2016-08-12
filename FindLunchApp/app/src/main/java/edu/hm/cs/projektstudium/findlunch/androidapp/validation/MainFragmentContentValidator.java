package edu.hm.cs.projektstudium.findlunch.androidapp.validation;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.hm.cs.projektstudium.findlunch.androidapp.data.UserLocationContent;


/**
 * The type Main fragment content validator
 * that validates the input of the
 * user at the main fragment.
 */
public class MainFragmentContentValidator {

    /**
     * The constant VALID_STREET_PATTERN
     * that represents the pattern for a valid street.
     */
    private static final Pattern VALID_STREET_PATTERN = Pattern
            .compile("([ÖöÄäÜüßA-Z]+[- ._]?)*", Pattern.CASE_INSENSITIVE);

    /**
     * The constant VALID_STREET_NUMBER_PATTERN.
     * that represents the pattern for a valid street number.
     */
    private static final Pattern VALID_STREET_NUMBER_PATTERN = Pattern
            .compile("[1-9]{1}[\\d]{0,5}[a-z.]?(-[\\d]{1,6}[a-z.]?)?", Pattern.CASE_INSENSITIVE);

    /**
     * The constant VALID_ZIP_PATTERN
     * that represents the pattern for a valid zip.
     */
    private static final Pattern VALID_ZIP_PATTERN= Pattern
            .compile("^([0]{1}[1-9]{1}|[1-9]{1}[0-9]{1})[0-9]{3}$");

    /**
     * The constant VALID_DISTANCE_PATTERN
     * that represents the pattern for a valid distance.
     */
    private static final Pattern VALID_DISTANCE_PATTERN = Pattern
            .compile("[0-9]+");

    /**
     * The constant ATTRIBUTE_STREET.
     */
    public static final String ATTRIBUTE_STREET = "street";
    /**
     * The constant ATTRIBUTE_STREET_NUMBER.
     */
    public static final String ATTRIBUTE_STREET_NUMBER = "streetNumber";
    /**
     * The constant ATTRIBUTE_ZIP.
     */
    public static final String ATTRIBUTE_ZIP = "zip";
    /**
     * The constant ATTRIBUTE_DISTANCE.
     */
    private static final String ATTRIBUTE_DISTANCE = "distance";

    /**
     * The constant ATTRIBUTE_STREET_INVALID.
     */
    public static final String ATTRIBUTE_STREET_INVALID = "streetInvalid";
    /**
     * The constant ATTRIBUTE_STREET_BLANK.
     */
    public static final String ATTRIBUTE_STREET_BLANK = "streetBlank";
    /**
     * The constant ATTRIBUTE_STREET_NUMBER_INVALID.
     */
    public static final String ATTRIBUTE_STREET_NUMBER_INVALID = "streetNumberInvalid";
    /**
     * The constant ATTRIBUTE_STREET_NUMBER_BLANK.
     */
    public static final String ATTRIBUTE_STREET_NUMBER_BLANK = "streetNumberBlank";
    /**
     * The constant ATTRIBUTE_ZIP_INVALID.
     */
    public static final String ATTRIBUTE_ZIP_INVALID = "zipInvalid";
    /**
     * The constant ATTRIBUTE_ZIP_BLANK.
     */
    public static final String ATTRIBUTE_ZIP_BLANK = "zipBlank";
    /**
     * The constant ATTRIBUTE_DISTANCE_INVALID.
     */
    private static final String ATTRIBUTE_DISTANCE_INVALID = "distanceInvalid";
    /**
     * The constant ATTRIBUTE_DISTANCE_BLANK.
     */
    private static final String ATTRIBUTE_DISTANCE_BLANK = "distanceBlank";

    /**
     * Method that validates the location content.
     *
     * @param userLocationContent the location content
     * @param validationError  the validation error
     */
    public void validate(UserLocationContent userLocationContent, ValidationError validationError) {
        checkStreet(userLocationContent, validationError);
        checkStreetNumber(userLocationContent, validationError);
        checkZip(userLocationContent, validationError);
        checkDistance(userLocationContent, validationError);
    }

    /**
     * Method that checks the validity of the street.
     *
     * @param userLocationContent the location content
     * @param validationError  the validation error
     */
    private void checkStreet(UserLocationContent userLocationContent, ValidationError validationError) {
        if(!streetIsNotBlank(userLocationContent.getStreet())) {
            validationError.rejectValue(ATTRIBUTE_STREET, ATTRIBUTE_STREET_BLANK);
        } else if(!validateStreet(userLocationContent.getStreet())) {
            validationError.rejectValue(ATTRIBUTE_STREET, ATTRIBUTE_STREET_INVALID);
        }
    }

    /**
     * Method that checks the validity of the street number.
     *
     * @param userLocationContent the location content
     * @param validationError  the validation error
     */
    private void checkStreetNumber(UserLocationContent userLocationContent, ValidationError validationError) {
        if(!streetNumberIsNotBlank(userLocationContent.getStreetNumber())) {
            validationError.rejectValue(ATTRIBUTE_STREET_NUMBER, ATTRIBUTE_STREET_NUMBER_BLANK);
        } else if(!validateStreetNumber(userLocationContent.getStreetNumber())) {
            validationError.rejectValue(ATTRIBUTE_STREET_NUMBER, ATTRIBUTE_STREET_NUMBER_INVALID);
        }
    }

    /**
     * Method that checks the validity of the zip.
     *
     * @param userLocationContent the location content
     * @param validationError  the validation error
     */
    private void checkZip(UserLocationContent userLocationContent, ValidationError validationError) {
        if(!zipIsNotBlank(userLocationContent.getZip())) {
            validationError.rejectValue(ATTRIBUTE_ZIP, ATTRIBUTE_ZIP_BLANK);
        } else if(!validateZip(userLocationContent.getZip())) {
            validationError.rejectValue(ATTRIBUTE_ZIP, ATTRIBUTE_ZIP_INVALID);
        }
    }

    /**
     * Method that checks the validity of the distance.
     *
     * @param userLocationContent the location content
     * @param validationError  the validation error
     */
    private void checkDistance(UserLocationContent userLocationContent, ValidationError validationError) {
        if(!distanceIsNotBlank(userLocationContent.getDistance())) {
            validationError.rejectValue(ATTRIBUTE_DISTANCE, ATTRIBUTE_DISTANCE_BLANK);
        } else if(!validateDistance(userLocationContent.getDistance())) {
            validationError.rejectValue(ATTRIBUTE_DISTANCE, ATTRIBUTE_DISTANCE_INVALID);
        }
    }

    /**
     * Method that validates the street.
     *
     * @param street the street
     * @return <code>true</code> if the street is valid
     */
    private boolean validateStreet(String street) {
        Matcher matcher = VALID_STREET_PATTERN.matcher(street);
        return matcher.matches();
    }

    /**
     * Method that validates the street number.
     *
     * @param streetNumber the street number
     * @return <code>true</code> if the street number is valid
     */
    private boolean validateStreetNumber(String streetNumber) {
        Matcher matcher = VALID_STREET_NUMBER_PATTERN.matcher(streetNumber);
        return matcher.matches();
    }

    /**
     * Method that validates the zip.
     *
     * @param zip the zip
     * @return <code>true</code> if the zip is valid
     */
    private boolean validateZip(String zip) {
        Matcher matcher = VALID_ZIP_PATTERN.matcher(zip);
        return matcher.matches();
    }

    /**
     * Method that validates the distance.
     *
     * @param distance the distance
     * @return <code>true</code> if the distance is valid
     */
    private boolean validateDistance(String distance) {
        Matcher matcher = VALID_DISTANCE_PATTERN.matcher(distance);
        return matcher.matches();
    }

    /**
     * Method that checks if the street is not blank.
     *
     * @param street the street
     * @return <code>true</code> if the street is not blank
     */
    private boolean streetIsNotBlank(String street) {
        return !TextUtils.isEmpty(street);
    }

    /**
     * Method that checks if the street number is not blank.
     *
     * @param streetNumber the street number
     * @return <code>true</code> if the street number is not blank
     */
    private boolean streetNumberIsNotBlank(String streetNumber) {
        return !TextUtils.isEmpty(streetNumber);
    }

    /**
     * Method that checks if the zip is not blank.
     *
     * @param zip the zip
     * @return <code>true</code> if the zip is not blank
     */
    private boolean zipIsNotBlank(String zip) {
        return !TextUtils.isEmpty(zip);
    }

    /**
     * Method that checks if the distance is not blank.
     *
     * @param distance the distance
     * @return <code>true</code> if the distance is not blank
     */
    private boolean distanceIsNotBlank(String distance) {
        return !TextUtils.isEmpty(distance);
    }
}
