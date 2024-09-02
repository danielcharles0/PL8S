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

import it.unipd.dei.webapp.wa001.database.restaurant.SearchRestaurantByNameByCuisineDAO;
import it.unipd.dei.webapp.wa001.resource.Message;
import it.unipd.dei.webapp.wa001.resource.ResourceList;
import it.unipd.dei.webapp.wa001.resource.ErrorCodes;
import it.unipd.dei.webapp.wa001.resource.dbentities.Restaurant;
import it.unipd.dei.webapp.wa001.resource.logging.Actions;
import it.unipd.dei.webapp.wa001.resource.logging.LogContext;
import it.unipd.dei.webapp.wa001.rest.AbstractRR;
import it.unipd.dei.webapp.wa001.rest.dish.SearchDishesByRestaurantByNameByDietRR;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * A REST resource for searching {@link Restaurant}s.
 * <br> Possible URI patterns are:
 * <br> &emsp; {@code /restaurant/{restaurant_id}}
 * <br> &emsp; {@code /restaurants/{restaurant_id}}
 * <br> &emsp; {@code /restaurants/name/{name}/cuisine_type/{cuisine_type}}
 * <br> &emsp; {@code /restaurants/cuisine_type/{cuisine_type}}
 * <br> &emsp; {@code /restaurants/name/{name}}
 */
public final class SearchRestaurantByNameByCuisineRR extends AbstractRR {

    /**
     * The JSON UTF-8 MIME media type
     */
    private static final String JSON_UTF_8_MEDIA_TYPE = "application/json; charset=utf-8";

    /**
     * Creates a new REST resource for listing {@link  Restaurant}s.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @param con the connection to the database.
     */
    public SearchRestaurantByNameByCuisineRR(final HttpServletRequest req, final HttpServletResponse res, Connection con) {
        super(Actions.SEARCH_RESTAURANT_BY_NAME_BY_CUISINE, req, res, con);
    }

    @Override
    protected void doServe() throws IOException {

        List<Restaurant> el;
        Message m;
        String cuisType = null;
        String restName = null;
        Integer restaurant_id = null ;

        try {
            String [] tokens = req.getPathInfo().split("/");
//           Let's fetch all possible search parameters
            for(int i=0; i<tokens.length;i++){
                if (tokens[i].equals("restaurant")){
                    restaurant_id = Integer.parseInt(tokens[i+1]);
                }else if (tokens[i].equals("cuisine_type")){
                    cuisType = tokens[i+1];
                }else if (tokens[i].equals("name")){
                    restName = tokens[i+1];
                }
            }
            String resource;
            if (tokens[1].equals("restaurants")){
                resource = "/rest/restaurants";
            }else {
                resource = "/rest/restaurant";
            }
            LogContext.setResource(resource);
            // creates a new DAO for accessing the database and lists the restaurant(s)
            el = new SearchRestaurantByNameByCuisineDAO(con, restaurant_id, restName, cuisType).access().getOutputParam();

            if (el != null) {
                LOGGER.info("Restaurant(s) successfully listed.");

                res.setStatus(HttpServletResponse.SC_OK);
                res.setContentType(JSON_UTF_8_MEDIA_TYPE);
				
				if(restaurant_id != null){
					if(el.isEmpty()){
						LOGGER.error("Error while retrieving the restaurant. The restaurant(%d) does not exists.", restaurant_id);

		                m = new Message("Cannot retrieve the restaurant: the restaurant does not exist.", ErrorCodes.INVALID_INPUT_PARAMETER, null);
		                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		                res.setContentType(JSON_UTF_8_MEDIA_TYPE);
		                m.toJSON(res.getOutputStream());
					} else
						el.get(0).toJSON(res.getOutputStream());
				} else
                	new ResourceList("restaurants", el).toJSON(res.getOutputStream());
            } else { // it should not happen
                LOGGER.error("Fatal error while listing restaurant(s).");

                m = new Message("Cannot list restaurant(s): unexpected error.", ErrorCodes.UNEXPECTED_DB_ERROR, null);
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                res.setContentType(JSON_UTF_8_MEDIA_TYPE);
                m.toJSON(res.getOutputStream());
            }
        } catch (SQLException ex) {
            LOGGER.error("Cannot search for restaurant(s): unexpected database error.", ex);

            m = new Message("Cannot list restaurant(s): unexpected database error.", ErrorCodes.UNEXPECTED_DB_ERROR, ex.getMessage());
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            m.toJSON(res.getOutputStream());
        }catch (IndexOutOfBoundsException | NumberFormatException ex) {
            LOGGER.warn("Cannot search for the restaurant(s): wrong format", ex);

            m = new Message("Cannot search for the restaurant(s): wrong format", ErrorCodes.INVALID_INPUT_PARAMETER,
                    ex.getMessage());
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            m.toJSON(res.getOutputStream());
        }
    }


}
