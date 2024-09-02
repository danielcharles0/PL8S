/**
 * Authorizzation token.
*/
const token = getAuthToken();

const restaurant_id = parseURLParam("restaurant");

console.log("Restaurant: " + restaurant_id);

const dish_diet = document.getElementById("dishdiet");

/**
 * Ingredients container
 */
const ingredients = document.getElementById("ingredients");

/**
 * The function will post the new dish details
 * @param {Event} e submit event
 * @param {HTMLFormElement} form
 */
async function submitDishAdd(evt){
	
	/**
	 * Avoid the form performing its action (that is not under control)
	 */
	evt.preventDefault();

	console.log("Submitting..");
	
	const data = new FormData(evt.target);

	const ingredients = buildIngredients(data.getAll("ingredients"), data.getAll("diets"));

	if(ingredients === undefined){
		
		console.log("Error in the submission.");

		return false;
	}

	let payload = {
		"dish": {
			"name": clarifyText(data.get("name")),
			"price": Number.parseFloat(data.get("price")),
			"restaurant": restaurant_id,
			"ingredients": ingredients
		}
	}
	
	const url = "/pl8s/rest/dish";

	const response = await fetch(url, {
	    method: "POST",
	    headers: {
			"Content-Type": "application/json",
			"Authorization": token
		},
		body: JSON.stringify(payload)
	});

	if(!response.ok){
		const { message } = await response.json();
		console.log("Error while creating the dish.");
		console.log(message);
		raiseError(message);
		return false;
	}

	console.log("Submitted.");

	raiseMessage(`Dish ${payload.dish.name} successifully created.`);

	disheditform.reset();

	$("ul#ingredients li").remove();

	return true;
}

console.log("Adding event listener for the add ingredient button.");

/**
 * Adding event listener for add ingredient button.
 */
document.getElementById("addingredient").addEventListener("click", addIngredient);

console.log("Adding event listener for the form submit.");

/**
 * Adding event listener for the form submit.
 */
document.getElementById("disheditform").addEventListener("submit", submitDishAdd);