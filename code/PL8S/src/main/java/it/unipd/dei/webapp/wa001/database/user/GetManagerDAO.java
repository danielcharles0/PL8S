package it.unipd.dei.webapp.wa001.database.user;

import it.unipd.dei.webapp.wa001.database.AbstractDAO;
import it.unipd.dei.webapp.wa001.resource.dbentities.User;

import java.sql.*;

/**
 * Retrieve restaurant manager information.
 */
public class GetManagerDAO extends AbstractDAO<User> {

    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "SELECT * FROM Festival.select_manager_by_restaurant(?)";

    /**
     * The id of the restaurant to be retrieved the manager from
     */
    private final int restaurant;

    /**
     * Creates a new object User that is the manager of the restaurant
     *
     * @param con the connection to the database.
     * @param restaurant the id of the restaurant from which to get the manager
     */
    public GetManagerDAO(final Connection con, final int restaurant) {
        super(con);

        if (restaurant <= 0) {
            LOGGER.error("The restaurant id cannot be less or equal to 0.");
            throw new NullPointerException("The restaurant id cannot be less or equal to 0.");
        }

        this.restaurant = restaurant;
    }

    @Override
    protected final void doAccess() throws SQLException {

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User manager = null;

        try {
            pstmt = con.prepareStatement(STATEMENT);
            pstmt.setInt(1, restaurant);

            rs = pstmt.executeQuery();

            rs.next();

            try{

                manager = new User(rs.getInt("user_id"), rs.getString("email"), rs.getString("password"), rs.getString("name"),
                        rs.getString("surname"), rs.getString("stripe_id"), rs.getString("role"));

            } catch (SQLException e){
                throw new SQLException("Restaurant not found.");
            }

            LOGGER.info("Manager of restaurant (%d) successfully retrieved.", restaurant);
        } finally {
            if (rs != null) {
                rs.close();
            }

            if (pstmt != null) {
                pstmt.close();
            }

        }

        this.outputParam = manager;
    }
}
