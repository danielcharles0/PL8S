// Here we work only on the navbar, adapting it to the different kind of user we have (guest, customer, manager, admin)

// First understand the user role
let user_role = null;
let name = null;
if(tokenExpired()) {
    user_role = "guest";
    name = "Guest";
}
else{
    let json = JSON.parse(localStorage.getItem("pl8s-user"));
    user_role = json.role;
    name = json.name;
}

console.log("User: " + user_role);

// Set the username
document.getElementById("username").textContent =  name;

// Get the two sections after which we have to insert new elements
let general = $(document.getElementById("general"));
let settings = $(document.getElementById("settings"));

// List of all possible elements to be added
let home_item = "<div class=\"navbar-setting\" id=\"home\">\n" +
    "\t\t\t\t<svg class=\"setting-icon\" xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 576 512\"><!--!Font Awesome Free 6.5.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2024 Fonticons, Inc.--><path d=\"M575.8 255.5c0 18-15 32.1-32 32.1h-32l.7 160.2c0 2.7-.2 5.4-.5 8.1V472c0 22.1-17.9 40-40 40H456c-1.1 0-2.2 0-3.3-.1c-1.4 .1-2.8 .1-4.2 .1H416 392c-22.1 0-40-17.9-40-40V448 384c0-17.7-14.3-32-32-32H256c-17.7 0-32 14.3-32 32v64 24c0 22.1-17.9 40-40 40H160 128.1c-1.5 0-3-.1-4.5-.2c-1.2 .1-2.4 .2-3.6 .2H104c-22.1 0-40-17.9-40-40V360c0-.9 0-1.9 .1-2.8V287.6H32c-18 0-32-14-32-32.1c0-9 3-17 10-24L266.4 8c7-7 15-8 22-8s15 2 21 7L564.8 231.5c8 7 12 15 11 24z\"/></svg>\n" +
    "\t\t\t\t<p>Home</p>\n" +
    "\t\t\t</div>";

let cart_item = "<div class=\"navbar-setting\" id=\"cart\">\n" +
    "\t\t\t\t<svg class=\"setting-icon\" xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 576 512\"><!--!Font Awesome Free 6.5.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2024 Fonticons, Inc.--><path d=\"M0 24C0 10.7 10.7 0 24 0H69.5c22 0 41.5 12.8 50.6 32h411c26.3 0 45.5 25 38.6 50.4l-41 152.3c-8.5 31.4-37 53.3-69.5 53.3H170.7l5.4 28.5c2.2 11.3 12.1 19.5 23.6 19.5H488c13.3 0 24 10.7 24 24s-10.7 24-24 24H199.7c-34.6 0-64.3-24.6-70.7-58.5L77.4 54.5c-.7-3.8-4-6.5-7.9-6.5H24C10.7 48 0 37.3 0 24zM128 464a48 48 0 1 1 96 0 48 48 0 1 1 -96 0zm336-48a48 48 0 1 1 0 96 48 48 0 1 1 0-96z\"/></svg>\n" +
    "\t\t\t\t<p>Cart</p>\n" +
    "\t\t\t</div>";

let orders_item = "<div class=\"navbar-setting\" id=\"orders\">\n" +
    "\t\t\t\t<svg class=\"setting-icon\" xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 512 512\"><!--!Font Awesome Free 6.5.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2024 Fonticons, Inc.--><path d=\"M464 256A208 208 0 1 1 48 256a208 208 0 1 1 416 0zM0 256a256 256 0 1 0 512 0A256 256 0 1 0 0 256zM232 120V256c0 8 4 15.5 10.7 20l96 64c11 7.4 25.9 4.4 33.3-6.7s4.4-25.9-6.7-33.3L280 243.2V120c0-13.3-10.7-24-24-24s-24 10.7-24 24z\"/></svg>\n" +
    "\t\t\t\t<p>Previous Orders</p>\n" +
    "\t\t\t</div>";

let login_item = "<div class=\"navbar-setting\" id=\"login\">\n" +
    "\t\t\t\t<svg class=\"setting-icon\" xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 512 512\"><!--!Font Awesome Free 6.5.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2024 Fonticons, Inc.--><path d=\"M217.9 105.9L340.7 228.7c7.2 7.2 11.3 17.1 11.3 27.3s-4.1 20.1-11.3 27.3L217.9 406.1c-6.4 6.4-15 9.9-24 9.9c-18.7 0-33.9-15.2-33.9-33.9l0-62.1L32 320c-17.7 0-32-14.3-32-32l0-64c0-17.7 14.3-32 32-32l128 0 0-62.1c0-18.7 15.2-33.9 33.9-33.9c9 0 17.6 3.6 24 9.9zM352 416l64 0c17.7 0 32-14.3 32-32l0-256c0-17.7-14.3-32-32-32l-64 0c-17.7 0-32-14.3-32-32s14.3-32 32-32l64 0c53 0 96 43 96 96l0 256c0 53-43 96-96 96l-64 0c-17.7 0-32-14.3-32-32s14.3-32 32-32z\"/></svg>\n" +
    "\t\t\t\t<p>Log In</p>\n" +
    "\t\t\t</div>";

let logout_item = "<div class=\"navbar-setting\" id=\"logout\">\n" +
    "\t\t\t\t<svg class=\"setting-icon\" xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 512 512\"><!--!Font Awesome Free 6.5.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2024 Fonticons, Inc.--><path d=\"M377.9 105.9L500.7 228.7c7.2 7.2 11.3 17.1 11.3 27.3s-4.1 20.1-11.3 27.3L377.9 406.1c-6.4 6.4-15 9.9-24 9.9c-18.7 0-33.9-15.2-33.9-33.9l0-62.1-128 0c-17.7 0-32-14.3-32-32l0-64c0-17.7 14.3-32 32-32l128 0 0-62.1c0-18.7 15.2-33.9 33.9-33.9c9 0 17.6 3.6 24 9.9zM160 96L96 96c-17.7 0-32 14.3-32 32l0 256c0 17.7 14.3 32 32 32l64 0c17.7 0 32 14.3 32 32s-14.3 32-32 32l-64 0c-53 0-96-43-96-96L0 128C0 75 43 32 96 32l64 0c17.7 0 32 14.3 32 32s-14.3 32-32 32z\"/></svg>\n" +
    "\t\t\t\t<p>Log Out</p>\n" +
    "\t\t\t</div>";

let users_item = "<div class=\"navbar-setting\" id=\"users\">\n" +
    "\t\t\t\t<svg class=\"setting-icon\" xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 640 512\"><!--!Font Awesome Free 6.5.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2024 Fonticons, Inc.--><path d=\"M144 0a80 80 0 1 1 0 160A80 80 0 1 1 144 0zM512 0a80 80 0 1 1 0 160A80 80 0 1 1 512 0zM0 298.7C0 239.8 47.8 192 106.7 192h42.7c15.9 0 31 3.5 44.6 9.7c-1.3 7.2-1.9 14.7-1.9 22.3c0 38.2 16.8 72.5 43.3 96c-.2 0-.4 0-.7 0H21.3C9.6 320 0 310.4 0 298.7zM405.3 320c-.2 0-.4 0-.7 0c26.6-23.5 43.3-57.8 43.3-96c0-7.6-.7-15-1.9-22.3c13.6-6.3 28.7-9.7 44.6-9.7h42.7C592.2 192 640 239.8 640 298.7c0 11.8-9.6 21.3-21.3 21.3H405.3zM224 224a96 96 0 1 1 192 0 96 96 0 1 1 -192 0zM128 485.3C128 411.7 187.7 352 261.3 352H378.7C452.3 352 512 411.7 512 485.3c0 14.7-11.9 26.7-26.7 26.7H154.7c-14.7 0-26.7-11.9-26.7-26.7z\"/></svg>\n" +
    "\t\t\t\t<p>Users</p>\n" +
    "\t\t\t</div>";

let restaurants_item = "<div class=\"navbar-setting\" id=\"restaurants\">\n" +
    "\t\t\t\t<svg class=\"setting-icon\" xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 448 512\"><!--!Font Awesome Free 6.5.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2024 Fonticons, Inc.--><path d=\"M416 0C400 0 288 32 288 176V288c0 35.3 28.7 64 64 64h32V480c0 17.7 14.3 32 32 32s32-14.3 32-32V352 240 32c0-17.7-14.3-32-32-32zM64 16C64 7.8 57.9 1 49.7 .1S34.2 4.6 32.4 12.5L2.1 148.8C.7 155.1 0 161.5 0 167.9c0 45.9 35.1 83.6 80 87.7V480c0 17.7 14.3 32 32 32s32-14.3 32-32V255.6c44.9-4.1 80-41.8 80-87.7c0-6.4-.7-12.8-2.1-19.1L191.6 12.5c-1.8-8-9.3-13.3-17.4-12.4S160 7.8 160 16V150.2c0 5.4-4.4 9.8-9.8 9.8c-5.1 0-9.3-3.9-9.8-9L127.9 14.6C127.2 6.3 120.3 0 112 0s-15.2 6.3-15.9 14.6L83.7 151c-.5 5.1-4.7 9-9.8 9c-5.4 0-9.8-4.4-9.8-9.8V16zm48.3 152l-.3 0-.3 0 .3-.7 .3 .7z\"/></svg>\n" +
    "\t\t\t\t<p>Restaurants</p>\n" +
    "\t\t\t</div>";

// Now that we know the user role, and we have all the possible elements to be added,
// we can create the user specific navbar
switch (user_role){
    case "guest":
        createNavbarGuest();
        break;
    case "customer":
        createNavbarCustomer();
        break;
    case "manager":
        createNavbarManager();
        break;
    case "admin":
        createNavbarAdmin();
        break;
}

// List of all the functions to create the navbar
/**
 * Creates the navbar buttons for Guest user type
 */
function createNavbarGuest() {

    general.after(home_item);

    settings.after(login_item);

    // add event listeners
    let home_button = document.getElementById("home");
    home_button.addEventListener("click", goToHome);

    let login_button = document.getElementById("login");
    login_button.addEventListener("click", goToLogin);

}

/**
 * Creates the navbar buttons for Customer user type
 */
function createNavbarCustomer() {

    general.after(orders_item);
    general.after(cart_item);
    general.after(home_item);

    settings.after(logout_item);

    // add event listeners
    let user_button = document.getElementById("userprofile");
    user_button.addEventListener("click", goToUserProfile);

    let orders_button = document.getElementById("orders");
    orders_button.addEventListener("click", goToOrders);

    let cart_button = document.getElementById("cart");
    cart_button.addEventListener("click", goToCart);

    let home_button = document.getElementById("home");
    home_button.addEventListener("click", goToHome);

    let logout_button = document.getElementById("logout");
    logout_button.addEventListener("click", logout);

}

/**
 * Creates the navbar buttons for Manager user type
 */
function createNavbarManager() {

    general.after(restaurants_item);

    settings.after(logout_item);

    // add event listeners
    let user_button = document.getElementById("userprofile");
    user_button.addEventListener("click", goToUserProfile);

    let restaurants_button = document.getElementById("restaurants");
    restaurants_button.addEventListener("click", goToRestaurants);

    let logout_button = document.getElementById("logout");
    logout_button.addEventListener("click", logout);
}

/**
 * Creates the navbar buttons for Admin user type
 */
function createNavbarAdmin() {

    general.after(users_item);
    general.after(restaurants_item);

    settings.after(logout_item);

    // add event listeners
    let user_button = document.getElementById("userprofile");
    user_button.addEventListener("click", goToUserProfile);

    let users_button = document.getElementById("users");
    users_button.addEventListener("click", goToUsers);

    let restaurants_button = document.getElementById("restaurants");
    restaurants_button.addEventListener("click", goToRestaurants);

    let logout_button = document.getElementById("logout");
    logout_button.addEventListener("click", logout);

}


// Easter egg
let logo = document.getElementsByClassName("logo-image")[0];
logo.addEventListener("click", () => { console.log("Thanks for choosing PL8S by WA001 :)"); });