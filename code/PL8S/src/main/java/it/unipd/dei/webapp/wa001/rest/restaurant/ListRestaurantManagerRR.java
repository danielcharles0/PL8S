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

package it.unipd.dei.webapp.wa001.rest.restaurant;

import it.unipd.dei.webapp.wa001.database.restaurant.ListRestaurantManagerDAO;
import it.unipd.dei.webapp.wa001.rest.AbstractRR;
import it.unipd.dei.webapp.wa001.resource.logging.Actions;
import it.unipd.dei.webapp.wa001.resource.dbentities.Restaurant;
import it.unipd.dei.webapp.wa001.resource.Message;
import it.unipd.dei.webapp.wa001.resource.ResourceList;
import it.unipd.dei.webapp.wa001.resource.ErrorCodes;
import it.unipd.dei.webapp.wa001.resource.logging.LogContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * A REST resource for listing {@link Restaurant}s owned by a specific manager or all the {@link Restaurant}s if requested by the admin.
 */
public final class ListRestaurantManagerRR extends AbstractRR {

    /**
     * The JSON UTF-8 MIME media type
     */
    private static final String JSON_UTF_8_MEDIA_TYPE = "application/json; charset=utf-8";

    /**
     * The list restaurants resource.
     */
    private static final String RESOURCE = "/rest/restaurants/manager";

    /**
     * Creates a new REST resource for listing {@link  Restaurant}s.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @param con the connection to the database.
     */
    public ListRestaurantManagerRR(final HttpServletRequest req, final HttpServletResponse res, Connection con) {
        super(Actions.LIST_RESTAURANT, req, res, con);
    }

    @Override
    protected void doServe() throws IOException {

        List<Restaurant> rl = null;
        Message m = null;

        try {

            LogContext.setResource(ListRestaurantManagerRR.RESOURCE);

            // creates a new DAO for accessing the database and lists the restaurant(s)
            rl = new ListRestaurantManagerDAO(con, auth_user.getUser_id()).access().getOutputParam();

            if (rl != null) {
                LOGGER.info("Restaurant(s) successfully listed.");

                res.setStatus(HttpServletResponse.SC_OK);
                res.setContentType(JSON_UTF_8_MEDIA_TYPE);
                new ResourceList("restaurants", rl).toJSON(res.getOutputStream());
            } else { // it should not happen
                LOGGER.error("Fatal error while listing restaurant(s).");

                m = new Message("Cannot list restaurant(s): unexpected error.", ErrorCodes.UNEXPECTED_DB_ERROR, null);
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                res.setContentType(JSON_UTF_8_MEDIA_TYPE);
                m.toJSON(res.getOutputStream());
            }
        } catch (SQLException ex) {
            LOGGER.error("Cannot list restaurant(s): unexpected database error.", ex);

            m = new Message("Cannot list restaurant(s): unexpected database error.", ErrorCodes.UNEXPECTED_DB_ERROR, ex.getMessage());
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            m.toJSON(res.getOutputStream());
        }
    }


}
