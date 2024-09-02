// Retrieve authorization token
let token = getAuthToken();

// List of [dish_id, quantity, price] for every dish to be displayed
let dishesList = [];

// Price multiplier to turn floats (max 2 digits after comma) into integers
const PRICE_MULTIPLIER = 100;

// Number of items in the cart (quantities are not counted, just different dishes)
let numItems = 0;

// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
// DISHES NAME LINKS
// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

/**
 * Goes to restaurant menu page of the clicked dish.
 * @param event current event
 */
function goToRestaurantMenu(event){

    // Find article container of the dish's restaurant
    const itemDiv = event.target.closest('.item');
    const article = itemDiv.parentElement;

    // Use a RegEx to match any sequence of digits (\d+)
    const match = article.id.match(/\d+/);

    // If there was not a match, return
    if (!match) {
        console.log("Restaurant article id not valid.");
        return;
    }

    // Extract id from matched RegEx
    const restaurant_id = match[0];

    // Find restaurant name
    const restaurant_name = article.querySelector(".section_title").textContent;

    window.location.href = "/pl8s/dishes/restaurant/" + restaurant_id;

}

// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
// QUANTITY BUTTONS
// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

// Constants and variables for quantity values
const MAX_QUANTITY = 30;
const MIN_QUANTITY = 1;
let totalPriceElement = null;

/**
 * Change quantity of an item on the screen.
 * @param event current event
 * @param dish_index index of the dish in the dishesList array
 * @param operation specifies the operation to be done: addition ("add") or subtraction ("subtract")
 */
function changeQuantity(event, dish_index, operation) {

    // Find div container of the quantity buttons
    const itemDiv = event.target.closest('.item');

    // Find price paragraph in the item div
    const itemPriceParagraph = itemDiv.querySelector(".item_price");

    // Find text paragraph in which the quantity is stored and parse it into an integer
    const quantityCounterParagraph = itemDiv.querySelector('.quantity_counter');

    let quantityValue = dishesList[dish_index][1];
    const priceValue = dishesList[dish_index][2];

    // If operation is "add" the sign is +1, otherwise in the "subtract" case the sign is -1
    const operationSign = (operation === "add") ? 1 : -1;

    // Boolean values to determine whether addition and subtraction operations are valid
    const validAddition = (operation === 'add' && quantityValue < MAX_QUANTITY);
    const validSubtraction = (operation === 'subtract' && quantityValue > MIN_QUANTITY);

    // Apply changes to quantity and prices
    if( validAddition || validSubtraction ){
        quantityValue += operationSign;
        quantityCounterParagraph.innerText = quantityValue;
        itemPriceParagraph.innerText = ((quantityValue * priceValue) / PRICE_MULTIPLIER).toFixed(2);
        itemPriceParagraph.innerText += "$";
        dishesList[dish_index][1] = quantityValue;
        dishesList[0][2] += operationSign * priceValue;
        totalPriceElement.innerText = (dishesList[0][2] / PRICE_MULTIPLIER).toFixed(2);
        totalPriceElement.innerText += "$";
    }
}

/**
 * Send request to update the dish quantity.
 * @param dish_index index of the dish in the dishesList array
 */
async function changeQuantityRequest(dish_index) {

    console.log("Sending HTTP PUT request to change quantity of a dish in cart.");

    // send request and get response
    const response = await fetch(
        "/pl8s/rest/order/dishes/" + dishesList[dish_index][0], {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": token
            },
            body: JSON.stringify({
                quantity: dishesList[dish_index][1]
            })
        }
    );

    // get response body as json
    let jsonBody = await response.json();

    console.log("Request sent successfully.\nResponse: ", jsonBody);

    if (!response.ok){
        //  show error as popup
        alert(jsonBody.message.message + "\nError: " + jsonBody.message["error-code"] + "\n" + jsonBody.message["error-details"]);
    }
}

// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
// DELETE BUTTONS
// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

const SPEED = 300;

/**
 * Delete an item from the cart on the screen.
 * @param event current event
 * @param dish_index index of the dish in the dishesList array
 */
function deleteItem(event, dish_index) {

    numItems--;
    if( numItems === 0 ){
        deleteCart();
        return;
    }

    // Find div container of the item to be deleted and its article parent
    const itemDiv = event.target.closest('.item');
    const article = itemDiv.parentElement;

    // Define delete transition
    itemDiv.style.transition = "opacity "+SPEED+"ms ease";
    itemDiv.style.opacity = "0";

    // Set timeout for the delete transition
    setTimeout(function() {

        // Refresh current total price
        if( totalPriceElement != null ){
            // Remove deleted dish price from the total price of the cart
            dishesList[0][2] -= dishesList[dish_index][1] * dishesList[dish_index][2];
            totalPriceElement.innerText = (dishesList[0][2] / PRICE_MULTIPLIER).toFixed(2);
            totalPriceElement.innerText += "$";
			
			if(!(dishesList[0][2] > 0))
				document.getElementById("checkout").setAttribute("disabled", "");
        }
        else{
            alert("Total price element not found!");
        }

        // Delete item
        itemDiv.parentNode.removeChild(itemDiv);

        // Find the first .item child of the article element
        const articleChild = article.querySelector('.item');

        // If there are no more .item children, remove article
        if( article && !articleChild ){
            article.parentNode.removeChild(article);
        }
    }, SPEED);

}

/**
 * Send request to delete the dish from the cart.
 * @param dish_index index of the dish in the dishesList array
 */
async function deleteRequest(dish_index) {

    console.log("Sending HTTP DELETE request to delete a dish from cart.");

    // send request and get response
    const response = await fetch(
        "/pl8s/rest/order/dishes/" + dishesList[dish_index][0], {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json",
                "Authorization": token
            }
        }
    );

    // get response body as json
    let jsonBody = await response.json();

    console.log("Request sent successfully.\nResponse: ", jsonBody);

    if (!response.ok) {
        //  show error as popup
        alert(jsonBody.message.message + "\nError: " + jsonBody.message["error-code"] + "\n" + jsonBody.message["error-details"]);
    }
}

/**
 * Delete all dishes from the cart on the screen.
 */
function deleteCart() {
    const container = document.getElementById("cart-container");

    container.style.transition = "opacity "+SPEED+"ms ease";
    container.style.opacity = "0";

    // Set timeout for the delete transition
    setTimeout(function() {
        container.innerHTML = "";

        const emptyCartContent = "<h1>Your Cart</h1>\n" +
            "<hr>";

        container.insertAdjacentHTML("beforeend", emptyCartContent);

        setTimeout(function() {
            container.style.opacity = "1";
            showEmptyCart(container);
        }, 300);

    }, 100);
}

/**
 * Send request to delete all the dishes from the cart.
 */
async function deleteCartRequest() {

    console.log("Sending HTTP DELETE request to delete all dishes from cart.");

    // send request and get response
    const response = await fetch(
        "/pl8s/rest/order/dishes", {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json",
                "Authorization": token
            }
        }
    );

    // get response body as json
    let jsonBody = await response.json();

    console.log("Request sent successfully.\nResponse: ", jsonBody);

    if (!response.ok) {
        //  show error as popup
        alert(jsonBody.message.message + "\nError: " + jsonBody.message["error-code"] + "\n" + jsonBody.message["error-details"]);
    }
}

/**
 * Create the empty cart page and display it.
 * @param container html element in which to insert the empty cart page
 */
function showEmptyCart(container) {

    const emptyCartContent = "<div id=\"empty_cart\" class=\"empty-cart\">\n" +
        "\t<img alt=\"Empty cart!\" src=\"../../media/empty-cart.png\" class=\"empty-cart-image\">\n" +
        "</div>";

    container.insertAdjacentHTML("beforeend", emptyCartContent);

}

// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
// LOAD PAGE
// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

/**
 * Send request to load all the dishes from the cart and display them in the page.
 */
async function loadCart() {

    console.log("Sending HTTP GET request to list all dishes in cart.");

    const response = await fetch(
        "/pl8s/rest/order", {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": token
            }
        }
    );

    if(!response.ok){
        const { message } = await response.json();
        console.log("Error while listing your cart.");
        console.log(message);
        return;
    }

    // Get response body as json
    const jsonBody = await response.json();

    console.log("Request sent successfully.\nResponse: ", jsonBody);

    if(response.ok) {

        const dishes = jsonBody.order.dishes;

        const container = document.getElementById("cart-container");

        // Define total price as first element in the dishes list
        dishesList.push([-1, -1, jsonBody.order.price * PRICE_MULTIPLIER]);

        numItems = dishes.length;

        if( numItems === 0 ){
            showEmptyCart(container);
            return;
        }

        dishes.forEach((dish) => {
            loadDishItem(container, dish);
        });

		const placeOrderArticle =
            "<article id=\"place_order_article\">\n" +
            "\t<hr>\n" +
            "\t<div class=\"place-order\">\n" +
            "\t\t<input id=\"checkout\" type=\"button\" class=\"order_button\" value=\"Place Order\">\n" +
            "\t\t<div class=\"empty-space\"></div>\n" +
            "\t\t<p class=\"total-price\">" + (dishesList[0][2] / PRICE_MULTIPLIER).toFixed(2) + "$</p>\n" +
            "\t\t<div id=\"delete_cart\" class=\"circle-outline\">\n" +
            "\t\t\t<svg class=\"delete-button\" xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 448 512\"><path d=\"M135.2 17.7L128 32H32C14.3 32 0 46.3 0 64S14.3 96 32 96H416c17.7 0 32-14.3 32-32s-14.3-32-32-32H320l-7.2-14.3C307.4 6.8 296.3 0 284.2 0H163.8c-12.1 0-23.2 6.8-28.6 17.7zM416 128H32L53.2 467c1.6 25.3 22.6 45 47.9 45H346.9c25.3 0 46.3-19.7 47.9-45L416 128z\"/></svg>\n" +
            "\t\t</div>\n" +
            "\t</div>\n" +
            "</article>";
        	
		container.insertAdjacentHTML("beforeend", placeOrderArticle);
		
		document.getElementById("checkout").addEventListener("click", () =>{
			window.location.href = "/pl8s/order/checkout"
		});

        let isDeleting = false;

        document.getElementById("delete_cart").addEventListener("click", () =>{
            // If the cart is already getting deleted, return without doing anything
            if(isDeleting){
                return;
            }

            // Set to true the deleting variable
            isDeleting = true;

            deleteCart();
            deleteCartRequest();
        });

        totalPriceElement = container.querySelector(".total-price");

    } else {

        // Show error as popup
        raiseError(jsonBody.message);

    }

}

/**
 * Load a dish item inside the cart.
 * @param container html element in which to add the dish
 * @param dish json object that contains the data about the dish
 */
async function loadDishItem(container, dish) {

    // Enter data in local variables
    dishesList.push([dish.dish_id, dish.quantity, dish.price * PRICE_MULTIPLIER]);
    const current_dish = dishesList.length-1;

    // Retrieve article container
    let article = document.getElementById("restaurant" + dish.restaurant);

    if( article === null ) {
        let articleDiv =
            "<article id=\"" + "restaurant" + dish.restaurant + "\">\n" +
            "\t<header>\n" +
            "\t\t<h2 class=\"section_title\">" + dish.restaurant_name + "</h2>\n" +
            "\t</header>\n" +
            "\t<hr class=\"half_width\">\n" +
            "</article>"

        container.insertAdjacentHTML("beforeend", articleDiv);

        article = document.getElementById("restaurant" + dish.restaurant);
    }

    let itemDiv =
        "<div class=\"item\" id=\"dish" + current_dish + "\">\n" +
        "\t<p class=\"item_name\">" + dish.name + "</p>\n" +
        "\t<div class=\"item_quantity\">\n" +
        "\t\t<div class=\"circle-outline minus-button\">\n" +
        "\t\t\t<svg class=\"quantity-button\" xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 448 512\"><path d=\"M432 256c0 17.7-14.3 32-32 32L48 288c-17.7 0-32-14.3-32-32s14.3-32 32-32l352 0c17.7 0 32 14.3 32 32z\"/></svg>\n" +
        "\t\t</div>\n" +
        "\t\t<p class=\"quantity_counter\">" + dish.quantity + "</p>\n" +
        "\t\t<div class=\"circle-outline plus-button\">\n" +
        "\t\t\t<svg class=\"quantity-button\" xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 448 512\"><path d=\"M256 80c0-17.7-14.3-32-32-32s-32 14.3-32 32V224H48c-17.7 0-32 14.3-32 32s14.3 32 32 32H192V432c0 17.7 14.3 32 32 32s32-14.3 32-32V288H400c17.7 0 32-14.3 32-32s-14.3-32-32-32H256V80z\"/></svg>\n" +
        "\t\t</div>\n" +
        "\t</div>\n" +
        "\t<p class=\"item_price\">" + (dish.price * dish.quantity).toFixed(2) + "$</p>\n" +
        "\t<div class=\"circle-outline delete-button\">\n" +
        "\t\t<svg class=\"delete-button\" xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 448 512\"><path d=\"M135.2 17.7L128 32H32C14.3 32 0 46.3 0 64S14.3 96 32 96H416c17.7 0 32-14.3 32-32s-14.3-32-32-32H320l-7.2-14.3C307.4 6.8 296.3 0 284.2 0H163.8c-12.1 0-23.2 6.8-28.6 17.7zM416 128H32L53.2 467c1.6 25.3 22.6 45 47.9 45H346.9c25.3 0 46.3-19.7 47.9-45L416 128z\"/></svg>\n" +
        "\t</div>\n" +
        "</div>";

    article.insertAdjacentHTML("beforeend", itemDiv);

    // Get item added
    const item = article.lastElementChild;

    const dishLink = item.querySelector(".item_name");
    const plusButton = item.querySelector(".circle-outline.plus-button");
    const minusButton = item.querySelector(".circle-outline.minus-button");
    const deleteButton = item.querySelector(".circle-outline.delete-button");

    // Dish link to the restaurant menu it is from
    dishLink.addEventListener('click', (event) => {
        goToRestaurantMenu(event);
    });

    // Store the interval and timeout IDs
    let interval;
    let timeout;
    let buttonClicked = false;

    // plus-button time interval logic
    plusButton.addEventListener('mousedown', (event) => {
        buttonClicked = true;
        changeQuantity(event, current_dish, 'add');
        // Set time intervals to change quantity by holding the button down
        timeout = window.setTimeout(function() {
            interval = window.setInterval(() => {
                changeQuantity(event, current_dish, 'add');
            }, 100);
        }, 400);
        event.preventDefault();
    });

    plusButton.addEventListener('mouseup', () => {
        if(buttonClicked){
            window.clearInterval(interval);
            window.clearTimeout(timeout);
            changeQuantityRequest(current_dish);
        }
        buttonClicked = false;
    });

    // Clear the interval and timeout when the mouse leaves the button
    plusButton.addEventListener('mouseleave', () => {
        if(buttonClicked){
            window.clearInterval(interval);
            window.clearTimeout(timeout);
            changeQuantityRequest(current_dish);
        }
        buttonClicked = false;
    });

    // minus-button time interval logic
    minusButton.addEventListener('mousedown', (event) => {
        buttonClicked = true;
        changeQuantity(event, current_dish, 'subtract');
        // Set time intervals to change quantity by holding the button down
        timeout = window.setTimeout(function() {
            interval = setInterval(() => {
                changeQuantity(event, current_dish, 'subtract');
            }, 100);
        }, 400);
        event.preventDefault();
    });

    minusButton.addEventListener('mouseup', () => {
        // Stop the interval when the button is released
        clearInterval(interval);
        clearTimeout(timeout);
        changeQuantityRequest(current_dish);
    });

    // Clear the interval and timeout when the mouse leaves the button
    minusButton.addEventListener('mouseleave', () => {
        if(buttonClicked){
            window.clearInterval(interval);
            window.clearTimeout(timeout);
            changeQuantityRequest(current_dish);
        }
        buttonClicked = false;
    });

    let isDeleting = false;

    // delete-button logic
    deleteButton.addEventListener('click', (event) => {
        // If the dish is already getting deleted, return without doing anything
        if(isDeleting){
            return;
        }

        // Set to true the deleting variable
        isDeleting = true;

        deleteItem(event, current_dish);
        deleteRequest(current_dish);
    });
}

// Perform the retrieval of the cart when the page has just been loaded
loadCart().then();
