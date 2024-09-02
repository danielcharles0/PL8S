package it.unipd.dei.webapp.wa001.database.dish;

import it.unipd.dei.webapp.wa001.database.AbstractDAO;
import it.unipd.dei.webapp.wa001.resource.dbentities.DishIngredient;
import it.unipd.dei.webapp.wa001.resource.dbentities.Ingredient;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Array;

/**
 * Update a dish in the platform
 */
public class UpdateDishDAO extends AbstractDAO<DishIngredient> {

    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "CALL Festival.update_dish(?,?,?,?,?)";

    /**
     * The dish to be updated in the database
     */
    private final DishIngredient dish_ing;

    /**
     * Updates a Dish on the database.
     *
     * @param con the connection to the database.
     * @param dish_ing the dish to update.
     */
    public UpdateDishDAO(final Connection con, final DishIngredient dish_ing) {
        super(con);

        if (dish_ing == null) {
            LOGGER.error("The dish cannot be null.");
            throw new NullPointerException("The dish cannot be null.");
        }

        this.dish_ing = dish_ing;
    }

    @Override
    protected final void doAccess() throws SQLException {

        CallableStatement cstmt = null;
        final Ingredient[] ingredients = dish_ing.getIngredients();
        String[][] row_ings = new String[ingredients.length][2];

        int i = 0;
        for(Ingredient ing : dish_ing.getIngredients()){
            row_ings[i][0] = ing.getName();
            row_ings[i][1] = ing.getDiet();
            i++;
        }

        try {
            cstmt = con.prepareCall(STATEMENT);

            Array ings = con.createArrayOf("text", row_ings);
            cstmt.setInt(1, dish_ing.getDish_id());
            cstmt.setString(2, dish_ing.getName());
            cstmt.setFloat(3, dish_ing.getPrice());
            cstmt.setInt(4, dish_ing.getRestaurant());
            cstmt.setArray(5, ings);

            cstmt.execute();

            LOGGER.info("Dish (%d) successfully updated.", dish_ing.getDish_id());
        } catch(SQLException e){
            throw new SQLException("Cannot update dish.", e);
        } finally{

            if (cstmt != null) {
                cstmt.close();
            }

        }

        this.outputParam = dish_ing;
    }
}
