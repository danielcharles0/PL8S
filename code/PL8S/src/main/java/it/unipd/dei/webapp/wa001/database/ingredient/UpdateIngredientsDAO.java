package it.unipd.dei.webapp.wa001.database.ingredient;

import it.unipd.dei.webapp.wa001.database.AbstractDAO;
import it.unipd.dei.webapp.wa001.resource.dbentities.Ingredient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Update ingredients in the platform
 */
public class UpdateIngredientsDAO extends AbstractDAO<List<Ingredient>> {

    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "SELECT * FROM Festival.update_ingredients(?,?)";

    /**
     * The dish to update ingredients of.
     */
    private final int dish_id;

    /**
     * The ingredients the dish is made of.
     */
    private final List<String[]> ingredients;

    /**
     * Creates a new object Ingredient that has been updated in the database.
     *
     * @param con the connection to the database.
     * @param dish_id id of the dish made of the ingredients passed in input.
     * @param ingredients the ingredients to update.
     */
    public UpdateIngredientsDAO(final Connection con, final int dish_id, final List<String[]> ingredients) {
        super(con);

        if (dish_id <= 0) {
            LOGGER.error("The dish_id cannot be less or equal to 0.");
            throw new NullPointerException("The ingredients cannot be less or equal to 0.");
        }
        if (ingredients == null) {
            LOGGER.error("The ingredients cannot be null.");
            throw new NullPointerException("The ingredients cannot be null.");
        }

        this.dish_id = dish_id;
        this.ingredients = ingredients;

    }

    @Override
    protected final void doAccess() throws SQLException {

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Ingredient> il_ret = new ArrayList<>();

        try {
            // convert list of cuisine types to array
            Array is = con.createArrayOf("TEXT", ingredients.toArray());

            pstmt = con.prepareStatement(STATEMENT);
            pstmt.setInt(1, dish_id);
            pstmt.setArray(2, is);

            rs = pstmt.executeQuery();

            while(rs.next())
                il_ret.add(new Ingredient(rs.getInt("ingredient_id"), rs.getString("name"), rs.getString("diet")));

            LOGGER.info("%d Ingredient(s) successfully updated.", il_ret.size());
        } catch(SQLException e){
            throw new SQLException("Cannot update ingredients.", e);
        } finally{

            if (pstmt != null) {
                pstmt.close();
            }

        }

        this.outputParam = il_ret;
    }
}
