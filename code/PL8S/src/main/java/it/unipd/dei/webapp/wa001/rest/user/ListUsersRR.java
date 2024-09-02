package it.unipd.dei.webapp.wa001.rest.user;

import it.unipd.dei.webapp.wa001.database.user.ListUsersDAO;
import it.unipd.dei.webapp.wa001.resource.Message;
import it.unipd.dei.webapp.wa001.resource.ResourceList;
import it.unipd.dei.webapp.wa001.resource.ErrorCodes;
import it.unipd.dei.webapp.wa001.resource.dbentities.User;
import it.unipd.dei.webapp.wa001.resource.logging.Actions;
import it.unipd.dei.webapp.wa001.rest.AbstractRR;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * A REST resource for listing all the  attributes of a user {@link User}. *
 */

public final class ListUsersRR extends AbstractRR {

    /**
     * The JSON UTF-8 MIME media type
     */

    private static final String JSON_UTF_8_MEDIA_TYPE = "application/json; charset=utf-8";

    /**
     * Creates a new REST resource for listing {@code User}.
     *
     * @param req    the HTTP request.
     * @param res    the HTTP response.
     * @param con    the connection to the database.
     */
    public ListUsersRR(final HttpServletRequest req, final HttpServletResponse res, Connection con) {
        super(Actions.LIST_USERS, req, res, con);
    }

    @Override
    protected void doServe() throws IOException {

        List<User> ul = null;
        Message m = null;

        try {

            // creates a new DAO for accessing the database and lists the users(s)
            ul = new ListUsersDAO(con).access().getOutputParam();

            if (ul != null) {
                LOGGER.info("User(s) successfully listed.");

                res.setStatus(HttpServletResponse.SC_OK);
                res.setContentType(JSON_UTF_8_MEDIA_TYPE);
                new ResourceList("users", ul).toJSON(res.getOutputStream());
            } else { // it should not happen
                LOGGER.error("Fatal error while listing user(s).");

                m = new Message("Cannot list user(s): unexpected error.", ErrorCodes.UNEXPECTED_DB_ERROR, null);
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                res.setContentType(JSON_UTF_8_MEDIA_TYPE);
                m.toJSON(res.getOutputStream());
            }
        } catch (SQLException ex) {
            LOGGER.error("Cannot list user(s): unexpected database error.", ex);

            m = new Message("Cannot list user(s): unexpected database error.", ErrorCodes.UNEXPECTED_DB_ERROR, ex.getMessage());
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            m.toJSON(res.getOutputStream());
        }
    }
}
