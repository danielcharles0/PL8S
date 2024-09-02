// if user is already logged in redirect back to the page he comes from or its home page
if(!tokenExpired()) {
    console.log("Token still valid, no need to login.");

    let role = JSON.parse(localStorage.getItem("pl8s-user")).role;

    redirectBackOrHome_NoHistory(role);

}

// Otherwise set page items' listeners

// Show or hide password in the text input
let password = document.getElementById("password");
let toggler = document.getElementById("toggler");
toggler.addEventListener("click", () => {showHidePassword(password)});

// login button functionality
let email = document.getElementById("email");
let login_button = document.getElementById("login_button");

/**
 * Logs in the user or shows an error as alert.
 * @returns {Promise<void>}
 */
let login = async () => {

    // email validation
    if(!validateEmail(email)) {
        alert("Invalid email syntax.");
        return;
    }

    // password validation
    if(!validatePassword(password)) {
        alert("Invalid password syntax.\nThe password must have:\n" + "- Between 6 and 20 characters\n" + "- At least a number\n" + "- At least a special character");
        return;
    }

    // build authorization token
    let token = "Basic " + btoa(email.value.trim() + ":" + password.value.trim());

    console.log("Sending HTTP GET request to login user.");

    // send request and get response
    const response = await fetch(
        "/pl8s/rest/user/login", {
            method: "GET",
            headers: {
                "Authorization": token
            }
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

        // Redirect to another page
        redirectBackOrHome_NoHistory(jsonBody.user.role);

    } else {

        // Show error as popup
        raiseError(jsonBody.message);

    }
}
login_button.addEventListener("click", login);
