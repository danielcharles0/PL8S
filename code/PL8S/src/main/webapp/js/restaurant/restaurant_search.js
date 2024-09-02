// Getting search params
let restaurant_name = document.getElementById("search-box");
let select_cuisine = document.getElementById("select_cuisine_type");


/**
 * Lists all cusine types available in the database .
 *
 * @returns {boolean} true if the HTTP request was successful; false otherwise.
 */
function loadCuisineTypes() {

        const url = "/pl8s/rest/cuisine/types";
        console.log("Request URL: %s.", url)

        // the XMLHttpRequest object
        const xhr = new XMLHttpRequest();

        if (!xhr) {
            console.log("Cannot create an XMLHttpRequest instance.")

            alert("Giving up :( Cannot create an XMLHttpRequest instance");
            return false;
        }

        // set up the call back for handling the request
        xhr.onreadystatechange = function () {
            processResponse(this);
        };

        // perform the request
        console.log("Performing the HTTP GET request.");

        xhr.open("GET", url, true);
        xhr.send();

        console.log("HTTP GET request sent.");
}

/**
 * Processes the HTTP response and fills the input select element options with the retreived data.
 *
 * @param xhr the XMLHttpRequest object performing the request.
 */
function processResponse(xhr) {

    // not finished yet
    if (xhr.readyState !== XMLHttpRequest.DONE) {
        console.log("Request state: %d. [0 = UNSENT; 1 = OPENED; 2 = HEADERS_RECEIVED; 3 = LOADING]",
            xhr.readyState);
        return;
    }

    if (xhr.status !== 200) {
        console.log("Request unsuccessful: HTTP status = %d.", xhr.status);
        console.log(xhr.response);

        return;
    }

    // parse the response as JSON and extract the cuisinetypes array
    let cuisinetypes = JSON.parse(xhr.responseText)["cuisinetypes"]

    console.log(cuisinetypes);


    for (let i = 0; i < cuisinetypes.length; i++) {
        // extract the i-th cuisine type and populate the select dropdown
        let type = cuisinetypes[i]["CuisineType"].type;
        if (type != null) {
            let option = document.createElement("option");
            option.text = type;
            option.value = type;
            select_cuisine.appendChild(option);
        }
    }
    console.log("HTTP GET request successfully performed and processed.");

}

/**
 * Goes to restaurant page of the clicked restaurant.
 */
function goToRestaurantMenu(){

    window.location.href = "/pl8s/dishes/restaurant/" + this.getAttribute("id");

}

/**
 * Gets restaurants and inserts them in the DOM tree.
 * @returns {Promise<void>}
 */
async function search() {

    let response;

    console.log("Sending HTTP GET request to get restaurants.");

    if(restaurant_name.value.length === 0){

        if(select_cuisine.value.length === 0){

            // Performed initially when the page has just been loaded and the search elements are still empty
            response = await fetch("/pl8s/rest/restaurants");

        } else {

            // Search with only cuisine type specified
            response = await fetch("/pl8s/rest/restaurants/cuisine_type/" + select_cuisine.value.toString());

        }

    } else {

        if(select_cuisine.value.length === 0){

            // Search with only restaurant name specified
            response = await fetch("/pl8s/rest/restaurants/name/" + restaurant_name.value.toString());

        } else {

            // Search with both cuisine type and restaurant name specified
            response = await fetch("/pl8s/rest/restaurants/name/" + restaurant_name.value.toString() +
                "/cuisine_type/" + select_cuisine.value.toString());

        }

    }

    // Get response body as json
    let jsonBody = await response.json();

    console.log("Request sent successfully.\nResponse: ", jsonBody);

    if(response.ok) {

        // Get the restaurants container in the page
        let container = document.getElementById("card_container");

        // Clear all the elements in the container
        container.replaceChildren();

        let restaurants = jsonBody.restaurants;

        // For each restaurant build its HTML element and insert it in the container
        for(let i = 0; i < restaurants.length; i++){

            let res = restaurants[i].restaurant;

            // Create the countries string
            let countries = "";

            // Concatenate the countries
            for(let j = 0; j < res.countries.length; j++) {

                if(res.countries[j] == null)
                    continue;

                countries = countries + res.countries[j] + ", ";

            }

            // Remove last two characters, i.e. ", "
            countries = countries.substring(0, countries.length - 2);

            // Create the cuisine string
            let cuisines = "";

            // Concatenate the cuisine_types
            for(let j = 0; j < res.cuisine_types.length; j++) {

                if(res.cuisine_types[j] == null)
                    continue;

                cuisines = cuisines + res.cuisine_types[j] + ", ";

            }

            // Remove last two characters, i.e. ", "
            cuisines = cuisines.substring(0, cuisines.length - 2);

            // Insert the restaurant properties into the html format
            let card = "<div class=\"card\">\n" +
                "                    <div class=\"card-content\">\n" +
                "                        <p class=\"card-title-restaurant\" id=\"" + res.restaurant_id.toString() + "\">" + res.name + "</p>\n" +
                "                        <p class=\"card-hours\">Working hours: " + res.opening_at.slice(0, -3) + " - " + res.closing_at.slice(0,-3) + "</p>\n" +
                "                        <p class=\"card-countries\">Countries: " + countries + "</p>\n" +
                "                        <p class=\"card-cuisines\">Cuisines: " + cuisines + "</p>\n" +
                "                    </div>\n" +
                "                    <p class=\"card-description\">\"" + res.description + "\"</p>\n" +
                "                </div>";

            // Insert the restaurant element in the container
            container.insertAdjacentHTML("beforeend", card);

        }

        // Add event listener to each of the restaurants just added
        let restaurant_buttons = document.getElementsByClassName("card-title-restaurant");
        for(let i = 0; i < restaurant_buttons.length; i++){

            restaurant_buttons[i].addEventListener("click", goToRestaurantMenu);

        }

    } else {

        // Show error as popup
        raiseError(jsonBody.message);

    }

}

// Perform the search when the page has just been loaded to get all the restaurants
search();

// Perform the retrieval of all the cuisine types when the page has just been loaded
loadCuisineTypes();

// Add event listener to the search button
let search_button = document.getElementById("search_button");
search_button.addEventListener("click", search);

// Add event listener to the sort button
let sort_button = document.getElementById("sort_button");
sort_button.addEventListener("click", sort_elements);

// Add event listener to the input when "Enter" is pressed
restaurant_name.addEventListener("keypress", (e) => {
    if(e.key === "Enter")
        search();
});
