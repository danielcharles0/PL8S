package it.unipd.dei.webapp.wa001.database.order;

import it.unipd.dei.webapp.wa001.database.AbstractDAO;
import it.unipd.dei.webapp.wa001.resource.dbentities.Order;
import it.unipd.dei.webapp.wa001.resource.dbentities.OrderDish;

import java.sql.*;

/**
 * Complete the pending order in the database.by turning status into completed
 */
public final class CompleteOrderDAO extends AbstractDAO<Order> {

    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "SELECT * FROM Festival.update_order_status(?)";

    /**
     * The user_id of the user's Order to be completed from the database
     */
    private final int user;

    /**
     * Creates a new object Order that has been completed from the database.
     *
     * @param con the connection to the database.
     * @param user the user_id of the user's order to be completed
     */
    public CompleteOrderDAO(final Connection con, final int user) {
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

                LOGGER.info("Order successfully completed. Order: %d",
                        order.getOrder_id());
            }
        } catch(SQLException e){
            throw new SQLException("Order not found.", e);
        } finally{

            if (pstmt != null) {
                pstmt.close();
            }

        }

        this.outputParam = order;
    }
}

