package it.unipd.dei.webapp.wa001.database.user;

import it.unipd.dei.webapp.wa001.database.AbstractDAO;
import it.unipd.dei.webapp.wa001.resource.dbentities.User;

import java.sql.*;

/**
 * Retrieve dish manager information.
 */
public class GetDishManagerDAO extends AbstractDAO<User> {

    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "SELECT * FROM Festival.select_manager_by_dish(?)";

    /**
     * The id of the dish to be retrieved the manager from
     */
    private final int dish;

    /**
     * Creates a new object User that is the manager of the restaurant that serves the dish
     *
     * @param con the connection to the database.
     * @param dish the id of the dish from which to get the manager of the restaurant that serves this dish
     */
    public GetDishManagerDAO(final Connection con, final int dish) {
        super(con);

        if (dish <= 0) {
            LOGGER.error("The dish id cannot be less or equal to 0.");
            throw new NullPointerException("The dish id cannot be less or equal to 0.");
        }

        this.dish = dish;
    }

    @Override
    protected final void doAccess() throws SQLException {

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User manager = null;

        try {
            pstmt = con.prepareStatement(STATEMENT);
            pstmt.setInt(1, dish);

            rs = pstmt.executeQuery();

            rs.next();

            try{

                manager = new User(rs.getInt("user_id"), rs.getString("email"), rs.getString("password"), rs.getString("name"),
                        rs.getString("surname"), rs.getString("stripe_id"), rs.getString("role"));

            } catch (SQLException e){
                throw new SQLException("Restaurant not found.");
            }

            LOGGER.info("Manager of restaurant that serves dish (%d) successfully retrieved.", dish);
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
