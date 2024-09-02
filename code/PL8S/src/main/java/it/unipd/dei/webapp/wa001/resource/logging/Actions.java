package it.unipd.dei.webapp.wa001.resource.logging;

/**
 * Contains constants for the actions performed by the application.
 */
public class Actions {

	/**
	 * The application connects to the database connection pool
	 */
	public static final String DATABASE_CONNECTION = "CONNECTION_TO_THE_DATABASE_CONNECTIONS_POOL";

	/**
	 * The application releases the database connection
	 */
	public static final String DATABASE_CONNECTION_RELEASE = "DATABASE_CONNECTION_RELEASE";
	
	/**
	 * List user credit cards action
	 */
	public static final String LIST_USER_CARDS = "LIST_USER_CREDIT_CARDS";

	/**
	 * List festival restaurants action
	 */
    public static final String LIST_RESTAURANT = "LIST_RESTAURANT";

	/**
	 * List restaurant dishes action
	 */
	public static final String LIST_DISHES = "LIST_DISHES";

	/**
	 * List users action
	 */
	public static final String LIST_USERS = "LIST_USERS";

	/**
	 * Register user to the platform action
	 */
    public static final String REGISTER_USER = "REGISTER_USER";

	/**
	* The application authenticate the request through a filter
	*/
	public static final String AUTHENTICATE_USER = "AUTHENTICATE_USER";

	/**
	* The application authenticate the webhook request through a filter
	*/
	public static final String AUTHENTICATE_STRIPE_WEBHOOK = "AUTHENTICATE_STRIPE_WEBHOOK";

	/**
	 * Login user to the platform action. Releases JWT tokens.
	 */
    public static final String LOGIN_USER = "LOGIN_USER";

	/**
	 * Logout user to the platform action. Releases invalid JWT tokens.
	 */
    public static final String LOGOUT_USER = "LOGOUT_USER";

	/**
	 * Update user information action
	 */
    public static final String UPDATE_USER = "UPDATE_USER";

	/**
	 * List previous orders action
	 */
    public static final String LIST_PREVIOUS_ORDERS = "LIST_PREVIOUS_ORDERS";

	/**
	 * Retreive information about a specific order action
	 */
    public static final String LIST_DISHES_FROM_ORDER = "LIST_DISHES_FROM_ORDER";

	/**
	 * Add dish to the cart action
	 */
    public static final String ADD_DISH_TO_ORDER = "ADD_DISH_TO_ORDER";

	/**
	 * Update cart dish quantity action
	 */
    public static final String UPDATE_DISH_QUANTITY = "UPDATE_DISH_QUANTITY";

	/**
	 * Delete dish from the cart action
	 */
    public static final String DELETE_DISH_FROM_ORDER = "DELETE_DISH_FROM_ORDER";

	/**
	 * Delete all dishes from the cart action
	 */
	public static final String DELETE_ALL_DISHES_FROM_ORDER = "DELETE_ALL_DISHES_FROM_ORDER";

	/**
	 * Complete order from the cart action
	 */
	public static final String COMPLETE_ORDER = "COMPLETE_ORDER";

	/**
	 * Create payment for the cart order action
	 */
	public static final String CREATE_PAYMENT = "CREATE_PAYMENT";

	/**
	 * Delete a user from the platform action
	 */
    public static final String DELETE_USER = "DELETE_USER";

	/**
	 * Create restaurant in the platform action
	 */
	public static final String CREATE_RESTAURANT = "CREATE_RESTAURANT";

	/**
	 * Update restaurant from the platform action
	 */
	public static final String UPDATE_RESTAURANT = "UPDATE_RESTAURANT";

	/**
	 * Delete restaurant from the platform action
	 */
    public static final String DELETE_RESTAURANT = "DELETE_RESTAURANT";

	/**
	 * List restaurants by name and cuisine action
	 */
    public static final String SEARCH_RESTAURANT_BY_NAME_BY_CUISINE = "SEARCH_RESTAURANT_BY_NAME_BY_CUISINE";

	/**
	 * List restaurant dishes by name and diet action
	 */
    public static final String SEARCH_DISHES_BY_RESTAURANT_BY_NAME_BY_DIET = "SEARCH_DISHES_BY_RESTAURANT_BY_NAME_BY_DIET";

	/**
	 * Create dish action
	 */
	public static final String CREATE_DISH = "CREATE_DISH";

	/**
	 * Update dish action
	 */
	public static final String UPDATE_DISH = "UPDATE_DISH";

	/**
	 * Delete dish from the restaurant menu action
	 */
	public static final String DELETE_DISH = "DELETE_DISH";

	/**
	 * List dish orders action
	 */
	public static final String DISH_LIST_ORDERS = "DISH_LIST_ORDERS";

	/**
	 * List festival cuisine types action
	 */
    public static final String LIST_CUISINE_TYPES = "LIST_CUISINE_TYPES";

	/**
	 * Search registered users by email action
	 */
    public static final String SEARCH_USER_BY_EMAIL = "SEARCH_USER_BY_EMAIL";

	/**
	 * Selects a dish from the database.
	 */
	public static final String SELECT_DISH = "SELECT_DISH";

    /**
     * This class can be neither instantiated nor sub-classed.
     */
    private Actions() {
        throw new AssertionError(String.format("No instances of %s allowed.", Actions.class.getName()));
    }

}
