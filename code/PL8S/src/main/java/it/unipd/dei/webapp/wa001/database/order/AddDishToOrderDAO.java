/*
 * Copyright 2018-2023 University of Padua, Italy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.unipd.dei.webapp.wa001.database.order;

import it.unipd.dei.webapp.wa001.database.AbstractDAO;
import it.unipd.dei.webapp.wa001.resource.dbentities.OrderDish;

import java.sql.*;

/**
 * Adds the dish to the pending order in the database.
 */
public final class AddDishToOrderDAO extends AbstractDAO<OrderDish> {

    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "call Festival.insert_order_dish(?,?,?)";

    /**
     * The user_id of the user that added a dish to his order
     */
    private final int user;

    /**
     * The dish_id of the dish to be added
     */
    private final int dish;

    /**
     * Creates a new object for adding a dish to an order.
     *
     * @param con   the connection to the database.
     * @param user the user_id of the user that added a dish to his order.
     * @param dish the dish_id of the dish to be added.
     */
    public AddDishToOrderDAO(final Connection con, final int user, final int dish) {
        super(con);
        this.user = user;
        this.dish = dish;
    }

    @Override
    protected final void doAccess() throws SQLException {

        CallableStatement cstmt = null;
        OrderDish od = null;

        try {
            cstmt = con.prepareCall(STATEMENT);
            cstmt.setInt(1, user);
            cstmt.setInt(2, dish);

            cstmt.registerOutParameter(3, Types.INTEGER);

            cstmt.execute();

            od = new OrderDish(cstmt.getInt(3), dish, 1);

            LOGGER.info("OrderDish successfully inserted: Order: %s, Dish: %s, Quantity: %s", od.getOrder(), dish, 1);

        } catch(SQLException e){
            if( e.getSQLState().equals("23503") ){
                throw new SQLException("Invalid dish in OrderDish relation.", e);
            }
            else if( e.getSQLState().equals("23505") ){
                throw new SQLException("OrderDish relation already registered.", e);
            }
            else{
                throw new SQLException("Unexpected OrderDish error." + e.getSQLState(), e);
            }

        } finally {

            if (cstmt != null) {
                cstmt.close();
            }

        }

        outputParam = od;
    }
}
