package it.unipd.dei.webapp.wa001.database.user;

import it.unipd.dei.webapp.wa001.database.AbstractDAO;
import it.unipd.dei.webapp.wa001.resource.dbentities.Restaurant;
import it.unipd.dei.webapp.wa001.resource.dbentities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Lists all the {@link User}s in the database
 */
public final class ListUsersDAO extends AbstractDAO<List<User>> {

    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "SELECT * FROM Festival.select_users()";

    /**
     * Creates a new object for listing all the users.
     *
     * @param con the connection to the database.
     */

    public ListUsersDAO(Connection con) {
        super(con);
    }

    @Override
    protected void doAccess() throws Exception {
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        final List<User> users = new ArrayList<User>();

        try {
            pstmt = con.prepareStatement(STATEMENT);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                users.add(new User(rs.getInt("user_id"), rs.getString("email"), rs.getString("password"),
                        rs.getString("name"), rs.getString("surname"), rs.getString("stripe_id"), rs.getString("role")));
            }

            LOGGER.info("User(s) successfully listed from the database.");
        } finally {
            if (rs != null) {
                rs.close();
            }

            if (pstmt != null) {
                pstmt.close();
            }

        }

        outputParam = users;
    }
}
