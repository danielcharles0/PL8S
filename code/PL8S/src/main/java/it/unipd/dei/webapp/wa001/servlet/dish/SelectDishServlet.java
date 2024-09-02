/*
 * Copyright 2018 University of Padua, Italy
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

package it.unipd.dei.webapp.wa001.servlet.dish;

import it.unipd.dei.webapp.wa001.database.dish.SelectDishDAO;
import it.unipd.dei.webapp.wa001.resource.logging.Actions;
import it.unipd.dei.webapp.wa001.resource.dbentities.DishIngredient;
import it.unipd.dei.webapp.wa001.resource.logging.LogContext;
import it.unipd.dei.webapp.wa001.resource.Message;
import it.unipd.dei.webapp.wa001.resource.ErrorCodes;
import it.unipd.dei.webapp.wa001.servlet.AbstractDatabaseServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.message.StringFormattedMessage;

import io.jsonwebtoken.lang.Arrays;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Selects a dish from the the database.
 *
 * @author Luigi Frigione (luigi.frigione@dei.unipd.it)
 * @version 1.00
 * @since 1.00
 */
public final class SelectDishServlet extends AbstractDatabaseServlet {

	/**
	 * Selects a dish from the the database.
	 *
	 * @param req the HTTP request from the client.
	 * @param res the HTTP response from the server.
	 *
	 * @throws IOException if any error occurs in the client/server communication.
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {

		LogContext.setIPAddress(req.getRemoteAddr());
		LogContext.setAction(Actions.SELECT_DISH);

		// request parameters
		int dish_id = -1;
		String url = req.getRequestURL().toString();
		// model
		DishIngredient dish = null;
		Message m = null;

		try {
			
			List<String> tokens = Arrays.asList(url.split("/"));
			// retrieves the request parameters
			dish_id = Integer.parseInt(tokens.get(tokens.indexOf("dish") + 1));

			// set the dish as the resource in the log context
			// at this point we know it is a valid integer
			LogContext.setResource("Dish(" + dish_id + ")");

			// creates a new object for accessing the database and stores the employee
			dish = new SelectDishDAO(getConnection(), dish_id).access().getOutputParam();
			
			m = new Message(String.format("Dish %d successfully selected.", dish_id));

			LOGGER.info("Dish %d successfully selected from the database.", dish_id);

		} catch (NumberFormatException ex) {
			m = new Message(
					"Cannot select the dish. Invalid input parameter: dish_id must be integer.",
					ErrorCodes.INVALID_INPUT_PARAMETER, ex.getMessage());

			LOGGER.error(
					"Cannot select the dish. Invalid input parameter: dish_id must be integer.",
					ex);
		} catch (SQLException ex) {
			
			m = new Message("Cannot select the dish: unexpected error while accessing the database.", ErrorCodes.UNEXPECTED_DB_ERROR,
					ex.getMessage());

			LOGGER.error("Cannot select the dish: unexpected error while accessing the database.", ex);
			
		}

		try {
			// stores the dish and the message as a request attribute
			req.setAttribute("dish", dish);
			req.setAttribute("message", m);

			// forwards the control to the create-employee-result JSP
			if(url.contains("edit"))
				req.getRequestDispatcher("/jsp/dish/edit-dish.jsp").forward(req, res);
			else
				req.getRequestDispatcher("/jsp/dish/dish.jsp").forward(req, res);
		} catch (Exception ex) {
			LOGGER.error(new StringFormattedMessage("Unable to send response when selecting the dish %d.", dish_id), ex);
		} finally {
			LogContext.removeIPAddress();
			LogContext.removeAction();
			LogContext.removeResource();
		}

	}

}
