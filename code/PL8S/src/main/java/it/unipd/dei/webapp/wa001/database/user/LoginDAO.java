package it.unipd.dei.webapp.wa001.database.user;

import it.unipd.dei.webapp.wa001.database.AbstractDAO;
import it.unipd.dei.webapp.wa001.resource.dbentities.User;

import java.sql.*;

/**
 * Login a user to the platform.
 */
public class LoginDAO extends AbstractDAO<User> {

    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "SELECT * FROM Festival.select_user_by_email(?)";

    /**
     * The user to be logged in
     */
    private final User user;

    /**
     * Creates a new object User that has been inserted in the database.
     *
     * @param con the connection to the database.
     * @param user user to be logged in.
     */
    public LoginDAO(final Connection con, final User user) {
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
            pstmt.setString(1, user.getEmail());

            rs = pstmt.executeQuery();

            rs.next();

            try{

                if (!user.getPassword().equals(rs.getString("password")))
                    throw new Exception("Wrong password.");

                u_ret = new User(rs.getInt("user_id"), rs.getString("email"), rs.getString("password"), rs.getString("name"),
                        rs.getString("surname"), rs.getString("stripe_id"), rs.getString("role"));

            } catch (SQLException e){
                throw new SQLException("User not found.", e);
            } catch (Exception e){
                throw new SQLException(e);
            }

            LOGGER.info("User (%d) successfully logged in.", u_ret.getUser_id());
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
