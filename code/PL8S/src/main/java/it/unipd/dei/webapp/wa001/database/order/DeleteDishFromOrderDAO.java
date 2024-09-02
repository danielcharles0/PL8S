package it.unipd.dei.webapp.wa001.database.order;

import it.unipd.dei.webapp.wa001.database.AbstractDAO;
import it.unipd.dei.webapp.wa001.resource.dbentities.OrderDish;

import java.sql.*;

/**
 * Delete the dish from the pending order in the database.
 */
public final class DeleteDishFromOrderDAO extends AbstractDAO<OrderDish> {

    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "SELECT * FROM Festival.delete_order_dish(?,?)";

    /**
     * The user_id of the user's OrderDish to be deleted from the database
     */
    private final int user;

    /**
     * The dish_id of the dish to be deleted from the order
     */
    private final int dish;

    /**
     * Creates a new object OrderDish that has been deleted from the database.
     *
     * @param con the connection to the database.
     * @param user the user_id of the user's OrderDish to be deleted
     * @param dish the dish_id of the dish to be deleted from the order
     */
    public DeleteDishFromOrderDAO(final Connection con, final int user, final int dish) {
        super(con);
        this.user = user;
        this.dish = dish;
    }

    @Override
    protected final void doAccess() throws SQLException {

        PreparedStatement pstmt = null;
        OrderDish o_ret = null;
        ResultSet rs = null;

        try {
            pstmt = con.prepareCall(STATEMENT);
            pstmt.setInt(1, user);
            pstmt.setInt(2, dish);

            rs = pstmt.executeQuery();

            if( rs.next() ) {
                o_ret = new OrderDish(rs.getInt("order"), rs.getInt("dish"),
                        rs.getInt("quantity"));

                LOGGER.info("OrderDish successfully deleted. Order: %d, Dish: %d",
                        o_ret.getOrder(), o_ret.getDish());
            }
        } catch(SQLException e){
            throw new SQLException("OrderDish not found.", e);
        } finally{

            if (pstmt != null) {
                pstmt.close();
            }

        }

        this.outputParam = o_ret;
    }
}

