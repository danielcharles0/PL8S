package it.unipd.dei.webapp.wa001.database.order;

import it.unipd.dei.webapp.wa001.database.AbstractDAO;
import it.unipd.dei.webapp.wa001.resource.dbentities.OrderDish;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Updates the cart(order) dish quantity in the database.
 */
public final class UpdateDishQuantityDAO extends AbstractDAO<OrderDish> {

    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "call Festival.update_order_dish_quantity(?,?,?,?)";

    /**
     * The user_id of the user's order
     */
    private final int user;

    /**
     * The dish_id of the dish
     */
    private final int dish;

    /**
     * The quantity of the dish to be updated into the database
     */
    private final int quantity;

    /**
     * Updates dish quantity provided by the {@link OrderDish}s object.
     *
     * @param con the connection to the database.
     * @param user the user_id
     * @param dish the dish_id
     * @param quantity the quantity to be updated
     */
    public UpdateDishQuantityDAO(final Connection con, final int user, final int dish, final int quantity) {
        super(con);

        this.user = user;
        this.dish = dish;
        this.quantity = quantity;
    }

    @Override
    protected final void doAccess() throws SQLException {

        CallableStatement cstmt = null;
        OrderDish order_dish = null;

        try {
            cstmt = con.prepareCall(STATEMENT);
            cstmt.setInt(1, user);
            cstmt.setInt(2, dish);
            cstmt.setInt(3, quantity);

            cstmt.registerOutParameter(4, Types.INTEGER);

            cstmt.execute();

            order_dish = new OrderDish(cstmt.getInt(4), dish, quantity);

            LOGGER.info("OrderDish quantity successfully updated: order_id: %d, dish_id: %d, quantity: %d",
                    order_dish.getOrder(), dish, quantity);
        } finally {

            if (cstmt != null) {
                cstmt.close();
            }

        }
        this.outputParam = order_dish;
    }
}
