package it.unipd.dei.webapp.wa001.database.user;

import it.unipd.dei.webapp.wa001.database.AbstractDAO;
import it.unipd.dei.webapp.wa001.resource.dbentities.User;

import java.sql.*;

/**
 * Selects a user in the database.
 */
public class SelectUserDAO extends AbstractDAO<User> {

    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "SELECT * FROM Festival.select_user_by_id(?)";

    /**
     * The user to be stored into the database
     */
    private final int user_id;

    /**
     * Creates a new object User that has been inserted in the database.
     *
     * @param con the connection to the database.
	 * @param user_id the user id of the user to select.
     */
    public SelectUserDAO(final Connection con, final int user_id) {
        super(con);

        this.user_id = user_id;
    }

    @Override
    protected final void doAccess() throws SQLException {

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User u_ret = null;

        try {
            pstmt = con.prepareStatement(STATEMENT);
			pstmt.setInt(1, user_id);

            rs = pstmt.executeQuery();

            rs.next();

            try{

                u_ret = new User(rs.getInt("user_id"), rs.getString("email"), rs.getString("password"), rs.getString("name"),
                        rs.getString("surname"), rs.getString("stripe_id"), rs.getString("role"));

            } catch (SQLException e){
                throw new SQLException("User not found.", e);
            }

            LOGGER.info("User (%d) successfully retrieved.", u_ret.getUser_id());
        } finally {
            if (rs != null) {
                rs.close();
            }

            if (pstmt != null) {
                pstmt.close();
            }

        }

        this.outputParam = u_ret;
    }
}
