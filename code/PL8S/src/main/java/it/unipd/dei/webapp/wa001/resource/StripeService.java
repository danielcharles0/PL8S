package it.unipd.dei.webapp.wa001.resource;

import java.util.Date;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethodCollection;
import com.stripe.net.RequestOptions;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerListPaymentMethodsParams;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.param.PaymentIntentCreateParams;

import it.unipd.dei.webapp.wa001.resource.dbentities.FullOrder;
import it.unipd.dei.webapp.wa001.resource.dbentities.User;

/**
 * Contains the methods needed to communicate to Stripe.
 * references:
 * 	- https://docs.stripe.com/development/quickstart
 * 	- https://docs.stripe.com/api
 */
public class StripeService {

	private static final String api_sk = "sk_test_51P40kMIlIIouy5CbUvrrt6isRV78n1oVYm6ROvkfDs18ebL9eZJOtfahZ4NuqCIiaNMaTs4WTbbqzFrcphQCrKKa008EmLuaPG";

	static{
		Stripe.apiKey = api_sk;
	}

    /**
     * This class can be neither instantiated nor sub-classed.
     */
    private StripeService() {
        throw new AssertionError(String.format("No instances of %s allowed.", StripeService.class.getName()));
    }

	/**
     * Creates a new Stripe customer and returns it's id.
     *
	 * @param user the user to be related with the created customer
	 * @throws StripeException if there is any issue.
	 * @return the id of the created customer.
     */
	public static final Customer createCustomer(User user) throws StripeException{

		CustomerCreateParams params =
		  CustomerCreateParams.builder()
		    .setName(user.getName() + " " + user.getSurname())
		    .setEmail(user.getEmail())
		    .build();
		
		return Customer.create(params);
	}

	/**
     * Relates Stripe customer with pl8s.
	 * 
	 * @param user the user to be related with the created customer
	 * @throws StripeException if there is any issue.
     */
	public static void relateCustomer(User user) throws StripeException{

		Customer resource = Customer.retrieve(user.getStripe_id());

		CustomerUpdateParams params =
		  CustomerUpdateParams.builder().putMetadata("user_id", String.valueOf(user.getUser_id())).build();

		resource.update(params);
	}
	
	/**
     * Retrieves a customer payment methods.
     *
	 * @param user the user to which we want to list credit cards
	 * @throws StripeException if there is any issue.
	 * @return the list of stripe payment methods.
     */
	public static final PaymentMethodCollection listPaymentMethods(User user) throws StripeException{

		Customer resource = Customer.retrieve(user.getStripe_id());

		CustomerListPaymentMethodsParams params =
		  CustomerListPaymentMethodsParams.builder().build();
		
		return resource.listPaymentMethods(params);
	}

	/**
	 * Deletes a stripe customer.
	 *
	 * @param resource the customer to delete.
	 * @throws StripeException if there is any issue.
	 */
	public static void deleteCustomer(Customer resource) throws StripeException{

		resource.delete();
	}

	/**
	 * Retreives a stripe customer.
	 *
	 * @param cus the customer id.
	 * @throws StripeException if there is any issue.
	 * @return the stripe customer
	 */
	public static Customer getCustomer(String cus) throws StripeException{

		return Customer.retrieve(cus);
		
	}

	/**
	 * Creates a stripe payment intent.
	 * @param user the user paying the bill
	 * @param order the current cart
	 * @throws StripeException if there is any issue.
	 * @return the payment intent
	 */
	public static PaymentIntent createPayment(User user, FullOrder order) throws StripeException{

		Customer cus = getCustomer(user.getStripe_id());

		long amt = order.getOrder().getCentsPrice(), unixTime = (new Date()).getTime();
		int order_id = order.getOrder().getOrder_id();

		PaymentIntentCreateParams params =
	        PaymentIntentCreateParams.builder()
	          .setAmount(amt)
	          .setCurrency("eur")
			  .setCustomer(cus.getId())
			  .putMetadata("user_id", String.valueOf(user.getUser_id()))
			  .putMetadata("order_id", String.valueOf(order_id))
			  // .setPaymentMethod(cus.getInvoiceSettings().getDefaultPaymentMethod())
			  // In the latest version of the API, specifying the `automatic_payment_methods` parameter is optional because Stripe enables its functionality by default.
	          .setAutomaticPaymentMethods(
	            PaymentIntentCreateParams.AutomaticPaymentMethods
	              .builder()
	              .setEnabled(true)
	              .build()
	          )
	          .build();
		
	  	/*
		* Set a reasonable idempotency key to avoid duplucate requests.
		* We consider a payment a duplicate in a time window of 10s for the same order.
		*/
	  	String idempKey = "order_id:" + order_id + "-time:" + (unixTime / 10000L) * 10000L;

		RequestOptions reqOpt = (new RequestOptions.RequestOptionsBuilder()).setIdempotencyKey(idempKey).build();

      	// Create a PaymentIntent with the order amount and currency
		return PaymentIntent.create(params, reqOpt);
	}

}
