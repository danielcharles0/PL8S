/**
 * Authorization token.
*/
const token = getAuthToken();

const dish_id = parseURLParam("dish");

console.log("Dish: " + dish_id);

/**
 * We assume to reach this page with a selected dish.
 * If it is not the case we redirect to the restaurants page
*/
if(typeof dish_id === 'undefined'){
	console.log("No dish selected, redirect to restaurants search page");
	window.location.replace("/pl8s/restaurants");
}/* if */

/**
 * The function creates a new 
 * @param {Order} order
 * @return {HTMLTableRowElement} table row element for the $order
 */
function createOrderTableRow(order){
	
	let td;
	
	const placedOn = new Date(`${order.placedOn} UTC`);

	console.log("Creating table row element ..");

	const tr = document.createElement("tr");
	
	td = document.createElement("td");
	td.appendChild(document.createTextNode(order.order_id));
	tr.appendChild(td);

	td = document.createElement("td");
	td.appendChild(document.createTextNode(order.user));
	tr.appendChild(td);

	td = document.createElement("td");
	td.appendChild(document.createTextNode(order.status));
	tr.appendChild(td);

	td = document.createElement("td");
	td.appendChild(document.createTextNode(dateFormatter.format(placedOn)));
	tr.appendChild(td);

	td = document.createElement("td");
	td.appendChild(document.createTextNode(timeFormatter.format(placedOn)));
	tr.appendChild(td);

	console.log("Table row element created.");
	
	return tr;
}

/**
 * The function appends an order to the order container
 * @param {HTMLTableSectionElement} ordercontainer the orders container
 * @param {Order} order to append to $ordercontainer
 */
function loadDishOrder(ordercontainer, order){

	console.log(`Loading order ${order.order_id} ..`);

	ordercontainer.appendChild(createOrderTableRow(order));
	
	console.log(`Order ${order.order_id} loaded.`);
}

/**
 * The functions loads the dish orders into the dish orders container.
 * @returns {Boolean} false if error, true otherwise
 */
async function loadDishPreviousOrders(){
	
	const url = `/pl8s/rest/dish/${dish_id}/orders`;

	const response = await fetch(url, {
	    method: "GET",
	    headers: {
			"Content-Type": "application/json",
			"Authorization": token
		}
	});

	if(!await checkResponse(response, "Error while listing dish orders."))
		return false;

	const { dish_orders } = await response.json();
	
	const ordercontainer = document.getElementById("dishorders");

	dish_orders.forEach((orderobj) => {
		const { order } = orderobj;
		loadDishOrder(ordercontainer, order);
	});

	return true;
}

console.log("Adding the event listener delegated to show previous orders of the dish.");

/**
 * Adding the event listener delegated to show previous orders of the dish
*/
document.getElementById("vieworders").addEventListener("click", async (evt) => {
	
	const input = evt.target;
	const table = document.getElementById("dishorderstable");

	console.log("Showing previous orders for the dish.");
	
	if(await loadDishPreviousOrders()){

		input.setAttribute("disabled", "");

		table.classList.remove("hidden");
	}
});