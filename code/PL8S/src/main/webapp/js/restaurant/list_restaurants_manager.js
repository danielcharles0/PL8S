// Get user role
let role = JSON.parse(localStorage.getItem("pl8s-user")).role;

// if admin show also restaurant's manager email, i.e. add the column to the table
if(role === "admin") {

    let name_column = $(document.getElementById("name_column"));
    name_column.after("<th>Manager Email</th>");


    // remove add button (and its container)
    let button_container = document.getElementById("button_container");

    console.log("Removing add button and its container: ", button_container);

    button_container.remove();

} else {

    // add add_button listener
    let add_button = document.getElementById("add_button");
    add_button.addEventListener("click", goToAddRestaurant);

}

/**
 * Goes to edit-restaurant page.
 */
function goToEditRestaurant(){

    let res_id;

    // If coming from edit button
    if(this.class === "menu-item edit") {

        res_id = this.parentElement.parentElement.id;

    // else role = "admin" and coming from restaurant name button
    } else {

        res_id = this.parentElement.parentElement.parentElement.lastElementChild.id;

    }


    console.log("restaurant_id: " + res_id);

    window.location.href = `/pl8s/restaurant/${res_id}/edit`;

}

/**
 * Goes to list-dishes-manager page.
 */
function goToDishList(){

    // Get restaurant id exploiting the DOM tree structure
    let restaurant_id = this.parentElement.parentElement.parentElement.lastElementChild.id;

    console.log("Restaurant: " + restaurant_id);

    window.location.href = "/pl8s/dishes/manager/restaurant/" + restaurant_id;

}

/**
 * Deletes the selected restaurant.
 */
async function deleteRestaurant(){

    // Get restaurant id exploiting the DOM tree structure
    let res_id = this.parentElement.parentElement.id;

    console.log("Restaurant id: " + res_id);

    // Get restaurant name exploiting the DOM tree structure
    let res_name = this.parentElement.parentElement.parentElement.firstElementChild.firstElementChild.firstElementChild.textContent;

    console.log("Restaurant name: " + res_name);

    // Perform restaurant deletion here, asking for confirmation first
    if( confirm("Are you sure you want to delete restaurant \"" + res_name + "\"?") ){

        console.log("Sending HTTP DELETE request to delete restaurant.");

        let response = await fetch("/pl8s/rest/restaurant/delete/" + res_id, {
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

            let table = row.parentElement;

            table.removeChild(row);

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
        edit_buttons[i].addEventListener("click", goToEditRestaurant);

    }

    // Delete buttons functionality
    let delete_buttons = document.getElementsByClassName("menu-item delete");
    for(let i = 0; i < edit_buttons.length; i++){

        console.log(`Delete button ${i}:`, delete_buttons[i]);
        delete_buttons[i].addEventListener("click", deleteRestaurant);

    }

    // Restaurant name button functionality
    let restaurant_names = document.getElementsByClassName("name");
    for (let i = 0; i < restaurant_names.length; i++) {

        if(role === "manager")
            restaurant_names[i].addEventListener("click", goToDishList);
        else
            restaurant_names[i].addEventListener("click", goToEditRestaurant);

    }
}

/**
 * Retrieves restaurants owned by the logged-in manager (or all the restaurants if the admin is logged in)
 * and inserts them in the page.
 * @returns {Promise<void>}
 */
async function loadRestaurants() {

    console.log("Sending HTTP GET request to get restaurants.");

    let response = await fetch("/pl8s/rest/restaurants/manager", {
        headers:{
            "Authorization": getAuthToken()
        }
    });

    // Get response body as json
    let jsonBody = await response.json();

    console.log("Request sent successfully.\nResponse: ", jsonBody);

    if(response.ok) {

        // Get the restaurants container in the page
        let container = document.getElementById("table_body");

        // Clear all the elements in the container
        container.replaceChildren();

        let restaurants = jsonBody.restaurants;

        // For each restaurant build its HTML element and insert it in the container
        for (let i = 0; i < restaurants.length; i++) {

            let res = restaurants[i].restaurant;

            let row = "<tr>\n" +
                "                            <td class=\"flex-container\">\n" +
                "                                <div class=\"text-container\">\n" +
                "                                    <div class=\"name\">" + res.name + "</div>\n" +
                "                                    <div class=\"description\">" + res.description + "</div>\n" +
                "                                </div>\n" +
                "                            </td>\n" +
                (role === "admin" ? ("                            <td>" + res.manager_email + "</td>\n") : "") +
                "                            <td class=\"working-hours\">" + res.opening_at.slice(0, -3) + " - " + res.closing_at.slice(0, -3) + "</td>\n" +
                "                            <td class=\"option\" id=\"" + res.restaurant_id.toString() + "\">&#8942;\n" +
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

// Perform the retrieval of the restaurants when the page has just been loaded
loadRestaurants();
