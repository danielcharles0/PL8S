/**
 * Date formatter
*/
const dateFormatter = Intl.DateTimeFormat(undefined, {day: "2-digit", month: "2-digit", year: "numeric"});

/**
 * Time formatter
 */
const timeFormatter = Intl.DateTimeFormat(undefined, {hour: '2-digit', minute: '2-digit'});
/**
 * Array containing all existing countries. It can be used for validation or to populate a select element
 * @type {string[]} Array containing all existing countries
 */
const allCountries = ['Afghanistan', 'Albania', 'Algeria', 'Andorra', 'Angola', 'Antigua and Barbuda',
    'Argentina', 'Armenia', 'Australia', 'Austria', 'Azerbaijan', 'Bahamas', 'Bahrain',
    'Bangladesh', 'Barbados', 'Belarus', 'Belgium', 'Belize', 'Benin', 'Bhutan', 'Bolivia',
    'Bosnia Herzegovina', 'Botswana', 'Brazil', 'Brunei', 'Bulgaria', 'Burkina', 'Burundi',
    'Cambodia', 'Cameroon', 'Canada', 'Cape Verde', 'Central African Republic', 'Chad',
    'Chile', 'China', 'Colombia', 'Comoros', 'Congo', 'Congo, Democratic Republic', 'Costa Rica',
    'Croatia', 'Cuba', 'Cyprus', 'Czech Republic', 'Denmark', 'Djibouti', 'Dominica',
    'Dominican Republic', 'East Timor', 'Ecuador', 'Egypt', 'El Salvador',
    'Equatorial Guinea', 'Eritrea', 'Estonia', 'Ethiopia', 'Fiji', 'Finland',
    'France', 'Gabon', 'Gambia', 'Georgia', 'Germany', 'Ghana', 'Greece', 'Grenada',
    'Guatemala', 'Guinea', 'Guinea-Bissau', 'Guyana', 'Haiti', 'Honduras', 'Hungary',
    'Iceland', 'India', 'Indonesia', 'Iran', 'Iraq', 'Ireland', 'Israel',
    'Italy', 'Ivory Coast', 'Jamaica', 'Japan', 'Jordan', 'Kazakhstan', 'Kenya',
    'Kiribati', 'Kosovo', 'Kuwait', 'Kyrgyzstan', 'Laos', 'Latvia', 'Lebanon', 'Lesotho',
    'Liberia', 'Libya', 'Liechtenstein', 'Lithuania', 'Luxembourg', 'Macedonia',
    'Madagascar', 'Malawi', 'Malaysia', 'Maldives', 'Mali', 'Malta', 'Marshall Islands',
    'Mauritania', 'Mauritius', 'Mexico', 'Micronesia', 'Moldova', 'Monaco', 'Mongolia',
    'Montenegro', 'Morocco', 'Mozambique', 'Myanmar', 'Namibia', 'Nauru', 'Nepal',
    'Netherlands', 'New Zealand', 'Nicaragua', 'Niger', 'Nigeria', 'North Korea', 'Norway',
    'Oman', 'Pakistan', 'Palau',  'Panama', 'Papua New Guinea', 'Paraguay', 'Peru',
    'Philippines', 'Poland', 'Portugal', 'Qatar', 'Romania', 'Russian Federation', 'Rwanda',
    'Saint Kitts and Nevis', 'St Lucia', 'Saint Vincent & the Grenadines', 'Samoa',
    'San Marino', 'Sao Tome and Principe', 'Saudi Arabia', 'Senegal', 'Serbia', 'Seychelles',
    'Sierra Leone', 'Singapore', 'Slovakia', 'Slovenia', 'Solomon Islands', 'Somalia',
    'South Africa', 'South Korea', 'South Sudan', 'Spain', 'Sri Lanka', 'Sudan', 'Suriname',
    'Swaziland', 'Sweden', 'Switzerland', 'Syria', 'Taiwan', 'Tajikistan', 'Tanzania',
    'Thailand', 'Togo', 'Tonga', 'Trinidad and Tobago', 'Tunisia', 'Turkey', 'Turkmenistan',
    'Tuvalu', 'Uganda', 'Ukraine', 'United Arab Emirates', 'United Kingdom', 'United States',
    'Uruguay', 'Uzbekistan', 'Vanuatu', 'Vatican City', 'Venezuela', 'Vietnam', 'Yemen',
    'Zambia', 'Zimbabwe'];


/**
 * It fills an input_select element with all values contained by the array passed as a parameter.
 * @param input_select the document element of the type select to be populated
 * @param array the array containing the values to assign to the select options
 * @param optionName the name for the options
 */
function fillSelect(input_select , array, optionName){
    for (let i = 0; i < array.length; i++) {
        let value = array[i];
        let option = document.createElement("option");
        option.text = value;
        option.name = optionName;
        option.value = value;
        input_select.appendChild(option);
    }

}
/**
 * Checks whether the stored JWT is expired.
 * @returns {boolean} true if expired token, false otherwise.
 */
function tokenExpired () {

    let exp_date = localStorage.getItem("pl8s-jwt-exp");

    if (exp_date != null) {

        let now = new Date();

        return (now.getTime() - Number(exp_date)) > 0;
    } else {
        return true;
    }

}

/**
 * Raises the error as popup.
 * @param message error message
 */
function raiseError(message){

    alert(message.message + "\nError: " + message["error-code"] + (message["error-details"] === undefined ? "" : ("\n" + message["error-details"])));

}

/**
 * The function will alert with the desired message.
 * @param {String} message to be showed.
 */
function raiseMessage(message){
    alert(message);
}

/**
 * Raises the error as popup.
 * @param message error message
 */
function raiseErrorAndLog(message){
    const ErrorMessage = message.message + "\nError: " + message["error-code"] + (message["error-details"] === undefined ? "" : ("\n" + message["error-details"]));
    console.log(ErrorMessage);
    alert (ErrorMessage);
}

/**
 * Basic email validator.
 * @param email element containing the email.
 * @returns {boolean} true if valid email, false otherwise.
 */
function validateEmail(email) {

    return /^(.+)@(\S+)$/.test(email.value);

}

/**
 * Password validator.
 * @param password element containing the password.
 * @returns {boolean} true if valid password, false otherwise.
 */
function validatePassword(password){

    return /^(?=.*[a-z])(?=.*[0-9])(?=.*[^\w\*])[^\s]{6,20}$/.test(password.value);

}

/**
 * Shows password written in an input box of type 'password'.
 * @param password element containing the password to be put in-clear or hide.
 */
function showHidePassword(password){

    if (password.type === "password") {
        password.setAttribute("type", "text");
    } else {
        password.setAttribute("type", "password");
    }

}

/**
 * It returns the query parameters of the current page
 * @returns {URLSearchParams} the query parameters of the current page
 */
function getUrlParameters(){
    const params = new Proxy(new URLSearchParams(window.location.search), {
        get: (searchParams, prop) => searchParams.get(prop),
    });
    return params;
}

/**
 * Redirects the user to the previous page on the search history or to the homepage,
 * storing the current page in the search history.
 */
function redirectBackOrHome() {

    // Get the value of "return_url" in e.g. "https://example.com/?return_url=some_value"
    let value = getUrlParameters.return_url; // "some_value"

    if(value != null){
        //if there is a back page then go to it
        window.location.href = value;
    } else {
        //if there is no back page then go to homepage, i.e. restaurants page
        window.location.href = "/pl8s/restaurant/search";
    }

}

/**
 * Redirects the user to the previous page on the search history or to the homepage,
 * without storing the current page in the search history.
 */
function redirectBackOrHome_NoHistory(role) {

    // Get the value of "return_url" in e.g. "https://example.com/?return_url=some_value"
    let value = getUrlParameters().return_url; // "some_value"

    if(value != null && role === "customer"){
        //if there is a back page and the user is a customer then go to it
        window.location.replace(value);
    } else {
        //if there is no back page then go to homepage w.r.t. user role
        switch (role) {
            case "customer":
                window.location.replace("/pl8s/restaurants");
                break;
            default:
                window.location.replace("/pl8s/restaurants/manager");
                break;
        }

    }

}

/* Functions to be called on click of navbar elements */

/**
 * Redirects to the homepage storing the current page in search history.
 */
function goToHome() {
    window.location.href = "/pl8s/restaurants";
}

/**
 * Logs out the user.
 */
function logout() {

    let expiration = new Date();

    localStorage.removeItem("pl8s-jwt");
    localStorage.setItem("pl8s-jwt-exp", expiration.getTime().toString());

    // Redirect to home page without storing this page in search history
    window.location.replace("/pl8s/restaurants");

}

/**
 * Goes to login page, setting the current page as redirection when the user logged in successfully.
 */
function goToLogin(){

    let path = window.location.pathname;

    window.location.href = "/pl8s/user/login?return_url=" + path;

}

/**
 * Goes to list of restaurants for admin and managers.
 */
function goToRestaurants(){
    window.location.href = "/pl8s/restaurants/manager";
}

/**
 * Goes to list of users for admin.
 */
function goToUsers() {
    window.location.href = "/pl8s/user/list";
}

/**
 * Goes to cart of the logged-in user.
 */
function goToCart() {
    window.location.href = "/pl8s/order/cart";
}

/**
 * Goes to list of previous orders of the logged-in user.
 */
function goToOrders() {
    window.location.href = "/pl8s/order/list";
}

/**
 * Goes to user profile of the logged-in user.
 */
function goToUserProfile() {
    window.location.href = "/pl8s/user";
}

/**
 * Goes to the add-restaurant page.
 */
function goToAddRestaurant() {
    window.location.href = "/pl8s/restaurant/add";
}

/**
 * Goes to the add-dish page.
 */
function goToAddDish(restaurant_id) {
    window.location.href = "/pl8s/dish/add/restaurant/" + restaurant_id;
}

/**
 * The function returns a random value in the range [min, max - 1]
 * @param {Number} min 
 * @param {Number} max 
 * @returns random value in the range [min, max - 1]
 */
function getRandom(min, max) {

	let interval = (max - min);
	return min + Math.floor(Math.random() * interval);

}

/**
 * The function will remove trailing and multiple spaces.
 * @param {String} txt to clarify
 * @returns clarified text
 */
function clarifyText(txt){
	return txt.trim().replace(/\s+/g, ' ');
}

/**
 * @returns auth token
 */
function getAuthToken(){
    return "Bearer " + localStorage.getItem("pl8s-jwt");
}

/**
 * The function returns the restaurant($restaurant_id) name
 * @param {Number} restaurant_id
 * @returns {Restaurant} restaurant($restaurant_id)
 */
async function getRestaurant(restaurant_id){

    console.log(`Retrieving the restaurant(${restaurant_id})`);

    const url = "/pl8s/rest/restaurant/" + restaurant_id;

    const response = await fetch(url);

    if(!response.ok){
        const { message } = await response.json();
        console.log(`Error while retrieving the restaurant(${restaurant_id}).`);
        console.log(message);
        raiseError(message);
        return undefined;
    }

    const { restaurant } = await response.json();

    console.log(`Restaurant(${restaurant_id}) successfully retrieved`);

    return restaurant;
}

/**
 * The function will check the response
 * @param {Response} response
 * @param {String} errorMessage
 * @returns {Boolean} true if the response is returned correctly, false otherwise.
 */
async function checkResponse(response, errorMessage){

	if(!response.ok){

		const { message } = await response.json();

		console.log(errorMessage);
		console.log(message);

		raiseError(message);
	}

	return response.ok;
}

/**
 * The function will return the value of the param
 * @param {String} url
 * @param {String} param name of the param
 * @param {Function} type casting function
 * @returns {type} param casted. NaN if a Number not castable or undefined if the param does not exist.
 */
function parseParam(url, param, type = Number){

	const tokens = url.split('/');

	const idx = tokens.indexOf(param);

	if(idx == -1 || idx + 1 == tokens.length)
		return undefined;

	return type(tokens[idx + 1]);
}

/**
 * The function will return the value of the URL param
 * @param {String} param name of the param
 * @param {Function} type casting function
 */
function parseURLParam(param, type = Number){
	return parseParam(window.location.pathname, param, type);
}

/**
 * Gets url-parameter's value if present, otherwise returns undefined.
 * @param param url-parameter to be returned the value of
 * @returns {undefined|string} value associated to param, undefined otherwise
 */
function getUrlParam(param){

    let path = window.location.pathname.split("/");

    for(let i = 0; i < path.length; i++){

        if(path[i] === param)
            return path[i+1];

    }

    return undefined;

}