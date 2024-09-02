let password = document.getElementById("password");
let toggler = document.getElementById("toggler");
toggler.addEventListener("click", () => {showHidePassword(password)});

let repeatPassword = document.getElementById("repeatPassword");
let repeatToggler = document.getElementById("repeatToggler");
repeatToggler.addEventListener("click", () => {showHidePassword(repeatPassword)});

let firstname = document.getElementById("firstname");
let lastname = document.getElementById("lastname");
let email = document.getElementById("email");
let create_button = document.getElementById("create_button");

/**
 * Creates the manager or shows an error as alert.
 * @returns {Promise<void>}
 */
let create = async () => {

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

    let token = getAuthToken();

    // build request body
    let new_user = JSON.stringify({
        "user": {
            "name": firstname.value,
            "surname": lastname.value,
            "email": email.value,
            "password": password.value
        }
    });

    console.log("Sending HTTP POST request to create manager.");

    // send request and get response
    const response = await fetch(
        "/pl8s/rest/user/create", {
            method: "POST",
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
        // show that the request was successfully satisfied
        alert("Manager successfully created.");

        // empty all the input boxes
        firstname.value = "";
        lastname.value = "";
        email.value = "";
        password.value = "";
        repeatPassword.value = "";

    } else {

        // Show error as popup
        raiseError(jsonBody.message);

    }

}
create_button.addEventListener("click", create);