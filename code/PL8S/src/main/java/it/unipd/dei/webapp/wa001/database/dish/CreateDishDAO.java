package it.unipd.dei.webapp.wa001.database.dish;

import it.unipd.dei.webapp.wa001.database.AbstractDAO;
import it.unipd.dei.webapp.wa001.resource.dbentities.Dish;

import java.sql.*;

/**
 * Create dish in the platform
 */
public class CreateDishDAO extends AbstractDAO<Dish> {

    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "CALL Festival.insert_dish(?,?,?,?)";

    /**
     * The dish to be created in the database
     */
    private final Dish dish;

    /**
     * Creates a new object DishIngredient that has been inserted in the database.
     *
     * @param con the connection to the database.
     * @param dish the cuisines to create.
     */
    public CreateDishDAO(final Connection con, final Dish dish) {
        super(con);

        if (dish == null) {
            LOGGER.error("The dish cannot be null.");
            throw new NullPointerException("The dish cannot be null.");
        }

        this.dish = dish;
    }

    @Override
    protected final void doAccess() throws SQLException {

        CallableStatement cstmt = null;
        Dish d_ret = null;

        try {
            cstmt = con.prepareCall(STATEMENT);
            cstmt.setString(1, dish.getName());
            cstmt.setFloat(2, dish.getPrice());
            cstmt.setInt(3, dish.getRestaurant());

            cstmt.registerOutParameter(4, Types.INTEGER);

            cstmt.execute();

            d_ret = new Dish(cstmt.getInt(4), dish.getPrice(), dish.getName(), dish.isDeleted(), dish.getRestaurant());

            LOGGER.info("Dish (%d) successfully created.", d_ret.getDish_id());
        } catch(SQLException e){
            throw new SQLException("Cannot create dish.", e);
        } finally{

            if (cstmt != null) {
                cstmt.close();
            }

        }

        this.outputParam = d_ret;
    }
}
