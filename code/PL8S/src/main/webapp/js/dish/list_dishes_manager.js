/**
 * The function sets the restaurant name
 */
async function setRestaurantName(){

    let restaurant_id = getUrlParam("restaurant");

    if(restaurant_id === undefined) {
        raiseMessage("URL's parameter \"restaurant_id\" not found.");
        return;
    }

	const restaurant = await getRestaurant(restaurant_id);
	
	let restaurant_name = document.getElementById("restaurant_name");
	restaurant_name.textContent = restaurant.name;
}

// Setting restaurant name
setRestaurantName();

// add add_button listener
let add_button = document.getElementById("add_button");
add_button.addEventListener("click", () => { goToAddDish(getUrlParam("restaurant")); });

/**
 * Goes to dish statistics page.
 */
function goToDishStatistics(){

    // Get dish id exploiting the DOM tree structure
    let dish_id = this.parentElement.parentElement.parentElement.lastElementChild.id;

    console.log("Dish_id: " + dish_id);

    window.location.href = `/pl8s/dish/${dish_id}`;

}

/**
 * Goes to edit-dish page.
 */
function goToEditDish(){

    console.log("Id of dish to edit: " + this.parentElement.parentElement.id);

    window.location.href = "/pl8s/dish/" + this.parentElement.parentElement.id + "/edit";

}

/**
 * Deletes the selected dish.
 */
async function deleteDish(){

    // Get dish id exploiting the DOM tree structure
    let dish_id = this.parentElement.parentElement.id;

    console.log("Id of dish to delete: " + dish_id);

    // Get dish name exploiting the DOM tree structure
    let dish_name = this.parentElement.parentElement.parentElement.firstElementChild.firstElementChild.firstElementChild.textContent;

    console.log("Dish name: " + dish_name);

    // Perform dish deletion here, asking for confirmation first
    if( confirm("Are you sure you want to delete dish \"" + dish_name + "\"?") ){

        console.log("Sending HTTP DELETE request to delete dish.");

        let response = await fetch("/pl8s/rest/dish/" + dish_id, {
            method: "DELETE",
            headers: {
                "Authorization": getAuthToken()
            }
        });

        let jsonBody = await response.json();

        console.log("Request sent successfully.\nResponse: ", jsonBody);

        if(response.ok){

            // Delete this table row
            let row = this.parentElement.parentElement.parentElement;

            row.remove();

        } else {

            // Show error as popup
            raiseError(jsonBody.message);

        }


    }
}

/**
 * Adds buttons functionalities.
 */
function addButtonsListeners(){

    // Add an event listener to each of the option cells
    document.querySelectorAll('.option').forEach(function (optionCell) {

        optionCell.addEventListener('click', function (event) {

            const menu = this.querySelector('.menu');
            if (menu.style.display === 'block') {
                menu.style.display = 'none';
            } else {
                document.querySelectorAll('.menu').forEach(function (otherMenu) {
                    otherMenu.style.display = 'none';
                });
                menu.style.display = 'block';
            }
            event.stopPropagation();
        });
    });

    // Close all the menus when clicking on another part of the screen
    document.addEventListener('click', function () {
        document.querySelectorAll('.menu').forEach(function (menu) {
            menu.style.display = 'none';
        });
    });

    // Edit buttons functionality
    let edit_buttons = document.getElementsByClassName("menu-item edit");
    for(let i = 0; i < edit_buttons.length; i++){

        console.log(`Edit button ${i}:`, edit_buttons[i]);
        edit_buttons[i].addEventListener("click", goToEditDish);

    }

    // Delete buttons functionality
    let delete_buttons = document.getElementsByClassName("menu-item delete");
    for(let i = 0; i < edit_buttons.length; i++){

        console.log(`Delete button ${i}:`, delete_buttons[i]);
        delete_buttons[i].addEventListener("click", deleteDish);

    }

    // Dish name button functionality
    let dish_names = document.getElementsByClassName("name");
    for(let i = 0; i < dish_names.length; i++){

        dish_names[i].addEventListener("click", goToDishStatistics);

    }

}

/**
 * Retrieves dishes of the restaurant owned by the logged-in manager and inserts them in the page.
 * @returns {Promise<void>}
 */
async function loadDishes() {

    let restaurant_id = getUrlParam("restaurant");

    if(restaurant_id === undefined) {
        raiseMessage("URL's parameter \"restaurant_id\" not found.");
        return;
    }

    console.log(`Sending HTTP GET request to get restaurant ${restaurant_id}'s dishes.`);

    let response = await fetch("/pl8s/rest/dishes/restaurant_id/" + restaurant_id);

    // Get response body as json
    let jsonBody = await response.json();

    console.log("Request sent successfully.\nResponse: ", jsonBody);

    if(response.ok) {

        // Get the restaurants container in the page
        let container = document.getElementById("table_body");

        // Clear all the elements in the container
        container.replaceChildren();

        let dishes = jsonBody.dishes;

        // For each restaurant build its HTML element and insert it in the container
        for (let i = 0; i < dishes.length; i++) {

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

            let row = "<tr>\n" +
                "                            <td class=\"flex-container\">\n" +
                "                                <div class=\"text-container\">\n" +
                "                                    <div class=\"name\">" + dish.name + "</div>\n" +
                "                                    <div class=\"ingredients\">Ingredients: " + ingredients + "</div>\n" +
                "                                </div>\n" +
                "                            </td>\n" +
                "                            <td class=\"price\">" + parseFloat(dish.price).toFixed(2).toString() + "€</td>\n" +
                "                            <td class=\"diet\">" + dish.diet + "</td>\n" +
                "                            <td class=\"option\" id=\"" + dish.dish_id.toString() + "\">&#8942;\n" +
                "                                <div class=\"menu\">\n" +
                "                                    <div class=\"menu-item edit\">Edit</div>\n" +
                "                                    <div class=\"menu-item delete\">Delete</div>\n" +
                "                                </div>\n" +
                "                            </td>\n" +
                "                        </tr>";

            container.insertAdjacentHTML("beforeend", row);

        }

        addButtonsListeners();

    } else {

        // Show error as popup
        raiseError(jsonBody.message);

    }

}

// Perform the retrieval of the dishes when the page has just been loaded
loadDishes();
