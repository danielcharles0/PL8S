/**
 * Authorization token.
*/
const token = getAuthToken();

const managers = document.getElementById("managers");

const customers = document.getElementById("customers");

// const roles = ["customer", "manager"]

{/* <td>Nina</td>
<td>Marcel</td>
<td>n.m@gmail.com</td>
<td>ahsh9asj</td>
<td>Go to restaurant</td>
<td><i class="fa-solid fa-trash"></i></td> */}

/**
 * The function will create a table cell with the specified text
 * @param {String} txt content
 * @returns {HTMLTableCellElement} the cell
 */
function createCell(txt){
	
	const td = document.createElement("td");
	td.appendChild(document.createTextNode(txt));

	return td;
}

/**
 * The function will delete the user
 * @param {Number} uId user identifier
 * @returns {Boolean} true if the user was successifully deleated, false otherwise
 */
async function deleteUser(uId){

	console.log(`Deleting user ${uId}..`);

	const url = `/pl8s/rest/user/delete/${uId}`;

	const response = await fetch(url, {
	    method: "DELETE",
	    headers: {
			"Content-Type": "application/json",
			"Authorization": token
		}
	});

	if(!await checkResponse(response, "Error while deleting the user."))
		return false;

	// const { user } = await response.json();
	
	console.log(`User ${uId} deleted.`);

	return true;
}

/**
 * The function will create the trash button
 * @param {Number} uId user id to delete with the trash
 * @param {String} email user email of the deleted user
 * @returns {HTMLTableCellElement} trush
 */
function createTrash(uId, email){
	
	const td = document.createElement("td");

	const i = document.createElement("i");

	i.classList.add("fa-solid");
	i.classList.add("fa-trash");

	i.addEventListener("click", async ({ target }) => {
		if(!confirm(`Are you sure you want to delete the user "${email}"?`))
			console.log(`User ${email} not deleted. Operation cancelled.`);
		else if(await deleteUser(uId))
			target.parentElement.parentElement.remove();
	});

	td.appendChild(i);

	return td;

}

/**
 * The function will load the user into the page
 * @param {User} user to load
 */
function createUser(user){
	
	console.log(`Creating the user ${user.user_id}..`);

	const row = document.createElement("tr");

	row.appendChild(createCell(user.name));
	row.appendChild(createCell(user.surname));
	row.appendChild(createCell(user.email));
	row.appendChild(createCell(user.stripe_id));
	
	row.appendChild(createTrash(user.user_id, user.email));

	console.log(`User ${user.user_id} created.`);
	
	return row;
}

/**
 * The function will render the users into the page
 * @param {User[]} users list
 */
function renderUsers(users){

	console.log("Starting rendering the users..");
	
	users.forEach(({ user }) => {
		if(user.role == "customer")
			customers.appendChild(createUser(user));
		else if(user.role == "manager")
			managers.appendChild(createUser(user));
	});

	console.log("Users rendered.");
}

/**
 * The function will load the users into the page
 */
async function loadUsers(){

	console.log("Loading the users..");

	const url = "/pl8s/rest/user/list";

	const response = await fetch(url, {
	    method: "GET",
	    headers: {
			"Content-Type": "application/json",
			"Authorization": token
		}
	});

	if(!await checkResponse(response, "Error while listing the users."))
		return false;

	const { users } = await response.json();

	renderUsers(users);

	console.log("Users loaded..");
}

loadUsers();

console.log("Adding the event listener to go to the create manager page.");

/**
 * Adding the event listener to go to the create manager page.
*/
document.getElementById("addmanager").addEventListener("click", () => {
	window.location.href = "/pl8s/user/create";
});

/**
 * The function will remove the previous content from the page
 */
function removeContent(){
	
	console.log("Removing previous content..");

	/**
	 * Removing previous content
	 */
	$("tbody tr").remove();
}

/**
 * The function will search users by email and render them
 * @param {String} email 
 */
async function searchUsers(email){
	
	console.log("Searching for the users..");

	const url = `/pl8s/rest/user/email/${email}`;

	const response = await fetch(url, {
	    method: "GET",
	    headers: {
			"Content-Type": "application/json",
			"Authorization": token
		}
	});
	
	if(!await checkResponse(response, "Error while searching for the users."))
		return false;
	
	const { users } = await response.json();

	removeContent();

	renderUsers(users);
	
	console.log("Search finished.");
}

/**
 * The function will search for the users and update the interface
 */
function submitSearch(evt){
	
	evt.preventDefault();

	const data = new FormData(evt.target);
	
	const email = data.get("email");
	
	if(email == ""){
		removeContent();
		loadUsers();
	} else
		searchUsers(email);

}

console.log("Adding the event listener for the search bar.");

/**
 * Adding the event listener for the search bar.
*/
document.getElementById("usersearch").addEventListener("submit", submitSearch);