// first get the user details, if user logged in, otherwise redirect to login page. Then fill the text inputs with them
let user = localStorage.getItem("pl8s-user");

let firstname = document.getElementById("firstname");
let lastname = document.getElementById("lastname");
let email = document.getElementById("email");
let password = document.getElementById("password");
let repeatPassword = document.getElementById("repeatPassword");

user = JSON.parse(user);

console.log(user);

firstname.setAttribute("value", user.name);
lastname.setAttribute("value", user.surname);
email.setAttribute("value", user.email);

let toggler = document.getElementById("toggler");
toggler.addEventListener("click", () => {showHidePassword(password)});
let repeatToggler = document.getElementById("repeatToggler");
repeatToggler.addEventListener("click", () => {showHidePassword(repeatPassword)});

// save_user button functionality
let save_user_button = document.getElementById("save_user_button");

/**
 * Saves the user general info or shows an error as alert.
 * @returns {Promise<void>}
 */
let save_user = async () => {

    // email validation
    if(!validateEmail(email)) {
        alert("Invalid email syntax.");
        return;
    }

    // build request body
     let new_user = JSON.stringify({
        "user": {
            "name": firstname.value,
            "surname": lastname.value,
            "email": email.value,
            "password": ""
        }
    });

    let token = getAuthToken();

    console.log("Sending HTTP PUT request to update user's general info.");

    // send request and get response
    const response = await fetch(
        "/pl8s/rest/user", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": token
            },
            body: new_user
        }
    );

    // get response body as json
    let jsonBody = await response.json();

    console.log("Request sent successfully.\nResponse: ", jsonBody);

    if (response.ok) {
        let expiration = new Date();
        expiration.setSeconds(expiration.getSeconds() + jsonBody.jwt.expires_in);

        localStorage.setItem("pl8s-jwt", jsonBody.jwt.access_token);
        localStorage.setItem("pl8s-jwt-exp", expiration.getTime().toString());
        localStorage.setItem("pl8s-user", JSON.stringify(jsonBody.user));

        alert("User profile changed successfully.");

    } else {

        //  Show error as popup
        raiseError(jsonBody.message);

    }
}
save_user_button.addEventListener("click", save_user);

// save_password button functionality
let save_password_button = document.getElementById("save_password_button");

/**
 * Saves the user's new password or shows an error as alert.
 * @returns {Promise<void>}
 */
let save_password = async () => {

    // password validation
    if (!validatePassword(password)) {
        alert("Invalid password syntax.\nThe password must have:\n" + "- Between 6 and 20 characters\n" + "- At least a number\n" + "- At least a special character");
        return;
    }

    // check that password and repeatPassword are equal
    if (password.value !== repeatPassword.value) {
        alert("Passwords are not equal.")
        return;
    }

    // build request body
    let new_user = JSON.stringify({
        "user": {
            "name": user.name,
            "surname": user.surname,
            "email": user.email,
            "password": password.value
        }
    });

    let token = getAuthToken();

    console.log("Sending HTTP PUT request to update user's password.");

    // send request and get response
    const response = await fetch(
        "/pl8s/rest/user", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": token
            },
            body: new_user
        }
    );

    // get response body as json
    let jsonBody = await response.json();

    console.log("Request sent successfully.\nResponse: ", jsonBody);

    if (response.ok) {
        let expiration = new Date();
        expiration.setSeconds(expiration.getSeconds() + jsonBody.jwt.expires_in);

        localStorage.setItem("pl8s-jwt", jsonBody.jwt.access_token);
        localStorage.setItem("pl8s-jwt-exp", expiration.getTime().toString());
        localStorage.setItem("pl8s-user", JSON.stringify(jsonBody.user));

        alert("Password changed successfully.");

        // Emptying the fields
        password.value = "";
        repeatPassword.value = "";

    } else {

        // Show error as popup
        raiseError(jsonBody.message);

    }
}
save_password_button.addEventListener("click", save_password);