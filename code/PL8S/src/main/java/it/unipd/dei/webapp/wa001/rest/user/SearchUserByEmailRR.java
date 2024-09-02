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

import it.unipd.dei.webapp.wa001.database.user.SearchUserByEmailDAO;
import it.unipd.dei.webapp.wa001.resource.Message;
import it.unipd.dei.webapp.wa001.resource.ResourceList;
import it.unipd.dei.webapp.wa001.resource.ErrorCodes;
import it.unipd.dei.webapp.wa001.resource.dbentities.User;
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
 * A REST resource for listing {@link User}s.
 */
public final class SearchUserByEmailRR extends AbstractRR {

	/**
	 * The JSON UTF-8 MIME media type
	 */
	private static final String JSON_UTF_8_MEDIA_TYPE = "application/json; charset=utf-8";
	private static final String RESOURCE = "rest/user";
	/**
	 * Creates a new REST resource for listing {@code User}s.
	 *
	 * @param req the HTTP request.
	 * @param res the HTTP response.
	 * @param con the connection to the database.
	 */
	public SearchUserByEmailRR(final HttpServletRequest req, final HttpServletResponse res, Connection con) {
		super(Actions.SEARCH_USER_BY_EMAIL, req, res, con);
	}

	@Override
	protected void doServe() throws IOException {

		List<User> ul = null;
		Message m = null;

		try {
			// parse the URL to extract the email address to search for
			String path = req.getRequestURI();
			path = path.substring(path.lastIndexOf("email") + 5);

			String email = path.substring(1);

			LogContext.setResource(RESOURCE);

			// creates a new DAO for accessing the database and lists the user(s)
			ul = new SearchUserByEmailDAO(con, email).access().getOutputParam();

			if (ul != null) {
				LOGGER.info("User(s) successfully listed.");

				res.setStatus(HttpServletResponse.SC_OK);
				res.setContentType(JSON_UTF_8_MEDIA_TYPE);
				new ResourceList("users", ul).toJSON(res.getOutputStream());
			} else { // it should not happen
				LOGGER.error("Fatal error while searching user(s).");

				m = new Message("Cannot search users(s): unexpected error.", ErrorCodes.UNEXPECTED_DB_ERROR, null);
				res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				res.setContentType(JSON_UTF_8_MEDIA_TYPE);
				m.toJSON(res.getOutputStream());
			}
		} catch (IndexOutOfBoundsException | NumberFormatException ex) {
			LOGGER.warn("Cannot search user(s) by email: wrong format for URI /user/email/{email}.", ex);

			m = new Message("Cannot search employee(s): wrong format for URI /user/email/{email}.", ErrorCodes.INVALID_INPUT_PARAMETER,
					ex.getMessage());
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			m.toJSON(res.getOutputStream());
		} catch (SQLException ex) {
			LOGGER.error("Cannot search for user(s): unexpected database error.", ex);

			m = new Message("Cannot search for user(s): unexpected database error.", ErrorCodes.UNEXPECTED_DB_ERROR, ex.getMessage());
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentType(JSON_UTF_8_MEDIA_TYPE);
			m.toJSON(res.getOutputStream());
		}
	}


}
