package it.unipd.dei.webapp.wa001.resource;

/**
 * Contains the REST APIs Error code constants
 */
public class ErrorCodes {

	/**
	 * Generic database error.
	 */
    public static final String UNEXPECTED_DB_ERROR = "ED00";
	/**
	 * Error while parsing parameters.
	 */
    public static final String INVALID_INPUT_PARAMETER = "EP00";
	/**
	 * Error while parsing unknown resource
	 */
	public static final String UNKNOWN_RESOURCE = "EP01";
	/**
	 * Unexpected error while processing the REST resource
	 */
	public static final String UNEXPECTED_ERROR = "EP02";
	/**
	 * Error code while parsing an unrecognized URI pattern
	 */
	public static final String UNRECOGNIZED_URI_PATTERN = "EP03";
	/**
	 * REST method not allowed while processing the REST resource
	 */
	public static final String UNSUPPORTED_OPERATION = "EP04";
	/**
	 * Error while contacting stripe.
	 */
	public static final String UNEXPECTED_STRIPE_ERROR = "ES00";

//	 public static final String CREATE_STRIPE_CUSTOMER_ERROR = "ES01";

	// public static final String WRONG_PASSWORD = "EU00";

	/**
	 * Error while encoding the user password.
	 */
    public static final String PASSWORD_ENCODING_ERROR = "EU00";
	
	/**
	 * Error code for Unspecified media type
	 */
	public static final String UNSPECIFIED_MEDIA_TYPE = "EM00";
	/**
	 * Error code for unsupported input media type
	 */
	public static final String UNSUPPORTED_INPUT_MEDIA_TYPE = "EM01";
	/**
	 * Error code for unsupported output media type
	 */
	public static final String UNSUPPORTED_OUTPUT_MEDIA_TYPE = "EM02";
	/**
	 * Error code for unauthenticated user
	 */
	public static final String UNAUTHENTICATED_USER = "EF00";
	/**
	 * Error code for unauthorized user
	 */
	public static final String UNAUTHORIZED_USER = "EF01";
}
