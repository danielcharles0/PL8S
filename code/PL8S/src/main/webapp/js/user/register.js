let password = document.getElementById("password");
let toggler = document.getElementById("toggler");
toggler.addEventListener("click", () => {showHidePassword(password)});

let repeatPassword = document.getElementById("repeatPassword");
let repeatToggler = document.getElementById("repeatToggler");
repeatToggler.addEventListener("click", () => {showHidePassword(repeatPassword)});

// login button functionality
let firstname = document.getElementById("firstname");
let lastname = document.getElementById("lastname");
let email = document.getElementById("email");
let register_button = document.getElementById("register_button");

/**
 * Registers the new user or shows an error as alert.
 * @returns {Promise<void>}
 */
let register = async () => {

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

    // check that password and repeatPassword are equal
    if(password.value !== repeatPassword.value){
        alert("Passwords are not equal.")
        return;
    }

    // build request body
    let user = JSON.stringify({
        "user": {
            "name": firstname.value,
            "surname": lastname.value,
            "email": email.value,
            "password": password.value
        }
    });

    console.log("Sending HTTP POST request to register user.");

    // send request and get response
    const response = await fetch(
        "/pl8s/rest/user/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: user
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
register_button.addEventListener("click", register);