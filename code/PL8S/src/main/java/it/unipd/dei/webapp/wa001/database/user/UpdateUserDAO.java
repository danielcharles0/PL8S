package it.unipd.dei.webapp.wa001.database.user;

import it.unipd.dei.webapp.wa001.database.AbstractDAO;
import it.unipd.dei.webapp.wa001.resource.dbentities.User;

import java.sql.*;

/**
 * Updates a user in the database.
 */
public class UpdateUserDAO extends AbstractDAO<User> {

    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "SELECT * FROM Festival.update_user(?,?,?,?,?)";

    /**
     * The user to be updated into the database
     */
    private final User user;

    /**
     * Updates all user attributes provided by the {@link User}s object.
     *
     * @param con the connection to the database.
     * @param user the User to be updated
     */
    public UpdateUserDAO(final Connection con, final User user) {
        super(con);

        if (user == null) {
            LOGGER.error("The user cannot be null.");
            throw new NullPointerException("The user cannot be null.");
        }

        this.user = user;
    }

    @Override
    protected final void doAccess() throws SQLException {

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User u_ret = null;

        try {
            pstmt = con.prepareStatement(STATEMENT);
            pstmt.setInt(1, user.getUser_id());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getName());
            pstmt.setString(5, user.getSurname());

            rs = pstmt.executeQuery();

            rs.next();

            u_ret = new User(rs.getInt("user_id"), rs.getString("email"),  rs.getString("password"), rs.getString("name"),
                    rs.getString("surname"), rs.getString("stripe_id"), rs.getString("role"));

            LOGGER.info("User %d successfully updated: email: %s, Name: %s, Surname: %s, Role: %s"
                    , u_ret.getUser_id(), u_ret.getEmail(), u_ret.getName(), u_ret.getSurname(), u_ret.getRole());
        } finally {

            if (pstmt != null) {
                pstmt.close();
            }

        }
        this.outputParam = u_ret;
    }
}
