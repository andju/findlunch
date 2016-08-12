package edu.hm.cs.projektstudium.findlunch.androidapp.validation;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Validation error
 * represents the errors that
 * occur during the validation.
 */
public class ValidationError {

    /**
     * The Errors.
     */
    private final Map<String, String> errors;

    /**
     * Instantiates a new Validation error.
     */
    public ValidationError() {
        errors = new HashMap<>();
    }

    /**
     * Returns <code>true</code> if errors exist.
     *
     * @return <code>true</code> if errors exist.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean hasErrors() {
        return errors.size() > 0;
    }

    /**
     * Method that allows to reject value
     * due to a given reason.
     *
     * @param field  the field
     * @param reason the reason
     */
    public void rejectValue(String field, String reason) {
        if(field != null && reason != null) {
            errors.put(field, reason);
        }
    }

    /**
     * Gets errors.
     *
     * @return the errors
     */
    public Map<String, String> getErrors() {
        return errors;
    }
}
