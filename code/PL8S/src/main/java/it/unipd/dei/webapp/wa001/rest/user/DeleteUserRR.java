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

import it.unipd.dei.webapp.wa001.database.user.DeleteUserDAO;
import it.unipd.dei.webapp.wa001.resource.dbentities.User;
import it.unipd.dei.webapp.wa001.resource.logging.Actions;
import it.unipd.dei.webapp.wa001.resource.Message;
import it.unipd.dei.webapp.wa001.resource.ErrorCodes;
import it.unipd.dei.webapp.wa001.rest.AbstractRR;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * A REST resource for deleting a user {@link DeleteUserRR}s. *
 */
public final class DeleteUserRR extends AbstractRR {

    /**
     * The JSON UTF-8 MIME media type
     */
    private static final String JSON_UTF_8_MEDIA_TYPE = "application/json; charset=utf-8";

    /**
     * Creates a new REST resource for deleting a {@code User}.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @param con the connection to the database.
     */
    public DeleteUserRR(final HttpServletRequest req, final HttpServletResponse res, Connection con) {
        super(Actions.DELETE_USER, req, res, con);
    }


    @Override
    protected void doServe() throws IOException {

        User user = null;
        Message m = null;

        try {
            // parse the URL to extract the user_id
            String pathinfo = req.getPathInfo();
            String[] tokens = pathinfo.split("/");
            // rest/user/delete/{user_id} so it is the fourth one
            int user_id  = Integer.parseInt(tokens[3]);

			LOGGER.info("Deleting the user (%d) ...", user_id);

            // creates a new DAO for accessing the database
            user = new DeleteUserDAO(con, user_id).access().getOutputParam();

            if (user != null) {
                LOGGER.info("User (%d) successfully deleted.", user.getUser_id());

                res.setStatus(HttpServletResponse.SC_OK);
                res.setContentType(JSON_UTF_8_MEDIA_TYPE);
                user.toJSON(res.getOutputStream());
            } else { // it should not happen
                LOGGER.error("Fatal error while deleting the user.");

                m = new Message("Cannot delete the user: Unexpected error.", ErrorCodes.UNEXPECTED_DB_ERROR, null);
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                res.setContentType(JSON_UTF_8_MEDIA_TYPE);
                m.toJSON(res.getOutputStream());
            }
        } catch (NumberFormatException ex) {
            LOGGER.error("Cannot delete the user: Invalid input parameters.", ex);

            m = new Message("Cannot delete the user: Invalid input parameters.", ErrorCodes.INVALID_INPUT_PARAMETER, ex.getMessage());
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            m.toJSON(res.getOutputStream());
        } catch (SQLException ex) {
            LOGGER.error("Cannot delete the user.", ex);

            m = new Message("Cannot delete the user.", ErrorCodes.UNEXPECTED_DB_ERROR, ex.getMessage());
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            m.toJSON(res.getOutputStream());
        }
    }
}
