package edu.hm.cs.projektstudium.findlunch.webapp.logging;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

/**
 * The Class LogUtils - Utilities for the Logger
 */
public class LogUtils {
	
	/** The parameter to ignore. */
	private static List<String> parameterToIgnore = Arrays.asList("password", "passwordconfirm");
	
	/**
	 * Gets the default info string.
	 *
	 * @param request the request
	 * @param methodName the method name
	 * @return the default info string
	 */
	public static String getDefaultInfoString(HttpServletRequest request, String methodName) {
		
		StringBuilder builder = new StringBuilder();
		builder.append("Access in Method: " + methodName + ": ");
		builder.append("User: " + (request.getUserPrincipal() != null? request.getUserPrincipal().getName(): "anonymous"));
		builder.append(" requested " + request.getRequestURI());
		builder.append(" with request method " + request.getMethod());
		builder.append(". Current session: " + (request.getSession() != null? request.getSession().getId(): " none"));
		
		return builder.toString();
		
	}
	
	/**
	 * Gets the default info string with path variable.
	 *
	 * @param request the request
	 * @param methodName the method name
	 * @param nameOfPathVariable the name of path variable
	 * @param valueOfPathVariable the value of path variable
	 * @return the default info string with path variable
	 */
	public static String getDefaultInfoStringWithPathVariable(HttpServletRequest request, String methodName, String nameOfPathVariable, String valueOfPathVariable) {
		StringBuilder builder = new StringBuilder();
		builder.append(getDefaultInfoString(request, methodName));
		builder.append(" Received HTTP path variable: " + nameOfPathVariable);
		builder.append(" with value: " + valueOfPathVariable);
		return builder.toString();
	}
	
	/**
	 * Gets the parameter list from the HttpServletRequest.
	 *
	 * @param request the request
	 * @return the parameter list
	 */
	private static String getParameterList(HttpServletRequest request) {
	
		StringBuilder builder = new StringBuilder();
		for(String key : request.getParameterMap().keySet()) {
			
			if(!parameterToIgnore.contains(key))
			builder.append(key + " : " + (request.getParameter(key) != ""? request.getParameter(key): "[[empty]]") + " ");
			
		}
		
		return "Received HTTP parameter: " + builder.toString();
	}
	
	/**
	 * Gets the info string with parameter list.
	 *
	 * @param request the request
	 * @param methodName the method name
	 * @return the info string with parameter list
	 */
	public static String getInfoStringWithParameterList(HttpServletRequest request, String methodName) {
		
		return getDefaultInfoString(request, methodName) + " " + getParameterList(request);
		
	}
	
	/**
	 * Gets the cancel info string.
	 *
	 * @param request the request
	 * @param methodName the method name
	 * @return the cancel info string
	 */
	public static String getCancelInfoString(HttpServletRequest request, String methodName) {
		StringBuilder builder = new StringBuilder();
		builder.append("Cancel in Method: " + methodName + " (" + request.getRequestURI() + "): ");
		builder.append("Process for canceled by user.");
		
		return builder.toString();
	}
	
	/**
	 * Gets the validation error string.
	 *
	 * @param request the request
	 * @param bindingResult the binding result
	 * @param methodName the method name
	 * @return the validation error string
	 */
	public static String getValidationErrorString(HttpServletRequest request, BindingResult bindingResult, String methodName) {
		
		StringBuilder builder = new StringBuilder();
		builder.append("Validation Error in Method: " + methodName + " (" + request.getRequestURI() + "): ");
		for(FieldError fieldError : bindingResult.getFieldErrors()) {
			if(!parameterToIgnore.contains(fieldError.getField())) {
				builder.append("Error within field: " + fieldError.getField() + ". Rejected value: " + fieldError.getRejectedValue() + ". Error code: " + fieldError.getCode() + ". ");
			}
			else {
				builder.append("Error within field: " + fieldError.getField() + ". Error code: " + fieldError.getCode() + ". ");
			}
			
		}
		
		return builder.toString();
	}
	
	/**
	 * Gets the error message.
	 *
	 * @param request the request
	 * @param methodName the method name
	 * @param errorMessage the error message
	 * @return the error message
	 */
	public static String getErrorMessage(HttpServletRequest request, String methodName, String errorMessage)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Error in Method: " + methodName);
		if(request != null)
			builder.append("(" + request.getRequestURI() + ")");
		builder.append(" : ");
		builder.append(errorMessage);
		
		return builder.toString();
	}
	
	/**
	 * Gets the error message.
	 *
	 * @param methodName the method name
	 * @param errorMessage the error message
	 * @return the error message
	 */
	public static String getErrorMessage(String methodName, String errorMessage)
	{
		return getErrorMessage(null, methodName, errorMessage);
	}
	
	/**
	 * Gets the exception message.
	 *
	 * @param request the request
	 * @param methodName the method name
	 * @param exception the exception
	 * @return the exception message
	 */
	public static String getExceptionMessage(HttpServletRequest request, String methodName, Exception exception)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Exception in Method: " + methodName);
		if(request != null)
			builder.append("(" + request.getRequestURI() + ")");
		builder.append(" : ");
		builder.append(exception.getMessage());
		
		return builder.toString();
	}
	
	/**
	 * Gets the exception message.
	 *
	 * @param methodName the method name
	 * @param exception the exception
	 * @return the exception message
	 */
	public static String getExceptionMessage(String methodName, Exception exception)
	{
		return getExceptionMessage(null, methodName, exception);
	}
	
	/**
	 * Gets the default scheduler message.
	 *
	 * @param methodName the method name
	 * @param message the message
	 * @return the default scheduler message
	 */
	public static String getDefaultSchedulerMessage(String methodName, String message)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Scheduled Task in Method: " + methodName + ": ");
		builder.append(message);
		
		return builder.toString();
	}

}
