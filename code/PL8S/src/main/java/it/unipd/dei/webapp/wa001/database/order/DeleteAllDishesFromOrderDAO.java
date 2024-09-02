package it.unipd.dei.webapp.wa001.database.order;

import it.unipd.dei.webapp.wa001.database.AbstractDAO;
import it.unipd.dei.webapp.wa001.resource.dbentities.Order;
import it.unipd.dei.webapp.wa001.resource.dbentities.OrderDish;

import java.sql.*;

/**
 * Deletes all the dishes from the pending order in the database.
 */
public final class DeleteAllDishesFromOrderDAO extends AbstractDAO<Order> {

    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "SELECT * FROM Festival.delete_order_dishes(?)";

    /**
     * The user_id of the user's pending order to be deleted from the database
     */
    private final int user;

    /**
     * Creates a new object Order that has been emptied from the database.
     *
     * @param con the connection to the database.
     * @param user the user_id of the user's OrderDish to be deleted
     */
    public DeleteAllDishesFromOrderDAO(final Connection con, final int user) {
        super(con);
        this.user = user;
    }

    @Override
    protected final void doAccess() throws SQLException {

        PreparedStatement pstmt = null;
        Order order = null;
        ResultSet rs = null;

        try {
            pstmt = con.prepareCall(STATEMENT);
            pstmt.setInt(1, user);

            rs = pstmt.executeQuery();

            if( rs.next() ) {
                order = new Order(rs.getInt("order_id"), rs.getFloat("price"),
                        rs.getTimestamp("placedOn"), rs.getString("status"), user);

                LOGGER.info("List of dishes from Order successfully deleted. Order: %d",
                        order.getOrder_id());
            }
        } catch(SQLException e){
            throw new SQLException("Order dishes to delete not found.", e);
        } finally{

            if (pstmt != null) {
                pstmt.close();
            }

        }

        this.outputParam = order;
    }
}

