package it.unipd.dei.webapp.wa001.database.restaurant;

import it.unipd.dei.webapp.wa001.database.AbstractDAO;
import it.unipd.dei.webapp.wa001.resource.dbentities.Restaurant;

import java.sql.*;

/**
 * Create restaurant in the platform
 */
public class CreateRestaurantDAO extends AbstractDAO<Restaurant> {

    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "CALL Festival.insert_restaurant(?,?,?,?,?,?)";

    /**
     * The restaurant to be created in the database
     */
    private final Restaurant restaurant;

    /**
     * Creates a new object Restaurant that has been inserted in the database.
     *
     * @param con the connection to the database.
     * @param restaurant the restaurant to create.
     */
    public CreateRestaurantDAO(final Connection con, final Restaurant restaurant) {
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
        Restaurant r_ret = null;

        try {
            cstmt = con.prepareCall(STATEMENT);
            cstmt.setString(1, restaurant.getName());
            cstmt.setString(2, restaurant.getDescription());
            cstmt.setInt(3, restaurant.getManager());
            cstmt.setTime(4, restaurant.getOpening_at());
            cstmt.setTime(5, restaurant.getClosing_at());

            cstmt.registerOutParameter(6, Types.INTEGER);

            cstmt.execute();

            r_ret = new Restaurant(cstmt.getInt(6), restaurant.getName(), restaurant.getDescription(),
                    restaurant.getManager(), restaurant.getOpening_at(), restaurant.getClosing_at(),
                    restaurant.getCountries(), restaurant.getCuisine_types());

            LOGGER.info("Restaurant (%d) successfully created.", r_ret.getRestaurant_id());

        } catch(SQLException e){
            throw new SQLException("Cannot create restaurant.");
        } finally{

            if (cstmt != null) {
                cstmt.close();
            }

        }

        this.outputParam = r_ret;
    }
}
