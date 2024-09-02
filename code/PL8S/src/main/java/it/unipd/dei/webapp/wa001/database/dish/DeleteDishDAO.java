package it.unipd.dei.webapp.wa001.database.dish;

import it.unipd.dei.webapp.wa001.database.AbstractDAO;
import it.unipd.dei.webapp.wa001.resource.dbentities.Dish;

import java.security.spec.ECParameterSpec;
import java.sql.*;

/**
 * Delete a dish from the database.
 */
public class DeleteDishDAO extends AbstractDAO<Dish> {

    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "SELECT * FROM Festival.delete_dish(?)";

    /**
     * The id of the dish to be deleted from the database
     */
    private final int dish_id;

    /**
     * Creates a new object Dish that has been deleted from the database.
     *
     * @param con the connection to the database.
	 * @param dish_id the id of the dish to be deleted.
     */
    public DeleteDishDAO(final Connection con, final int dish_id) {
        super(con);

        if (dish_id <= 0) {
            LOGGER.error("The id of the dish cannot be less or equal to 0.");
            throw new NullPointerException("The id of the  dish cannot be less or equal to 0.");
        }

        this.dish_id = dish_id;
    }

    @Override
    protected final void doAccess() throws SQLException {

        PreparedStatement pstmt = null;
        Dish d_ret = null;
        ResultSet rs = null;

        try {
            pstmt = con.prepareCall(STATEMENT);
            pstmt.setInt(1, dish_id);

            rs = pstmt.executeQuery();

            rs.next();

            d_ret = new Dish(rs.getInt("dish_id"), rs.getFloat("price"), rs.getString("name"), rs.getBoolean("isDeleted"),
                    rs.getInt("restaurant"));

            LOGGER.info("Dish (%d) successfully deleted.", d_ret.getDish_id());

        } catch(SQLException e) {
            throw new SQLException("Dish not found.");
        }    finally{

            if (pstmt != null) {
                pstmt.close();
            }

        }

        this.outputParam = d_ret;
    }
}
