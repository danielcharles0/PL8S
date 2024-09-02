const restaurant_id = parseURLParam("restaurant");

/**
 * The function will load the restaurant name into the page
 */
async function loadRestaurantName(){

	const restaurant = await getRestaurant(restaurant_id);

	document.getElementById("restaurant_name").textContent = restaurant.name;
}

loadRestaurantName();

// Getting search params
let dish_name = document.getElementById("search-box");

/**
 * Gets the quantity of the dish in the cart.
 * @param dish_id id of the dish we want the quantity of in the cart
 * @returns {Promise<number>}
 */
async function getDishQuantity(dish_id) {

    console.log("Sending HTTP GET request to get dishes in cart.");

    const response = await fetch("/pl8s/rest/order", {
        method: "GET",
        headers: {
            "Authorization": getAuthToken()
        }
    });

    let jsonBody = await response.json();

    console.log("Request sent successfully.\nResponse: ", jsonBody);

    if(response.ok){

        let dishes = jsonBody.order.dishes;
        let quantity = 0;

        for(let i = 0; i < dishes.length; i++){

            let dish = dishes[i];

            // leave double equal otherwise it does not work
            if(dish_id == dish.dish_id){
                quantity = dish.quantity;
                break;
            }

        }

        return quantity;

    } else {

        // Show error as popup
        raiseError(jsonBody.message);

        return -1;

    }

}

/**
 * Adds the dish to the cart setting its quantity to 1.
 */
async function addToCart(){

    if(tokenExpired()){
        goToLogin();
        return; // avoids browser to come back here and go on with the execution
    }

    // Get dish_id
    let dish_id = this.id;

    // Get dish quantity
    let quantity = await getDishQuantity(dish_id);

    // There was an error while getting dishes in the cart
    if( quantity === -1 )
        return;

    // Maximum quantity already reached
    if( quantity >= 30 ){
        alert("Maximum quantity reached for this dish.");
        return;
    }

    // The dish is not present in the cart
    if( quantity === 0) {

        console.log(`Sending HTTP POST request to add dish ${dish_id} to cart.`);

        // Add the dish to cart with quantity 1
        let response = await fetch("/pl8s/rest/order/dishes/" + dish_id, {
            method: "POST",
            headers: {
                "Content-type": "application/json",
                "Authorization": getAuthToken()
            }
        });

        let jsonBody = await response.json();

        console.log("Request sent successfully.\nResponse: ", jsonBody);

        if (response.ok) {

            alert("Dish added to cart successfully.");

        } else {

            // Show error as popup
            raiseError(jsonBody.message);

        }

    // The dish is present in the cart
    } else {

        // Adds one to the current dish quantity
        let plusone = quantity + 1;

        console.log(`Sending HTTP PUT request to update dish ${dish_id} quantity in cart.`);

        // Update dish quantity
        const response = await fetch(
            "/pl8s/rest/order/dishes/" + dish_id, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": getAuthToken()
                },
                body: JSON.stringify({
                    quantity: plusone
                })
            }
        );

        let jsonBody = await response.json();

        console.log("Request sent successfully.\nResponse: ", jsonBody);

        if(response.ok){

            alert("Quantity modified correctly.");

        } else {

            // Show error as popup
            raiseError(jsonBody.message);

        }

    }
}

/**
 * Gets dishes and inserts them in the DOM tree.
 * @returns {Promise<void>}
 */
async function search() {

    let response;

    let selected_diet = document.querySelector("input[name=diet]:checked").value;

    console.log("Selected diet: ", selected_diet);

    console.log(`Sending HTTP GET request to get restaurant ${restaurant_id}'s dishes.`);

    if(dish_name.value.length === 0){

        if(selected_diet === "all"){

            // Performed initially when the page has just been loaded and the search elements are still empty
            response = await fetch("/pl8s/rest/dishes/restaurant_id/" + restaurant_id);

        } else {

            // Search with only diet specified
            response = await fetch("/pl8s/rest/dishes/restaurant_id/" + restaurant_id + "/diet/" + selected_diet);

        }

    } else {

        if(selected_diet === "all"){

            // Search with only dish name specified
            response = await fetch("/pl8s/rest/dishes/restaurant_id/" + restaurant_id + "/name/" + dish_name.value);

        } else {

            // Search with both diet and dish name specified
            response = await fetch("/pl8s/rest/dishes/restaurant_id/" + restaurant_id + "/name/" + dish_name.value
                + "/diet/" + selected_diet);

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

        let dishes = jsonBody.dishes;

        // For each restaurant build its HTML element and insert it in the container
        for(let i = 0; i < dishes.length; i++){

            let dish = dishes[i].dish;

            // Create the ingredients string
            let ingredients = "";

            // Concatenate the ingredients
            for(let j = 0; j < dish.ingredients.length; j++) {

                if(dish.ingredients[j] == null)
                    continue;

                ingredients = ingredients + dish.ingredients[j].ingredient.name + ", ";

            }

            // Remove last two characters, i.e. ", "
            ingredients = ingredients.substring(0, ingredients.length - 2);

            // Insert the restaurant properties into the html format
            let card = "<div class=\"card\">\n" +
                "                    <div class=\"card-content\">\n" +
                "                        <div class=\"card-title-dish\">\n" +
                "                           <p id=\"dish_name\">" + dish.name + "</p>\n" +
                "                           <p id=\"dish_price\">" + parseFloat(dish.price).toFixed(2).toString() + "€</p>\n" +
                "                        </div>\n" +
                "                        <p class=\"card-diet\">Diet: " + dish.diet + "</p>\n" +
                "                        <p class=\"card-ingredients\">Ingredients: " + ingredients + "</p>\n" +
                "                    </div>\n" +
                "                    <input type=\"button\" id=\"" + dish.dish_id.toString() + "\" class=\"add-to-cart-button\" value=\"Add To Cart\"/>\n" +
                "                </div>";

            // Insert the restaurant element in the container
            container.insertAdjacentHTML("beforeend", card);

        }

        // Add event listener to each of the dish just added
        let dish_buttons = document.getElementsByClassName("add-to-cart-button");
        for(let i = 0; i < dish_buttons.length; i++){

            dish_buttons[i].addEventListener("click", addToCart);

        }

    } else {

        // Show error as popup
        raiseError(jsonBody.message);

    }

}

// Perform the search when the page has just been loaded to get all the dishes
search();

// Add event listener to the search button
let search_button = document.getElementById("search_button");
search_button.addEventListener("click", search);

// Add event listener to the sort button
let sort_button = document.getElementById("sort_button");
sort_button.addEventListener("click", sort_elements);

// Add event listener to the input when "Enter" is pressed
dish_name.addEventListener("keypress", (e) => {
    if(e.key === "Enter")
        search();
});


