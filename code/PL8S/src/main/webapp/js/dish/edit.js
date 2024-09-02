/**
 * Authorizzation token.
*/
const token = getAuthToken();

const dish_id = parseURLParam("dish");

console.log("Dish: " + dish_id);

const dish_diet = document.getElementById("dishdiet");

/**
 * Ingredients container
 */
const ingredients = document.getElementById("ingredients");

// console.log("Adding event listener to textarea.");

/**
 * To enable textarea vertical auto-resizing
 */
// ingredients.addEventListener("input", function(){
// 	this.style.height = "auto";
// 	this.style.height = this.scrollHeight + "px";
// });

/**
 * The function will post the new dish details
 * @param {Event} e submit event
 * @param {HTMLFormElement} form
 */
async function submitDishEdit(evt){
	
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
			"dish_id": dish_id,
			"name": clarifyText(data.get("name")),
			"price": Number.parseFloat(data.get("price")),
			"restaurant": restaurant_id,
			"ingredients": ingredients
		}
	}
	
	const url = `/pl8s/rest/dish/${dish_id}`;

	const response = await fetch(url, {
	    method: "PUT",
	    headers: {
			"Content-Type": "application/json",
			"Authorization": token
		},
		body: JSON.stringify(payload)
	});

	if(!await checkResponse(response, "Error while updating the dish."))
		return false;

	console.log("Submitted.");

	raiseMessage("Dish successifully updated.")

	return true;
}

/**
 * The function will delete the dish from the database
 */
async function deleteDish(){

	const url = `/pl8s/rest/dish/${dish_id}`;

	const response = await fetch(url, {
	    method: "DELETE",
	    headers: {
			"Content-Type": "application/json",
			"Authorization": token
		}
	});

	if(!await checkResponse(response, "Error while updating the dish."))
		return false;

	raiseMessage("Deletion successifully succeded. You will be redirected to the restaurant's page.");

	/**
	 * Redirect without storing this page in search history (since the dish no longer exists).
	 */
    window.location.replace(`/pl8s/dishes/manager/restaurant/${restaurant_id}`);

}

console.log("Adding event listener for the form submit.");

/**
 * Adding event listener for the form submit.
 */
document.getElementById("disheditform").addEventListener("submit", submitDishEdit);

console.log("Adding event listener for the trash button(s).");

/**
 * Adding event listener for delete button
 */
$("i.fa-trash").click(deleteIngredient);

console.log("Adding event listener for updating the dish diet for the actual dish ingredient(s).");

/**
 * Adding event listener for updating the dish diet
 */
$("select[name='diets']").on("change", updateDishDiet);

console.log("Adding event listener for the add button.");

/**
 * Adding event listener for add ingredient button.
 */
document.getElementById("addingredient").addEventListener("click", addIngredient);

console.log("Adding event listener for delete dish button.");
/**
 * Adding event listener for delete dish button.
 */
document.getElementById("deletedish").addEventListener("click", () => {
	if(confirm("The dish will be permanently deleted."))
		deleteDish();
});

console.log("Adding event listener to clarify ingredient name for the actual ingredient(s) text input(s).");

/**
 * Adding event listener to clarify ingredient name for the actual ingredient(s) text input(s).
 */
$("input[type='text'].ingredient").on("focusout", ({ target }) => {
	target.value = clarifyIngredient(target.value);
});