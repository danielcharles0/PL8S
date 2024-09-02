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
import it.unipd.dei.webapp.wa001.database.cuisine.CreateCuisinesDAO;
import it.unipd.dei.webapp.wa001.database.restaurant.CreateRestaurantDAO;
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
 * A REST resource for creating a restaurant {@link CreateRestaurantRR}s. *
 */
public final class CreateRestaurantRR extends AbstractRR {

    /**
     * The JSON UTF-8 MIME media type
     */
    private static final String JSON_UTF_8_MEDIA_TYPE = "application/json; charset=utf-8";

    /**
     * Creates a new REST resource for creating a {@code Restaurant}.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @param con the connection to the database.
     */
    public CreateRestaurantRR(final HttpServletRequest req, final HttpServletResponse res, Connection con) {
        super(Actions.CREATE_RESTAURANT, req, res, con);
    }

    @Override
    protected void doServe() throws IOException {

        Restaurant restaurant = null;
        List<Cuisine> cl = null;
        Message m = null;

        try {
            restaurant = Restaurant.fromJSON(req.getInputStream());

            // update manager id to the one of the authenticated manager that creates it
            restaurant = new Restaurant(restaurant.getRestaurant_id(), restaurant.getName(), restaurant.getDescription(),
                    auth_user.getUser_id(), restaurant.getOpening_at(), restaurant.getClosing_at(),
                    restaurant.getCountries(), restaurant.getCuisine_types());

            // creates a new restaurant
            restaurant = new CreateRestaurantDAO(con, restaurant).access().getOutputParam();

            // create cuisines
            List<Cuisine> cuisines = createCuisines(restaurant.getCountries(), restaurant.getCuisine_types(), restaurant.getRestaurant_id());

            // create new connection
            InitialContext cxt = new InitialContext();
            DataSource ds = (DataSource) cxt.lookup("java:/comp/env/jdbc/pl8s");

            cl = new CreateCuisinesDAO(ds.getConnection(), cuisines).access().getOutputParam();

            if (restaurant != null) {
                LOGGER.info("Restaurant (%d) successfully created.", restaurant.getRestaurant_id());

                res.setStatus(HttpServletResponse.SC_OK);
                res.setContentType(JSON_UTF_8_MEDIA_TYPE);
				restaurant.toJSON(res.getOutputStream());
                // writeRestaurantAndCuisinesJSON(res.getOutputStream(), restaurant, cl);
            } else { // it should not happen
                LOGGER.error("Fatal error while creating the restaurant.");

                m = new Message("Cannot creating the restaurant: Unexpected error.", ErrorCodes.UNEXPECTED_DB_ERROR, null);
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                res.setContentType(JSON_UTF_8_MEDIA_TYPE);
                m.toJSON(res.getOutputStream());
            }
        } catch (NumberFormatException ex) {
            LOGGER.error("Cannot create the restaurant: Invalid input parameters.", ex);

            m = new Message("Cannot create the restaurant: Invalid input parameters.", ErrorCodes.INVALID_INPUT_PARAMETER, ex.getMessage());
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            m.toJSON(res.getOutputStream());
        } catch (SQLException ex) {
            LOGGER.error("Cannot create the restaurant.", ex);

            m = new Message("Cannot create the restaurant.", ErrorCodes.UNEXPECTED_DB_ERROR, ex.getMessage());
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            m.toJSON(res.getOutputStream());
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create list of cuisines associated to restaurant to be added to the database.
     *
     * @param countries array of countries.
     * @param cuisine_types array of cuisine types.
     * @param restaurant_id id of the restaurant described by countries and cuisine types.
     * @return list of cuisines provided by the restaurant.
     */
    private List<Cuisine> createCuisines(String[] countries, String[] cuisine_types, int restaurant_id){

        List<Cuisine> cl = new ArrayList<>();
        int i = 0;
        if(countries.length >= cuisine_types.length){
            for(; i < cuisine_types.length; i++)
                cl.add(new Cuisine(-1, cuisine_types[i], countries[i], restaurant_id));
            for(; i < countries.length; i++)
                cl.add(new Cuisine(-1, null, countries[i], restaurant_id));
        } else {
            for(; i < countries.length; i++)
                cl.add(new Cuisine(-1, cuisine_types[i], countries[i], restaurant_id));
            for(; i < cuisine_types.length; i++)
                cl.add(new Cuisine(-1, cuisine_types[i], null, restaurant_id));
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
