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

package it.unipd.dei.webapp.wa001.rest.user;

import it.unipd.dei.webapp.wa001.resource.stripe.Card;
import it.unipd.dei.webapp.wa001.database.user.SelectUserDAO;
import it.unipd.dei.webapp.wa001.resource.dbentities.User;
import it.unipd.dei.webapp.wa001.resource.logging.Actions;
import it.unipd.dei.webapp.wa001.resource.logging.LogContext;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentMethodCollection;
import com.stripe.model.PaymentMethod;
import it.unipd.dei.webapp.wa001.resource.Message;
import it.unipd.dei.webapp.wa001.resource.StripeService;
import it.unipd.dei.webapp.wa001.rest.AbstractRR;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import it.unipd.dei.webapp.wa001.resource.ResourceList;
import it.unipd.dei.webapp.wa001.resource.ErrorCodes;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * A REST resource for registering a new user {@link RegisterRR}s. *
 */
public final class UserCardsRR extends AbstractRR {

    /**
     * The JSON UTF-8 MIME media type
     */
    private static final String JSON_UTF_8_MEDIA_TYPE = "application/json; charset=utf-8";

	/**
	 * The user cards resource.
	 */
	private static final String RESOURCE = "/user/cards";

    /**
     * Creates a new REST resource for registering a {@code User}.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @param con the connection to the database.
     */
    public UserCardsRR(final HttpServletRequest req, final HttpServletResponse res, Connection con) {
        super(Actions.LIST_USER_CARDS, req, res, con);
    }

    @Override
    protected void doServe() throws IOException {

        Message m = null;

        try {
            User user = null;

			LogContext.setResource(UserCardsRR.RESOURCE);
			LogContext.setAction(Actions.LIST_USER_CARDS);

            // creates a new DAO for accessing the database
            user = new SelectUserDAO(con, auth_user.getUser_id()).access().getOutputParam();

            if (user != null) {
				
				PaymentMethodCollection paymentMethods = StripeService.listPaymentMethods(user);

                res.setStatus(HttpServletResponse.SC_OK);
                res.setContentType(JSON_UTF_8_MEDIA_TYPE);

				List<Card> cards = new ArrayList<Card>();
		
				for (final PaymentMethod pm : paymentMethods.getData())
					cards.add(new Card(pm));

				new ResourceList("cards", cards).toJSON(res.getOutputStream());

            } else { // it should not happen
                LOGGER.error("Fatal error while retrieving user.");

                m = new Message("Cannot retrieve the user: Unexpected error.", ErrorCodes.UNEXPECTED_DB_ERROR, null);
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                res.setContentType(JSON_UTF_8_MEDIA_TYPE);
                m.toJSON(res.getOutputStream());
            }
        } catch (SQLException ex) {
            LOGGER.error("Cannot retrieve the user.", ex);

            m = new Message("Cannot retrieve the user.", ErrorCodes.UNEXPECTED_DB_ERROR, ex.getMessage());
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            m.toJSON(res.getOutputStream());
        } catch(StripeException ex){
			LOGGER.error("Cannot retrieve the user payment methods.", ex);

            m = new Message("Cannot retrieve the user payment methods.", ErrorCodes.UNEXPECTED_STRIPE_ERROR, ex.getMessage());
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            m.toJSON(res.getOutputStream());
		}
    }
}
