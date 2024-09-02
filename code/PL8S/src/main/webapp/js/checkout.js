/**
 * Authorizzation token.
*/
const token = getAuthToken();

/**
 * Stripe library
 */
let stripe;

/**
 * The payment intent
 */
let paymentIntent;

/**
 * Client secret configured by stripe returning into the checkout page
 */
let urlClientSecret = new URLSearchParams(window.location.search).get(
	"payment_intent_client_secret"
);

/**
 * Stripe elements
 */
let elements;

/**
 * The function will load the checkout page
 */
async function loadCheckout(){
	/**
	 * Loading stripe library
	 */
	stripe = await Stripe("pk_test_51P40kMIlIIouy5CbN5CNy9B6EZAHlMv4gEf5aZUTv2FxSozG5QuIVzUnd9yLJLLSXzzxSoGDqXjnvbREzvP7ZFmy00N5Qb7SB8", { locale: "en"});
	
	if(urlClientSecret)
		paymentIntent = (await stripe.retrievePaymentIntent(urlClientSecret)).paymentIntent;

	await initialize();
	checkStatus();
}

loadCheckout();

document
	.querySelector("#payment-form")
	.addEventListener("submit", handleSubmit);

/**
 * The function will load the checkout page header
 * @returns {Boolean} true if everithing ok, false otherwise
 */
async function loadHeader(){
	
	/**
	 * If the payment was successifull the payment object is not rendered
	 */
	if(paymentIntent !== undefined && paymentIntent.status == "succeeded")
		return false;

	const response = await fetch("/pl8s/rest/order", {
		method: "GET",
		headers: {
			"Authorization": token
		}
	});
	
	if(!await checkResponse(response, "Error while retrieving payment information."))
		return false;

	const { order } = await response.json();

	if(order.price > 0){
		checkout.appendChild(document.createTextNode(`Order total: ${order.price}` + " \u20AC"));
		return true;
	}

	document.querySelector("#submit").disabled = true;	

	raiseMessage("Checkout failed, the cart cannot be empty!");

	return false;
}

/**
 * The function will retreive a new client secret from a new payment intent
 * @returns {String} clientSecret
 */
async function getNewClientSecret(){
	
	const response = await fetch("/pl8s/rest/order", {
		method: "PUT",
		headers: {
			"Content-Type": "application/json",
			"Authorization": token
		}
	});

	if(!await checkResponse(response, "Error in the payment initializzation."))
		return undefined;

	const { clientSecret } = await response.json();

	return clientSecret;
}

/**
 * The function fetches a payment intent and captures the client secret
 */
async function initialize() {
	
	if(!await loadHeader())
		return;

	const clientSecret = (urlClientSecret) ? urlClientSecret : await getNewClientSecret();

	if(clientSecret === undefined)
		return;

	const appearance = {
		theme: 'stripe',
	};

	elements = stripe.elements({ appearance, clientSecret });

	const paymentElementOptions = {
		layout: "tabs",
	};

	const paymentElement = elements.create("payment", paymentElementOptions);
	paymentElement.mount("#payment-element");
}

/**
 * The function will perform the payment on submission
 * @param {Event} e submit event
 */
async function handleSubmit(e) {
	e.preventDefault();
	setLoading(true);

	const { error } = await stripe.confirmPayment({
		elements,
		confirmParams: {
			// Make sure to change this to your payment completion page
			return_url: window.location.href,
		},
	});

	// This point will only be reached if there is an immediate error when
	// confirming the payment. Otherwise, your customer will be redirected to
	// your `return_url`. For some payment methods like iDEAL, your customer will
	// be redirected to an intermediate site first to authorize the payment, then
	// redirected to the `return_url`.
	if (error.type === "card_error" || error.type === "validation_error") {
		showMessage(error.message);
	} else {
		showMessage("An unexpected error occurred.");
	}

	setLoading(false);
}

/**
 * The function fetches the payment intent status after payment submission
 */
async function checkStatus() {

	if (!urlClientSecret || paymentIntent === undefined)
		return;

	switch (paymentIntent.status) {
		case "succeeded":
			showMessage("Payment succeeded! You will be redirected in few seconds.");
			document.querySelector("#submit").disabled = true;
			break;
		case "processing":
			showMessage("Your payment is processing.");
			break;
		case "requires_payment_method":
			showMessage("Your payment was not successful, please try again.");
			break;
		default:
			showMessage("Something went wrong.");
			break;
	}
}

// ------- UI helpers -------
/**
 * The function shows a message in the checkout element
 * @param {String} messageText text to be shown
 */
function showMessage(messageText) {
	
	let time = 8000;
	const succeded = paymentIntent.status == "succeeded";
	const messageContainer = document.querySelector("#payment-message");

	messageContainer.classList.remove("hidden");
	messageContainer.textContent = messageText;

	if(succeded)
		time = 4000;

	setTimeout(function () {
		messageContainer.classList.add("hidden");
		messageContainer.textContent = "";

		if(succeded)
			window.location.href = "/pl8s/order/list";

	}, time);
}

/**
 * The function shows a spinner on payment submission
 */
function setLoading(isLoading) {
	if (isLoading) {
		// Disable the button and show a spinner
		document.querySelector("#submit").disabled = true;
		document.querySelector("#spinner").classList.remove("hidden");
		document.querySelector("#button-text").classList.add("hidden");
	} else {
		document.querySelector("#submit").disabled = false;
		document.querySelector("#spinner").classList.add("hidden");
		document.querySelector("#button-text").classList.remove("hidden");
	}
}