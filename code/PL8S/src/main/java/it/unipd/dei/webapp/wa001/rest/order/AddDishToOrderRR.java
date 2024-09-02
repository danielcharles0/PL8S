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

import it.unipd.dei.webapp.wa001.database.order.AddDishToOrderDAO;
import it.unipd.dei.webapp.wa001.resource.dbentities.OrderDish;
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

/**
 * A REST resource for adding a dish to an order {@link AddDishToOrderRR}s. *
 */
public final class AddDishToOrderRR extends AbstractRR {

    /**
     * The JSON UTF-8 MIME media type
     */
    private static final String JSON_UTF_8_MEDIA_TYPE = "application/json; charset=utf-8";

    /**
     * The add dish to order resource.
     */
    private static final String RESOURCE = "/rest/order";

    /**
     * Creates a new REST resource for adding a {@code Dish} to order.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @param con the connection to the database.
     */
    public AddDishToOrderRR(final HttpServletRequest req, final HttpServletResponse res, Connection con) {
        super(Actions.ADD_DISH_TO_ORDER, req, res, con);
    }


    @Override
    protected void doServe() throws IOException {

        OrderDish od = null;
        Message m = null;

        try {
            // parse the URL to extract the dish_id
            String pathinfo = req.getPathInfo();
            String[] tokens = pathinfo.split("/");
            // /order/dishes/{dish_id} so it is the fourth one
            int dish_id  = Integer.parseInt(tokens[3]);

            LogContext.setResource(RESOURCE);

            // creates a new DAO for accessing the database
            od = new AddDishToOrderDAO(con, auth_user.getUser_id(), dish_id).access().getOutputParam();

            if (od != null) {
                res.setStatus(HttpServletResponse.SC_OK);
                res.setContentType(JSON_UTF_8_MEDIA_TYPE);
                od.toJSON(res.getOutputStream());
            } else { // it should not happen
                LOGGER.error("Fatal error while creating OrderDish.");

                m = new Message("Cannot create the OrderDish: Unexpected error.", ErrorCodes.UNEXPECTED_DB_ERROR, null);
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                res.setContentType(JSON_UTF_8_MEDIA_TYPE);
                m.toJSON(res.getOutputStream());
            }
        } catch (NumberFormatException ex) {
            LOGGER.error("Cannot create the OrderDish: Invalid input parameters.", ex);

            m = new Message("Cannot create the OrderDish: Invalid input parameters.", ErrorCodes.INVALID_INPUT_PARAMETER, ex.getMessage());
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            m.toJSON(res.getOutputStream());
        } catch (SQLException ex) {
            LOGGER.error("Cannot create the OrderDish.", ex);

            m = new Message("Cannot create the OrderDish.", ErrorCodes.UNEXPECTED_DB_ERROR, ex.getMessage());
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            m.toJSON(res.getOutputStream());
        }
    }
}