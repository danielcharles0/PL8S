//contains the authentication token
let token = getAuthToken();

/**
 * get the restaurant id for the restaurant to edit/delete
 */
let restaurantId = parseURLParam("restaurant");

/**
 * div element that contains all cuisine checkboxes to be loaded dynamically
 * @type {HTMLElement}
 */
let div_cuisine_types = document.getElementById("div_cuisine_types");
/**
 * div element that contains all country checkboxes to be loaded dynamically
 * @type {HTMLElement}
 */
let div_countries = document.getElementById("div_countries");
/**
 * The input element containing the restaurant name
 * @type {HTMLElement}
 */
let input_restaurant_name = document.getElementById("input_restaurant_name");
/**
 * The input element containing the opening hour
 * @type {HTMLElement}
 */
let input_opening_hour = document.getElementById("input_opening_hour");

/**
 * The input element containing the closing hour
 * @type {HTMLElement}
 */
let input_closing_hour = document.getElementById("input_closing_hour");

/**
 * The input select element containing the manager emails
 * @type {HTMLElement}
 */
let select_manager_email = document.getElementById("select_manager_email");
/**
 * The input element containing the restaurant description
 * @type {HTMLElement}
 */
let input_restaurant_description = document.getElementById("input_restaurant_description");
/**
 * The button used to add a new restaurant
 * @type {HTMLElement}
 */
let add_button = document.getElementById("add_button");
/**
 * The button used to save (update) the data about this restaurant
 * @type {HTMLElement}
 */
let save_button = document.getElementById("save_button");
/**
 * The button used to delete all the data about a restaurant
 * @type {HTMLElement}
 */
let delete_button = document.getElementById("delete_button")
/**
 * The button used to add a cuisine type checked checkbox
 * @type {HTMLElement}
 */
let add_type_button = document.getElementById("add_type_button");
/**
 * The button used to add a country checked checkbox
 * @type {HTMLElement}
 */
let add_country_button = document.getElementById("add_country_button");
/**
 * The input element used to insert the value for a new cuisine type to be added to a checkbox
 * @type {HTMLElement}
 */
let input_cuisine_type = document.getElementById("input_cuisine_type");

/**
 * The input select element containing all the countries in the world
 * @type {HTMLElement}
 */
let select_country = document.getElementById("select_country");

/**
 * contains all emails and user id associations. It is needed to get the manager ID but knowing just the manager email.
 * This is done to avoid interrogating the database again
 * @type {Map<any, any>}
 */
let usersMap = new Map();
let managerId;

/**
 * This function is called after a load event. It populates some or all the element present in the page
 */
function loadPage(){
    if (user_role==="customer"){
        const message= "Sorry but customers cannot edit restaurants";
        console.log(message);
        alert(message);
        redirectBackOrHome(user_role);
    //no parameter has been provided, so we do not know what to edit
    }
    // if it has a save_button then it is an edit-restaurant page  and not an add-restaurant page
    if (document.getElementById("save_button")){
        if (restaurantId==null){
            console.log(restaurantId);
            alert("No restaurant ID has been provided");
            redirectBackOrHome();
            return;
        }
        populateFormfields();
        // this is an add-restaurant page
    }else if(user_role!=="manager"){
        const message= "Only managers can add restaurants!";
        console.log(message);
        alert("Only managers can add restaurants!");
        redirectBackOrHome(user_role);
        return;
    }
    fillSelect(select_country, allCountries, "country");

}


/**
* It fetches all the data from a restaurants and loads it into the input elements present in the page
*/
let populateFormfields = async () => {
    const url = "/pl8s/rest/restaurant/" + restaurantId;
    const method = "GET";
    // perform the request and get response
    console.log("Sending HTTP request to get all information about the restaurant with ID: %d.", restaurantId);
    console.log ("Request URL: %s" , url);
    console.log("Performing the HTTP %s request", method);
    const response = await fetch(url);
    console.log("HTTP %s request sent.", method);
    // Get response body as json
    let jsonBody = await response.json();
    if(response.ok) {
        // Extract the restaurant object\
        let restaurant = jsonBody.restaurant;
        console.log("Filling all input elements with the restaurant data...");
        input_restaurant_name.value = restaurant.name;
        input_opening_hour.value = restaurant.opening_at;
        input_closing_hour.value = restaurant.closing_at;
        input_restaurant_description.value  = restaurant.description;
        managerId = restaurant.manager;
        //Creating checkboxes:checked with all countries belonging to this restaurant
        let countries = restaurant["countries"];
        for (let i = 0; i < countries.length; i++) {
            // extract the i-th country  and populate the select dropdown
            let country = countries[i];
            if (country!== null) {
                const newDiv = document.createElement('div');
                let htmlContent = '<div class ="checkboxes-pair">';
                htmlContent += '<input type="checkbox" checked="checked" id="check_country' + i + '" name="country" value="' + country + '">';
                htmlContent += '<label for="check_country' + i + '"' + '>' + country + '</label>';
                htmlContent += '</div>';
                newDiv.innerHTML = htmlContent;
                div_countries.append(newDiv);
            }
        }
        //Creating checkboxes:checked with all cuisine types this restaurant can provide
        let cuisine_types = restaurant["cuisine_types"];
        for (let i = 0; i < cuisine_types.length; i++) {
            // extract the i-th cuisine type and populate the select dropdown
            let type = cuisine_types[i];
            if (type!=null) {
                const newDiv = document.createElement('div');

                let htmlContent = '<div class ="checkboxes-pair">';
                htmlContent += '<input type="checkbox" checked="checked" id="check_type' + i + '" name="type" value="' + type + '">';
                htmlContent += '<label for="check_type' + i + '"' + '>' + type + '</label>';
                htmlContent += '</div>';
                newDiv.innerHTML = htmlContent;
                div_cuisine_types.append(newDiv);
            }
        }
         /* Only admins can change the manager associated to a restaurant. So we provide a select filled will all
         * the managers' email
         */
        if(user_role==="admin") {
            fillManagersEmails();
        }
        console.log("HTTP %s request successfully performed and processed.", method);
    } else {
        // Show error as popup and log it
        console.log("Request unsuccessful: HTTP status = %d.", response.status);
        raiseErrorAndLog(jsonBody.message);
    }
}

/**
* This function fetches all the users present in the database and then adds the manager emails in a select box
* @returns {Promise<void>}
*/
let fillManagersEmails = async () => {
    const url = "/pl8s/rest/user/list";
    const method = "GET";

    console.log("Sending HTTP request to get the emails for all the managers in the database", restaurantId);
    console.log ("Request URL: %s", url);
    console.log("Performing the HTTP %s request", method);
    // perform the request and get response
    const response = await fetch(url, {
            method: method,
            headers: {
                "Content-Type": "application/json",
                "Authorization": token
            },
        }
    );
    console.log("HTTP %s request sent.", method);
    // Get response body as json
    let jsonBody = await response.json();
    console.log("this is the response body: ");
    console.log(jsonBody);

    if(response.ok) {
        const users = jsonBody.users;
        $(".div_manager_email").css("display","flex");

        for (let i = 0; i < users.length; i++) {
            // extract the i-th email and populate the select dropdown
            let user =users[i]["user"];
            let email = user.email;
            if (user.role==="manager") {
                let option = document.createElement("option");
                option.text = email;
                option.name = "email";
                option.value = email;
                usersMap.set(email, user.user_id);
                if (user.user_id  === managerId) {
                    option.selected = "selected";

                }
                select_manager_email.appendChild(option);
            }
        }
        console.log("HTTP %s request successfully performed and processed.", method);
    } else {
        // Show error as popup and log it
        console.log("Request unsuccessful: HTTP status = %d.", response.status);
        raiseErrorAndLog(jsonBody.message);
    }

}
/**
 * Before adding a new value to a checkbox, this function checks whether the value already exists
 * @param optionsList an array containing oll the values that need to be unique
 * @param valueToInsert the value to check if it exists in the array
 * @returns {boolean} true if the value exists; false otherwise
 */
function isCheckboxAlreadyAvailable(optionsList, valueToInsert){
    for (i = 0; i<optionsList.length; ++i){
        if (optionsList[i] === valueToInsert){
            alert ("The value " + valueToInsert+ " is already in the list");
            return true;
        }
    }
    return false;
}

/**
 * Creates a new checked checkbox with the value of input_cuisine_type.
 * It does nothing if the input element is empty or contains only whitespaces
 */
function addCuisineType() {
    const newtype = input_cuisine_type.value.toLowerCase();
    const available_types = Array.apply(null, document.querySelectorAll('div.checkboxes_cuisine_types input[type="checkbox"]:checked')).map(function(el){return el.value;});
    if(newtype!==null && newtype.trim()!=="" && !isCheckboxAlreadyAvailable(available_types, newtype)){
        const newDiv = document.createElement('div');
        let htmlContent = '<div class ="checkboxes-pair">';
        htmlContent += '<input type="checkbox" checked="checked" id="check_type'+ '" name="type" value="'+newtype+'">';
        htmlContent += '<label for="check_type'+ '"'+'>'+newtype+'</label>' ;
        htmlContent += '</div>';
        newDiv.innerHTML = htmlContent;
        div_cuisine_types.append(newDiv);

    }
    input_cuisine_type.value="";
}

/**
 * Creates a new checked checkbox with the value of select_country.
 * It does nothing if the input select element has no selected options
 */
function addCountry() {
    const newCountry = select_country.value;
    const available_countries= Array.apply(null, document.querySelectorAll('div.checkboxes_countries input[type="checkbox"]:checked')).map(function(el){return el.value;});
    if(newCountry!==null && newCountry.trim()!=="" && !isCheckboxAlreadyAvailable(available_countries, newCountry)){
        const newDiv = document.createElement('div');
        let htmlContent = '<div class ="checkboxes-pair">';
        htmlContent += '<input type="checkbox" checked="checked" id="check_country'+ '" name="country" value="'+newCountry+'">';
        htmlContent += '<label for="check_country'+ '"'+'>'+newCountry+'</label>';
        htmlContent += '</div>';
        newDiv.innerHTML = htmlContent;
        div_countries.append(newDiv);
    }
    // After adding a country we select a no value option
    select_country.getElementsByTagName('option')[0].selected = 'selected';
}


/**
 * It updates the Restaurant with the values filled in the input elements of the page
 * @returns {Promise<void>}
 */
let saveRestaurant = async () => {
    validateTimeFields();
    addCuisineType();
    addCountry();
    // build request body
    let restaurantBody = JSON.stringify({
        "restaurant": {
            "name": input_restaurant_name.value,
            "description": input_restaurant_description.value,
            "opening_at": input_opening_hour.value,
            "closing_at": input_closing_hour.value,
            //if the user is a manager the entry manager will be taken from the header
            "manager": (user_role==="admin") ? usersMap.get(select_manager_email.value): managerId,
            "cuisine_types": Array.apply(null, document.querySelectorAll('div.checkboxes_cuisine_types input[type="checkbox"]:checked')).map(function(el){return el.value;}),
            "countries": Array.apply(null, document.querySelectorAll('div.checkboxes_countries input[type="checkbox"]:checked')).map(function(el){return el.value;})

        }
    });

    const url = "/pl8s/rest/restaurant/update/"+ restaurantId;
    const method = "PUT";
    // perform the request and get response
    console.log("Sending HTTP request update the data the user inserted/modified for the restaurant with ID:", restaurantId);
    console.log ("Request URL: %s", url);
    console.log ("Body: %s", restaurantBody);
    console.log("Performing the HTTP %s request", method);
    const response = await fetch(
        url, {
            method: method,
            headers: {
                "Content-Type": "application/json",
                "Authorization": token
            },
            body: restaurantBody
        }
    );
    console.log("HTTP %s request sent.", method);
    // get response body as json
    let jsonBody = await response.json();
    console.log("The response is:");

    if (response.ok) {
        // show that the request was successfully satisfied
        console.log("HTTP %s request successfully performed and processed.", method);
        alert("Restaurant successfully updated.");
        redirectBackOrHome_NoHistory(user_role);

    } else {
        //  show error as popup
        console.log("Request unsuccessful: HTTP status = %d.", response.status);
        raiseErrorAndLog(jsonBody.message);
    }

}

/**
 * It checks if the time fileds present in the form are empty and fires an alert message
 * @returns {boolean} true if the time fields are empty, false otherwise
 */
function validateTimeFields(){
    if(input_opening_hour.value==="" || input_closing_hour.value==="") {
        alert("Opening and closing hours are mandatory");
        return false;
    }else {
        return true;
    }
}

/**
 * It adds a new Restaurant with the values filled in the input elements of the page
 * @returns {Promise<void>}
 */
let addRestaurant = async () => {
    // time validation
    if(!validateTimeFields()) {
        return;
    }
    addCuisineType();
    addCountry();
    // build request body
    let restaurantBody = JSON.stringify({
        "restaurant": {
            "name": input_restaurant_name.value,
            "description": input_restaurant_description.value,
            "opening_at": input_opening_hour.value+ ":00",
            "closing_at": input_closing_hour.value+ ":00",
            "cuisine_types": Array.apply(null, document.querySelectorAll('div.checkboxes_cuisine_types input[type="checkbox"]:checked')).map(function(el){return el.value;}),
            "countries": Array.apply(null, document.querySelectorAll('div.checkboxes_countries input[type="checkbox"]:checked')).map(function(el){return el.value;})

        }
    });
    const url = "/pl8s/rest/restaurant/create";
    const method = "POST";
    // perform the request and get response
    console.log("Sending HTTP request create a new restaurant according to the data inserted by the manager");
    console.log ("Request URL: %s", url);
    console.log("Body: %s", restaurantBody );
    console.log("Performing the HTTP %s request", method);
    const response = await fetch(url, {
            method: method,
            headers: {
                "Content-Type": "application/json",
                "Authorization": token
            },
            body: restaurantBody
        }
    );

    // get response body as json
    let jsonBody = await response.json();

    if (response.ok) {
        // show that the request was successfully satisfied
        console.log("HTTP %s request successfully performed and processed.", method);
        alert("Restaurant successfully created.");
        // let's clear all the form elements so that a new restaurant can be added
        input_restaurant_name.value = "";
        input_restaurant_description.value = "";
        input_opening_hour.value = "";
        input_closing_hour.value = "";
        div_cuisine_types.innerHTML = "";
        div_countries.innerHTML = "";
        input_cuisine_type.value = "";
        select_country.getElementsByTagName('option')[0].selected = 'selected';
    } else {
        //  show error as popup and also log it
        console.log("Request unsuccessful: HTTP status = %d.", response.status);
        raiseErrorAndLog(jsonBody.message);
    }

}
/* Event listeners  */
window.addEventListener ("load", loadPage);
add_button?.addEventListener("click", addRestaurant);
add_type_button.addEventListener("click", addCuisineType);
add_country_button.addEventListener("click", addCountry);
add_country_button.addEventListener("click", addCountry);
delete_button?.addEventListener('click', deleteRestaurant(restaurantId));
save_button?.addEventListener("click", saveRestaurant);
add_type_button.addEventListener("click", addCuisineType);
add_country_button.addEventListener("click", addCountry);
// Execute a function when the user presses a key on the keyboard
select_country.addEventListener("keypress", function(event) {
    // If the user presses the "Enter" key on the keyboard
    if (event.key === "Enter") {
        // Cancel the default action, if needed
        event.preventDefault();
        // Trigger the button element with a click
        addCountry();
    }
});

// Executes the addCuisineType function when ENTER is pressed on the input select element
input_cuisine_type.addEventListener("keypress", function(event) {
    // If the user presses the "Enter" key on the keyboard
    if (event.key === "Enter") {
        // Cancel the default action, if needed
        event.preventDefault();
        // Trigger the button element with a click
        addCuisineType();
    }
});