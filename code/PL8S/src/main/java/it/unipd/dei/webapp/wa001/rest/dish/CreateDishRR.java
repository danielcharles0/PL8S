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

package it.unipd.dei.webapp.wa001.rest.dish;

import it.unipd.dei.webapp.wa001.database.dish.CreateDishDAO;
import it.unipd.dei.webapp.wa001.database.ingredient.UpdateIngredientsDAO;
import it.unipd.dei.webapp.wa001.resource.dbentities.*;
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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A REST resource for creating a dish {@link CreateDishRR}s. *
 */
public final class CreateDishRR extends AbstractRR {

    /**
     * The JSON UTF-8 MIME media type
     */
    private static final String JSON_UTF_8_MEDIA_TYPE = "application/json; charset=utf-8";

    /**
     * Creates a new REST resource for creating a {@code Dish}.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @param con the connection to the database.
     */
    public CreateDishRR(final HttpServletRequest req, final HttpServletResponse res, Connection con) {
        super(Actions.CREATE_DISH, req, res, con);
    }

    @Override
    protected void doServe() throws IOException {

        DishIngredient dish_ing = null;
        Dish dish = null;
        List<String[]> il = new ArrayList<>();
        Message m = null;

        try {
            dish_ing = (DishIngredient) req.getAttribute("dish");
            dish = new Dish(-1, dish_ing.getPrice(), dish_ing.getName(), dish_ing.isDeleted(), dish_ing.getRestaurant());

            // creates a new DAO for accessing the database
            dish = new CreateDishDAO(con, dish).access().getOutputParam();

            // create list of ingredients (name, diet)
            for(Ingredient ing : dish_ing.getIngredients())
                il.add(new String[]{ing.getName(), ing.getDiet()});

            // create new connection
            InitialContext cxt = new InitialContext();
            DataSource ds = (DataSource) cxt.lookup("java:/comp/env/jdbc/pl8s");

            // create/update ingredients
            List<Ingredient> ings = new UpdateIngredientsDAO(ds.getConnection(), dish.getDish_id(), il).access().getOutputParam();

            // collect all the ingredients
            Ingredient[] ingredients = new Ingredient[ings.size()];
            ings.toArray(ingredients);

            dish_ing = new DishIngredient(dish.getDish_id(), dish.getPrice(), dish.getName(), dish.isDeleted(),
                    dish.getRestaurant(), ingredients);

            if (dish != null) {
                LOGGER.info("Dish (%d) successfully created.", dish.getDish_id());

                res.setStatus(HttpServletResponse.SC_OK);
                res.setContentType(JSON_UTF_8_MEDIA_TYPE);
                dish_ing.toJSON(res.getOutputStream());
            } else { // it should not happen
                LOGGER.error("Fatal error while creating the dish.");

                m = new Message("Cannot create the dish: Unexpected error.", ErrorCodes.UNEXPECTED_DB_ERROR, null);
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                res.setContentType(JSON_UTF_8_MEDIA_TYPE);
                m.toJSON(res.getOutputStream());
            }
        } catch (NumberFormatException ex) {
            LOGGER.error("Cannot create the dish: Invalid input parameters.", ex);

            m = new Message("Cannot create the dish: Invalid input parameters.", ErrorCodes.INVALID_INPUT_PARAMETER, ex.getMessage());
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            m.toJSON(res.getOutputStream());
        } catch (SQLException ex) {
            LOGGER.error("Cannot create the dish.", ex);

            m = new Message("Cannot create the dish.", ErrorCodes.UNEXPECTED_DB_ERROR, ex.getMessage());
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            m.toJSON(res.getOutputStream());
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }
}
