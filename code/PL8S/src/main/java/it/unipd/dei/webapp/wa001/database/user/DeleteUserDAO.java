package it.unipd.dei.webapp.wa001.database.user;

import it.unipd.dei.webapp.wa001.database.AbstractDAO;
import it.unipd.dei.webapp.wa001.resource.dbentities.User;

import java.sql.*;

/**
 * Delete user from the platform
 */
public class DeleteUserDAO extends AbstractDAO<User> {

    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "SELECT * FROM Festival.delete_user(?)";

    /**
     * The id of the user to be deleted from the database
     */
    private final int user_id;

    /**
     * Creates a new object User that has been deleted from the database.
     *
     * @param con the connection to the database.
	 * @param user_id the id of the user to delete.
     */
    public DeleteUserDAO(final Connection con, final int user_id) {
        super(con);

        if (user_id <= 1) {
            LOGGER.error("The user cannot be less or equal to 1.");
            throw new NullPointerException("The user cannot be less or equal to 1.");
        }

        this.user_id = user_id;
    }

    @Override
    protected final void doAccess() throws SQLException {

        PreparedStatement pstmt = null;
        User u_ret = null;
        ResultSet rs = null;

        try {
            pstmt = con.prepareCall(STATEMENT);
            pstmt.setInt(1, user_id);

            rs = pstmt.executeQuery();

            rs.next();

            u_ret = new User(rs.getInt("user_id"), rs.getString("email"), rs.getString("password"), rs.getString("name"),
                    rs.getString("surname"), rs.getString("stripe_id"), rs.getString("role"));

            LOGGER.info("User (%d) successfully deleted.", u_ret.getUser_id());

        } catch(SQLException e) {
            throw new SQLException("User not found.", e);
        }    finally{

            if (pstmt != null) {
                pstmt.close();
            }

        }

        this.outputParam = u_ret;
    }
}
