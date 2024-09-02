const diets = ["vegan", "vegetarian", "carnivorous"];

const ing_examples = ["Tomato Sauce", "Spicy Salami", "Cheese", "Meat", "Bread", "Chorizo", "Shrimps", "Salmon", "Potato"];

/**
 * The function will put the dish ingredients in the right format to be POSTed
 * @param {String[]} ings 
 * @param {String[]} diets 
 * @returns {Ingredient[]} dish ingredients in the right format to be POSTed
 */
function buildIngredients(ings, diets){
	
	console.log("Building dish ingredients..");
	
	let isValid = (ings.length > 0);

	if(!isValid){

		const message = {
			"message": "The ingredients cannot be empty!",
			"error-code": "FEE0",
			"error-details": ""
		};

		console.log("Error while building dish ingredients.");
		console.log(message);

		raiseError(message);

		return undefined;
	}

	const map = new Map();
	const ingredients = [];

	ings.forEach((ing, idx) => {
		
		ting = clarifyIngredient(ing);

		if(map.has(ting))
			isValid = false;

		map.set(ting, "");

		ingredients.push({
			"name": ting,
			"diet": diets[idx]
		});
	});

	if(!isValid){
		
		const message = {
			"message": "The ingredients must be unique!",
			"error-code": "FEE1",
			"error-details": ""
		};

		console.log("Error while building dish ingredients.");
		console.log(message);

		raiseError(message);

		return undefined;
	}
	
	console.log("Dish ingredients builded.");

	return ingredients;
}

/**
 * The function will create the dish ingredient name input box.
 * @returns {HTMLInputElement} the dish ingredient name input box.
 */
function createIngredientInput(){
	
	console.log("Creating the input element..");

	const input = document.createElement("input");

	input.setAttribute("required", "");
	input.setAttribute("type", "text");
	input.setAttribute("class", "ingredient");
	input.setAttribute("name", "ingredients");
	input.setAttribute("placeholder", ing_examples[getRandom(0, ing_examples.length)]);

	/**
	 * The listener will clarify the inserted text.
	 */
	input.addEventListener("focusout", ({ target }) => {
		target.value = clarifyIngredient(target.value);
	});

	console.log("Input element created.");

	return input;
}

/**
 * The function will create the dish ingredient diet select box.
 * @returns {HTMLSelectElement} the dish ingredient diet select box.
 */
function createIngredientSelect(){

	console.log("Creating the select element..");

	const select = document.createElement("select");

	select.setAttribute("required", "");
	select.setAttribute("name", "diets");
	select.setAttribute("class", "ingredient");

	diets.forEach((elem) => {
		
		const opt = document.createElement("option");
		
		opt.setAttribute("value", elem);

		opt.appendChild(document.createTextNode(elem));

		select.appendChild(opt);

	});

	select.addEventListener("change", updateDishDiet);

	console.log("Select element created.");

	return select;

}

/**
 * The function will delete an ingredient
 */
function deleteIngredient({ target }){
	
	target.parentElement.remove();
	
	/**
	 * Dish diet updating after ingredient deletion.
	 */
	updateDishDiet();
}

/**
 * The function will return the trash icon element.
 * @returns {HTMLElement} the trash icon element.
 */
function createTrash(){
	
	console.log("Creating the trash icon element..");

	const i = document.createElement("i");

	i.classList.add("fa-solid");
	i.classList.add("fa-trash");

	console.log("Adding event listener for the trash button.");

	/**
	 * Ingredient deletion clicking on the trash.
	 */
	i.addEventListener("click", deleteIngredient);

	console.log("Trash icon element created.");

	return i;
}

/**
 * The function will add an empty ingredient to the dish.
 */
function addIngredient(){

	const ing = document.createElement("li");

	ing.appendChild(createIngredientInput());

	ing.appendChild(createIngredientSelect());

	ing.appendChild(createTrash());

	ingredients.appendChild(ing);

}

/**
 * The function will recompute the dish diet whenever a change happens
 */
function updateDishDiet(){

	console.log("Updating dish diet..");

	let diet = {"id": 0, "val": diets[0]};

	$("select[name='diets']").each((elem, { value }) => {
		
		const idx = diets.indexOf(value);

		if(idx > diet.id){
			diet.id = idx;
			diet.val = value;
		}
	});

	dish_diet.selectedIndex = diet.id;

	console.log("Dish diet updated.");
}

/**
 * The function clarifies the ingredient name
 * First letter of a word is UpperCase
 * @param {String} ing to be clarified
 */
function clarifyIngredient(ing){
	
	console.log("Clarifying inserted text.");

	let cltxt = [];

	const tokens = clarifyText(ing).split(" ");
	
	tokens.forEach((word) => {
		cltxt.push(word.charAt(0).toUpperCase() + word.slice(1));
	});

	console.log("Inserted text clarified.");

	return cltxt.join(" ");
}