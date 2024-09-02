/*
 * Copyright 2020-2023 University of Padua, Italy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.unipd.dei.webapp.wa001.filter;

import it.unipd.dei.webapp.wa001.resource.Message;
import it.unipd.dei.webapp.wa001.resource.ErrorCodes;
import it.unipd.dei.webapp.wa001.resource.logging.Actions;
import it.unipd.dei.webapp.wa001.resource.logging.LogContext;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Checks for successful authentication to allow for accessing stripe resources.
 */
public class StripeResourceFilter implements Filter {

    /**
     * A LOGGER available for all the subclasses.
     */
    protected static final Logger LOGGER = LogManager.getLogger(StripeResourceFilter.class, StringFormatterMessageFactory.INSTANCE);

    /**
     * The JSON UTF-8 MIME media type
     */
    private static final String JSON_UTF_8_MEDIA_TYPE = "application/json; charset=utf-8";

    /**
     * The configuration for the filter
     */
    private FilterConfig config = null;

	private String endpointSecret = "whsec_d3e495637a02f17117fb35e14a81f2eed6333229ecf76fc68ec7f4638842bb31";

    @Override
    public void init(final FilterConfig config) throws ServletException {

        if (config == null) {
            LOGGER.error("Filter configuration cannot be null.");
            throw new ServletException("Filter configuration cannot be null.");
        }
        this.config = config;
    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain chain) throws
            IOException, ServletException {

        LogContext.setIPAddress(servletRequest.getRemoteAddr());

        try {
            if (!(servletRequest instanceof HttpServletRequest) || !(servletResponse instanceof HttpServletResponse)) {
                LOGGER.error("Only HTTP requests/responses are allowed.");
                throw new ServletException("Only HTTP requests/responses are allowed.");
            }

            // Safe to downcast at this point.
            final HttpServletRequest req = (HttpServletRequest) servletRequest;
            final HttpServletResponse res = (HttpServletResponse) servletResponse;

            LOGGER.info("request URL =  %s", req.getRequestURL());

            // authenticated and authorized webhook request
            if(!authenticateWebhook(req, res))
                return;

            // the user is properly authenticated and in session, continue the processing
            chain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            LOGGER.error("Unable to perform the stripe resource filtering.", e);
        } finally {
            LogContext.removeUser();
            LogContext.removeIPAddress();
            LogContext.removeAction();
        }
    }

    /**
     * Authenticates and authorizes the webhook request.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     *
     * @return {@code true} if the webhook request has been successfully authenticated; {@code false otherwise}.
     */
    private boolean authenticateWebhook(HttpServletRequest req, HttpServletResponse res) throws IOException {

        LogContext.setAction(Actions.AUTHENTICATE_STRIPE_WEBHOOK);
        LOGGER.info("Trying to authenticate the webhook request");
		
		String payload = new String(req.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
		final String sigHeader = req.getHeader("Stripe-Signature");
		
		Event event = null;

		try {
			event = Webhook.constructEvent(
		  		payload, sigHeader, endpointSecret
			);
			LOGGER.info("Construct event done!");
		} catch (SignatureVerificationException e) {
			
			LOGGER.error("Invalid webhook signature!", e);
            Message m = new Message("Invalid webhook signature!", ErrorCodes.INVALID_INPUT_PARAMETER, e.getMessage());
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            m.toJSON(res.getOutputStream());
			return false;

		 } catch (Exception /* com.google.gson.JsonSyntaxException */ e) {
			
			LOGGER.error("Error parsing stripe webhook payload!", e);
            Message m = new Message("Error parsing stripe webhook payload!", ErrorCodes.INVALID_INPUT_PARAMETER, e.getMessage());
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            m.toJSON(res.getOutputStream());
			return false;

		}

		// Deserialize the nested object inside the event
		EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
		StripeObject stripeObject = null;
		if (dataObjectDeserializer.getObject().isPresent()) {
			stripeObject = dataObjectDeserializer.getObject().get();
		} else {
			// LOGGER.info("Object: %s", event.getData().toJson());
			LOGGER.info("Event API version: %s / Stripe API version: %s", event.getApiVersion(), Stripe.API_VERSION);
			LOGGER.error("Object deserialization error!");
			// Deserialization failed, probably due to an API version mismatch.
			// Refer to the Javadoc documentation on `EventDataObjectDeserializer` for
			// instructions on how to handle this case, or return an error here.
			return false;
		}

		// Handle the event
		switch (event.getType()) {
			case "payment_intent.succeeded":
				req.setAttribute("pi", (PaymentIntent) stripeObject);
			  	LOGGER.info("PaymentIntent was successful!");
			  	break;
			// case "payment_method.attached":
			//   PaymentMethod paymentMethod = (PaymentMethod) stripeObject;
			//   LOGGER.info("PaymentMethod was attached to a Customer!");
			//   break;
			// ... handle other event types
			default:
				LOGGER.error("Unhandled event type: " + event.getType());
				return false;
		}

        return true;
    }

    @Override
    public void destroy() {
        config = null;
    }
}
