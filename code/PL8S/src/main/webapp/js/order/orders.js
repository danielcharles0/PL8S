/**
 * Authorizzation token.
*/
const token = getAuthToken();

/*
* The maximum dish quantity appearing for the user orders
*/
let MAX_DISH_QTY;

/**
 * The function will create the order header
 * @param {FullOrder} order
 * @returns {HTMLElement} the order header
*/
function createOrderHeader(order){
	
	let span;

	const placedOn = new Date(`${order.placedOn} UTC`);

	console.log("Creating header element ..");

	const header = document.createElement("header");
	header.classList.add("order");
	
	const i = document.createElement("i");
	i.classList.add("fa-solid", "fa-chevron-up", "rotr");
	header.appendChild(i);

	i.addEventListener("click", function(evt){
		var arrow = evt.target;
		var ul = arrow.parentElement.parentElement.children.item(1);
		arrow.classList.toggle("rotd");
		$(ul).animate({
			height: 'toggle'
		});
	});

	span = document.createElement("span");
	span.classList.add("left");
	span.appendChild(document.createTextNode(`Order n.${order.order_id}`));
	header.appendChild(span);

	span = document.createElement("span");
	span.classList.add("right");
	span.classList.add("price");
	span.appendChild(document.createTextNode(`${order.price}` + " \u20AC"));
	header.appendChild(span);

	span = document.createElement("span");
	span.classList.add("right");
	span.appendChild(document.createTextNode(`${dateFormatter.format(placedOn)} `));
	timespan = document.createElement("span");
	timespan.classList.add("time");
	timespan.appendChild(document.createTextNode(`${timeFormatter.format(placedOn)}`));
	span.appendChild(timespan);
	header.appendChild(span);

	console.log("Header element created.");

	return header;
}

/**
 * The function creates the restaurant header order details
 * @param {String} restaurant_name name of the restaurant
 * @returns {HTMLElement} restaurant header order details
 */
async function createRestaurantOrderDetailHeader(restaurant_name){

	console.log("Creating header detail element ..");
	
	const header = document.createElement("header");
	
	header.classList.add("order_detail");
	const h4 = document.createElement("h4");

	h4.appendChild(document.createTextNode(restaurant_name));
	header.appendChild(h4);
	header.appendChild(document.createElement("hr"));
	
	console.log("Header detail element created.");

	return header;
}

/**
 * The function creates the restaurant order detail content containing all the dishes of the order for a specific restaurant
 * @param {Dish[]} dishes
 * @returns {HTMLUListElement} list containing the dish details
 */
function createRestaurantOrderDetailContent(dishes){

	console.log("Creating content detail element ..");

	const ul = document.createElement("ul");

	ul.classList.add("order_detail");

	dishes.forEach((dish) => {

		const li = document.createElement("li");
		
		// We do not store dish images
		// const img = document.createElement("img");
		// img.setAttribute("alt", "image");
		// li.appendChild(img);

		const span = document.createElement("span");
		span.appendChild(document.createTextNode(dish.name));
		li.appendChild(span);

		const input = document.createElement("input");
		input.setAttribute("type", "number");
		input.setAttribute("disabled", "");
		input.setAttribute("value", dish.quantity);
		input.setAttribute("min", "1");
		input.setAttribute("max", MAX_DISH_QTY);
		li.appendChild(input);

		ul.appendChild(li);
	});

	console.log("Content detail element created.");
	
	return ul;
}

/**
 * The function creates the restaurant order details
 * @param {String} restaurant_name name of the restaurant
 * @param {Dish} dishes of the order for the restaurant $restaurant_id
 * @returns {HTMLLinkElement} restaurant order detail
 */
async function createRestaurantOrderDetail(restaurant_name, dishes){
	
	console.log(`Creating restaurant '${restaurant_name}' detail elements ..`);
	
	const li = document.createElement("li");

	li.appendChild(await createRestaurantOrderDetailHeader(restaurant_name));
	li.appendChild(createRestaurantOrderDetailContent(dishes));

	console.log(`Restaurant '${restaurant_name}' detail elements created.`);
	
	return li;
}

/**
 * The function will create the order detail elements
 * @param {FullOrder} order
 * @returns {HTMLUListElement} the detail elements list
 */
async function createOrderDetail(order){
	
	console.log("Creating detail elements ..");

	const ul = document.createElement("ul");
	ul.classList.add("hidden");

	// No control over the restaurant order
	// Map.groupBy(order.dishes, (dish) => dish.restaurant).forEach(async (dishes, restaurant_id) => {
	// 	ul.appendChild(await createRestaurantOrderDetail(restaurant_id, dishes));
	// });

	for(const [restaurant_name, dishes] of Map.groupBy(order.dishes, (dish) => dish.restaurant_name))
		ul.appendChild(await createRestaurantOrderDetail(restaurant_name, dishes));

	console.log("Detail elements created.");

	return ul;
}

/**
 * The function will load the logged user previous orders
 * @param {HTMLDivElement} container orders container
 * @param {FullOrder} order order to load
*/
async function loadPreviousOrder(container, order){
	
	console.log(`Loading order ${order.order_id} ..`);

	const article = document.createElement("article");
	
	article.appendChild(createOrderHeader(order));
	
	article.appendChild(await createOrderDetail(order));

	container.appendChild(article);

	console.log(`Order ${order.order_id} loaded.`);
}

/**
 * The function returns the maximum dish quantity appearing in the user orders
 * @param {FullOrder[]} orders
 * @returns {Number} the maximum dish quantity appearing in the user orders
 */
function getMaxDishQuantity(orders){

	let dmq = 0;

	orders.forEach(({order}) => {
		order.dishes.forEach((dish) => {
			if(dish.quantity > dmq)
				dmq = dish.quantity;
		});
	});

	return dmq;
}

/**
 * The function will load the previous orders for the logged user.
 */
async function loadOrders(){
	
	const url = "/pl8s/rest/order/previous";

	const response = await fetch(url, {
	    method: "GET",
	    headers: {
			"Content-Type": "application/json",
			"Authorization": token
		}
	});

	if(!response.ok){
		const { message } = await response.json();
		console.log("Error while listing dish orders.");
		console.log(message);
		raiseError(message);
		return;
	}
	
	const { previous_orders } = await response.json();
	
	MAX_DISH_QTY = getMaxDishQuantity(previous_orders);
	console.log("MAX_DISH_QTY: " + MAX_DISH_QTY);

	/**
	 * The content container element.
	*/
	const container = document.getElementById("content");

	// No control over the restaurant order
	// previous_orders.forEach(async ({ order }) => await loadPreviousOrder(container, order));
	if(previous_orders.length == 0)
		container.appendChild(document.createTextNode("No orders .."));
	else
		for(const { order } of previous_orders)
			await loadPreviousOrder(container, order);
}

loadOrders();