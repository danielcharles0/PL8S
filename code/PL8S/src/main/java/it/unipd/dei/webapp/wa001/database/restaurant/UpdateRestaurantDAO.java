package it.unipd.dei.webapp.wa001.database.restaurant;

import it.unipd.dei.webapp.wa001.database.AbstractDAO;
import it.unipd.dei.webapp.wa001.resource.dbentities.Restaurant;

import java.sql.*;

/**
 * Update restaurant in the platform
 */
public class UpdateRestaurantDAO extends AbstractDAO<Restaurant> {

    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "CALL Festival.update_restaurant(?,?,?,?,?,?)";

    /**
     * The restaurant to be updated in the database
     */
    private final Restaurant restaurant;

    /**
     * Creates a new object Restaurant that has been updated in the database.
     *
     * @param con the connection to the database.
     * @param restaurant the restaurant to update.
     */
    public UpdateRestaurantDAO(final Connection con, final Restaurant restaurant) {
        super(con);

        if (restaurant == null) {
            LOGGER.error("The restaurant cannot be null.");
            throw new NullPointerException("The restaurant cannot be null.");
        }

        this.restaurant = restaurant;
    }

    @Override
    protected final void doAccess() throws SQLException {

        CallableStatement cstmt = null;

        try {
            cstmt = con.prepareCall(STATEMENT);
            cstmt.setInt(1, restaurant.getRestaurant_id());
            cstmt.setString(2, restaurant.getName());
            cstmt.setString(3, restaurant.getDescription());
            cstmt.setInt(4, restaurant.getManager());
            cstmt.setTime(5, restaurant.getOpening_at());
            cstmt.setTime(6, restaurant.getClosing_at());

            cstmt.execute();

            LOGGER.info("Restaurant (%d) successfully updated.", restaurant.getRestaurant_id());

        } catch(SQLException e){
            throw new SQLException("Cannot update restaurant.", e);
        } finally{

            if (cstmt != null) {
                cstmt.close();
            }

        }

        this.outputParam = restaurant;
    }
}
