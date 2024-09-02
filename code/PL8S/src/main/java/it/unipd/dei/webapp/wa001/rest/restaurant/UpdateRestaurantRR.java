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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import it.unipd.dei.webapp.wa001.database.cuisine.UpdateCuisinesDAO;
import it.unipd.dei.webapp.wa001.database.restaurant.UpdateRestaurantDAO;
import it.unipd.dei.webapp.wa001.resource.dbentities.Cuisine;
import it.unipd.dei.webapp.wa001.resource.dbentities.Restaurant;
import it.unipd.dei.webapp.wa001.resource.logging.Actions;
import it.unipd.dei.webapp.wa001.resource.Message;
import it.unipd.dei.webapp.wa001.resource.ErrorCodes;
import it.unipd.dei.webapp.wa001.rest.AbstractRR;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A REST resource for creating a restaurant {@link UpdateRestaurantRR}s. *
 */
public final class UpdateRestaurantRR extends AbstractRR {

    /**
     * The JSON UTF-8 MIME media type
     */
    private static final String JSON_UTF_8_MEDIA_TYPE = "application/json; charset=utf-8";

    /**
     * Creates a new REST resource for updating a {@code Restaurant}.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @param con the connection to the database.
     */
    public UpdateRestaurantRR(final HttpServletRequest req, final HttpServletResponse res, Connection con) {
        super(Actions.UPDATE_RESTAURANT, req, res, con);
    }

    @Override
    protected void doServe() throws IOException {

        Restaurant restaurant = null;
        List<Cuisine> cl = null;
        Message m = null;
        int manager, restaurant_id;

        try {
            // get restaurant_id from the RestaurantResourceFilter
            restaurant_id = req.getAttribute("restaurant_id") == null ? -1 : (int) req.getAttribute("restaurant_id");
            //if(restaurant_id <= 0)
            //    throw new NumberFormatException("The restaurant_id cannot be less or equal 0.");

            // restaurant_id in the URI already inserted in the object by RestaurantResourceFilter
            restaurant = Restaurant.fromJSON(req.getInputStream());

            // if request from admin accept manager modification
            if(auth_user.getRole().equals("admin"))
                manager = restaurant.getManager();
            else
                manager = auth_user.getUser_id();

            // update manager id and restaurant_id to the one of the authenticated manager that updates it
            restaurant = new Restaurant(restaurant_id, restaurant.getName(), restaurant.getDescription(),
                    manager, restaurant.getOpening_at(), restaurant.getClosing_at(),
                    restaurant.getCountries(), restaurant.getCuisine_types());

            // updates the restaurant
            restaurant = new UpdateRestaurantDAO(con, restaurant).access().getOutputParam();

            // create cuisine types
            List<String[]> cuisines = createCuisines(restaurant.getCountries(), restaurant.getCuisine_types());

            // create new connection
            InitialContext cxt = new InitialContext();
            DataSource ds = (DataSource) cxt.lookup("java:/comp/env/jdbc/pl8s");

            cl = new UpdateCuisinesDAO(ds.getConnection(), restaurant.getRestaurant_id(), cuisines).access().getOutputParam();

            if (restaurant != null) {
                LOGGER.info("Restaurant (%d) successfully updated.", restaurant.getRestaurant_id());

                res.setStatus(HttpServletResponse.SC_OK);
                res.setContentType(JSON_UTF_8_MEDIA_TYPE);
                writeRestaurantAndCuisinesJSON(res.getOutputStream(), restaurant, cl);
            } else { // it should not happen
                LOGGER.error("Fatal error while updating the restaurant.");

                m = new Message("Cannot updating the restaurant: Unexpected error.", ErrorCodes.UNEXPECTED_DB_ERROR, null);
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                res.setContentType(JSON_UTF_8_MEDIA_TYPE);
                m.toJSON(res.getOutputStream());
            }
        } catch (NumberFormatException ex) {
            LOGGER.error("Cannot update the restaurant: Invalid input parameters.", ex);

            m = new Message("Cannot update the restaurant: Invalid input parameters.", ErrorCodes.INVALID_INPUT_PARAMETER, ex.getMessage());
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            m.toJSON(res.getOutputStream());
        } catch (SQLException ex) {
            LOGGER.error("Cannot update the restaurant.", ex);

            m = new Message("Cannot update the restaurant.", ErrorCodes.UNEXPECTED_DB_ERROR, ex.getMessage());
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            m.toJSON(res.getOutputStream());
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create list of cuisine types associated to restaurant to be added to the database.
     *
     * @param countries array of countries.
     * @param cuisine_types array of cuisine types.
     * @return list of cuisine types provided by the restaurant.
     */
    private List<String[]> createCuisines(String[] countries, String[] cuisine_types){

        List<String[]> cl = new ArrayList<>();
        int i = 0;
        if(countries.length >= cuisine_types.length){
            for(; i < cuisine_types.length; i++)
                cl.add(new String[]{countries[i], cuisine_types[i]});
            for(; i < countries.length; i++)
                cl.add(new String[]{countries[i], null});
        } else {
            for(; i < countries.length; i++)
                cl.add(new String[]{countries[i], cuisine_types[i]});
            for(; i < cuisine_types.length; i++)
                cl.add(new String[]{null, cuisine_types[i]});
        }

        return cl;
    }

    private void writeRestaurantAndCuisinesJSON(OutputStream out, Restaurant restaurant, List<Cuisine> cuisines) throws IOException{

        if(out == null) {
            LOGGER.error("The output stream cannot be null.");
            throw new IOException("The output stream cannot be null.");
        }

        JsonFactory jf = new JsonFactory();
        jf.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
        jf.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);

        final JsonGenerator jg = jf.createGenerator(out);

        jg.writeStartObject();

        jg.writeFieldName("restaurant");

        jg.writeStartObject();

        jg.writeNumberField("restaurant_id", restaurant.getRestaurant_id());

        jg.writeStringField("name", restaurant.getName());

        jg.writeStringField("description", restaurant.getDescription());

        jg.writeNumberField("manager", restaurant.getManager());

        jg.writeStringField("opening_at", restaurant.getOpening_at().toString());

        jg.writeStringField("closing_at", restaurant.getClosing_at().toString());

        jg.writeFieldName("countries");

        jg.writeArray(restaurant.getCountries(), 0, restaurant.getCountries().length);

        jg.writeFieldName("cuisine_types");

        jg.writeArray(restaurant.getCuisine_types(), 0, restaurant.getCuisine_types().length);

        jg.writeEndObject();

        //print cuisines
        for(Cuisine c : cuisines){

            jg.writeFieldName("cuisine");

            jg.writeStartObject();

            jg.writeNumberField("cuisine_id", c.getCuisine_id());

            jg.writeStringField("type", c.getType());

            jg.writeStringField("country", c.getCountry());

            jg.writeNumberField("restaurant", c.getRestaurant());

            jg.writeEndObject();

        }

        jg.writeEndObject();

        jg.flush();

    }

}
