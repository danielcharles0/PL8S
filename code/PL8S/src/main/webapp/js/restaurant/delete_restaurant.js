/**
 * This function perform a DELETE request for a restaurant
 * @param restaurantId the restaurant ID for the restaurant to delete
 * @returns {(function(): (boolean|undefined))|*}
 */
function deleteRestaurant(restId) {
    return function() {
        if(confirm("are you sure you want to delete this restaurant?")) {
            const url = "/pl8s/rest/restaurant/delete/" + restId;

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
                processResponsed(this);
            };

            // perform the request
            console.log("Performing the HTTP DELETE request.");

            xhr.open("DELETE", url, true);
            xhr.setRequestHeader("Content-Type", "application/json");
            xhr.setRequestHeader("Authorization", token);
            xhr.send();

            console.log("HTTP DELETE request sent.");
            console.log("This is the body sent: %s ", body);
        }
    }

}

/**
 * Processes the HTTP response and writes the results back to the HTML page.
 *
 * @param xhr the XMLHttpRequest object performing the request.
 */
function processResponsed(xhr) {
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

    console.log("HTTP DELETE request successfully performed and processed.");
    redirectBackOrHome_NoHistory(user_role);

}