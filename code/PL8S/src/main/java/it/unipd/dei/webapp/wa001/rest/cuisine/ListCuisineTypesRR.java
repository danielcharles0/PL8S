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

package it.unipd.dei.webapp.wa001.rest.cuisine;

import it.unipd.dei.webapp.wa001.database.cuisine.ListCuisineTypesDAO;
import it.unipd.dei.webapp.wa001.resource.fields.CuisineType;
import it.unipd.dei.webapp.wa001.resource.Message;
import it.unipd.dei.webapp.wa001.resource.ResourceList;
import it.unipd.dei.webapp.wa001.resource.ErrorCodes;
import it.unipd.dei.webapp.wa001.resource.logging.Actions;
import it.unipd.dei.webapp.wa001.resource.logging.LogContext;
import it.unipd.dei.webapp.wa001.rest.AbstractRR;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * A REST resource for listing {@code Cuisine} types. *
 */
public final class ListCuisineTypesRR extends AbstractRR {

	/**
	 * The JSON UTF-8 MIME media type
	 */
	private static final String JSON_UTF_8_MEDIA_TYPE = "application/json; charset=utf-8";

	/**
	 * The list Cuisine types resource.
	 */
	private static final String RESOURCE = "/rest/cuisine-types";

	/**
	 * Creates a new REST resource for listing {@code Cuisine} types.
	 *
	 * @param req the HTTP request.
	 * @param res the HTTP response.
	 * @param con the connection to the database.
	 */
	public ListCuisineTypesRR(final HttpServletRequest req, final HttpServletResponse res, Connection con) {
		super(Actions.LIST_CUISINE_TYPES, req, res, con);
	}
	@Override
	protected void doServe() throws IOException {

		List<CuisineType> el = null;
		Message m = null;

		try {
			LogContext.setResource(RESOURCE);
			// creates a new DAO for accessing the database and lists the cuisine types
			el = new ListCuisineTypesDAO(con).access().getOutputParam();

			if (el != null) {
				LOGGER.info("Cuisine types successfully listed.");

				res.setStatus(HttpServletResponse.SC_OK);
				res.setContentType(JSON_UTF_8_MEDIA_TYPE);
				new ResourceList("cuisinetypes", el).toJSON(res.getOutputStream());
			} else { // it should not happen
				LOGGER.error("Fatal error while listing cuisine types");

				m = new Message("Cannot list cuisine types: unexpected error.", ErrorCodes.UNEXPECTED_DB_ERROR, null);
				res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				res.setContentType(JSON_UTF_8_MEDIA_TYPE);
				m.toJSON(res.getOutputStream());
			}
		} catch (SQLException ex) {
			LOGGER.error("Cannot list cuisine types: unexpected database error.", ex);

			m = new Message("Cannot list cuisine types: unexpected database error.", ErrorCodes.UNEXPECTED_DB_ERROR, ex.getMessage());
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType(JSON_UTF_8_MEDIA_TYPE);
			m.toJSON(res.getOutputStream());
		}
	}


}
