/*
 * Copyright 2023 University of Padua, Italy
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

package it.unipd.dei.webapp.wa001.rest.order;

import it.unipd.dei.webapp.wa001.database.order.ListDishesFromOrderDAO;
import it.unipd.dei.webapp.wa001.resource.dbentities.FullOrder;
import it.unipd.dei.webapp.wa001.resource.logging.Actions;
import it.unipd.dei.webapp.wa001.resource.Message;
import it.unipd.dei.webapp.wa001.resource.ErrorCodes;
import it.unipd.dei.webapp.wa001.resource.StripeService;
import it.unipd.dei.webapp.wa001.resource.logging.LogContext;
import it.unipd.dei.webapp.wa001.rest.AbstractRR;
import it.unipd.dei.webapp.wa001.resource.stripe.PaymentResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

/**
 * A REST resource for creating a payment for an Order {@link CreatePaymentRR}. *
 */
public final class CreatePaymentRR extends AbstractRR {

    /**
     * The JSON UTF-8 MIME media type
     */
    private static final String JSON_UTF_8_MEDIA_TYPE = "application/json; charset=utf-8";

    /**
     * The create payment resource.
     */
    private static final String RESOURCE = "/rest/order";

    /**
     * Creates a new REST resource for creating a payment for an {@code Order}.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @param con the connection to the database.
     */
    public CreatePaymentRR(final HttpServletRequest req, final HttpServletResponse res, Connection con) {
        super(Actions.CREATE_PAYMENT, req, res, con);
    }


    @Override
    protected void doServe() throws IOException {

        FullOrder order;
        Message m;

        try {
            LogContext.setResource(RESOURCE);

			order = new ListDishesFromOrderDAO(con, auth_user.getUser_id()).access().getOutputParam();

            if (order != null) {
				
				PaymentIntent pi = StripeService.createPayment(auth_user, order);
				
				PaymentResponse pr = new PaymentResponse(pi.getClientSecret());

                LOGGER.info("Payment Intent successfully created. PaymentIntentId: %s", pi.getId());

                res.setStatus(HttpServletResponse.SC_OK);
                res.setContentType(JSON_UTF_8_MEDIA_TYPE);
                pr.toJSON(res.getOutputStream());
            } else { // it should not happen
                LOGGER.error("Fatal error while creating the payment for the cart order.");
                m = new Message("Cannot create the payment the cart order: Unexpected error.", ErrorCodes.UNEXPECTED_DB_ERROR, null);
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                res.setContentType(JSON_UTF_8_MEDIA_TYPE);
                m.toJSON(res.getOutputStream());
            }
        } catch (StripeException ex) {
            LOGGER.error("Stripe Error: Cannot create the payment for the cart order.", ex);
            m = new Message("Stripe Error: Cannot create the payment for the cart order.", ErrorCodes.UNEXPECTED_STRIPE_ERROR, ex.getMessage());
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            m.toJSON(res.getOutputStream());
		} catch (SQLException ex) {
            LOGGER.error("Cannot create the payment for the cart order.", ex);
            m = new Message("Cannot create the payment for the cart order.", ErrorCodes.UNEXPECTED_DB_ERROR, ex.getMessage());
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            m.toJSON(res.getOutputStream());
        } catch (IOException ex){
			LOGGER.error("Cannot return the payment response object.", ex);
            m = new Message("Cannot return the payment response object.", ErrorCodes.UNEXPECTED_ERROR, ex.getMessage());
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            m.toJSON(res.getOutputStream());
		}
    }
}
