package it.unipd.dei.webapp.wa001.database.restaurant;

import it.unipd.dei.webapp.wa001.database.AbstractDAO;
import it.unipd.dei.webapp.wa001.resource.dbentities.Restaurant;
import it.unipd.dei.webapp.wa001.resource.dbentities.User;

import java.sql.*;

/**
 * Delete restaurant from the platform
 */
public class DeleteRestaurantDAO extends AbstractDAO<Restaurant> {

    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "SELECT * FROM Festival.delete_restaurant(?)";

    /**
     * The id of the restaurant to be deleted from the database
     */
    private final int restaurant_id;

    /**
     * Creates a new object Restaurant that has been deleted from the database.
     *
     * @param con the connection to the database.
	 * @param restaurant_id the id of the restaurant to delete.
     */
    public DeleteRestaurantDAO(final Connection con, final int restaurant_id) {
        super(con);

        if (restaurant_id <= 0) {
            LOGGER.error("The restaurant_id cannot be less or equal 0.");
            throw new NullPointerException("The restaurant_id cannot be less or equal 0.");
        }

        this.restaurant_id = restaurant_id;
    }

    @Override
    protected final void doAccess() throws SQLException {

        PreparedStatement pstmt = null;
        Restaurant r_ret = null;
        ResultSet rs = null;

        try {
            pstmt = con.prepareStatement(STATEMENT);
            pstmt.setInt(1, restaurant_id);

            rs = pstmt.executeQuery();

            rs.next();

            r_ret = new Restaurant(rs.getInt("restaurant_id"), rs.getString("name"), rs.getString("description"),
                    rs.getInt("manager"), rs.getTime("opening_at"), rs.getTime("closing_at"));

            LOGGER.info("Restaurant (%d) successfully deleted.", r_ret.getRestaurant_id());

        } catch(SQLException e){
            throw new SQLException("Restaurant not found.");
        } finally{

            if (pstmt != null) {
                pstmt.close();
            }

        }

        this.outputParam = r_ret;
    }
}
