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

import it.unipd.dei.webapp.wa001.database.order.CompleteOrderDAO;
import it.unipd.dei.webapp.wa001.resource.dbentities.Order;
import it.unipd.dei.webapp.wa001.resource.logging.Actions;
import it.unipd.dei.webapp.wa001.resource.Message;
import it.unipd.dei.webapp.wa001.resource.ErrorCodes;
import it.unipd.dei.webapp.wa001.resource.logging.LogContext;
import it.unipd.dei.webapp.wa001.rest.AbstractRR;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import com.stripe.model.PaymentIntent;

/**
 * A REST resource for completing an Order {@link CompleteOrderRR}s. *
 */
public final class CompleteOrderRR extends AbstractRR {

    /**
     * The JSON UTF-8 MIME media type
     */
    private static final String JSON_UTF_8_MEDIA_TYPE = "application/json; charset=utf-8";

    /**
     * The complete order resource.
     */
    private static final String RESOURCE = "/rest/order/payment";

    /**
     * Creates a new REST resource for completing an {@code Order}.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @param con the connection to the database.
     */
    public CompleteOrderRR(final HttpServletRequest req, final HttpServletResponse res, Connection con) {
        super(Actions.COMPLETE_ORDER, req, res, con);
    }


    @Override
    protected void doServe() throws IOException {

        Order order = null;
        Message m = null;

        try {
            LogContext.setResource(RESOURCE);

			PaymentIntent pi = (PaymentIntent) req.getAttribute("pi");
			int user_id = Integer.parseInt(pi.getMetadata().get("user_id"));

			LOGGER.info("Confirming the order %s for the User(%d)", pi.getId(), user_id);

            // creates a new DAO for accessing the database
            order = new CompleteOrderDAO(con, user_id).access().getOutputParam();

            if (order != null) {
                LOGGER.info("Order successfully completed. Order: %d", order.getOrder_id());

                res.setStatus(HttpServletResponse.SC_OK);
                res.setContentType(JSON_UTF_8_MEDIA_TYPE);
                order.toJSON(res.getOutputStream());
            } else { // it should not happen
                LOGGER.error("Fatal error while completing the Order.");

                m = new Message("Cannot complete the Order: Unexpected error.", ErrorCodes.UNEXPECTED_DB_ERROR, null);
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                res.setContentType(JSON_UTF_8_MEDIA_TYPE);
                m.toJSON(res.getOutputStream());
            }
        } catch (SQLException ex) {
            LOGGER.error("Cannot complete the Order.", ex);

            m = new Message("Cannot complete the Order.", ErrorCodes.UNEXPECTED_DB_ERROR, ex.getMessage());
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            m.toJSON(res.getOutputStream());
        }
    }
}
