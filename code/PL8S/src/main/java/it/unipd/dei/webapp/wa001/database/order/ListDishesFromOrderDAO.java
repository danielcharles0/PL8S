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
import it.unipd.dei.webapp.wa001.resource.dbentities.Dish;
import it.unipd.dei.webapp.wa001.resource.dbentities.FullDish;
import it.unipd.dei.webapp.wa001.resource.dbentities.FullOrder;
import it.unipd.dei.webapp.wa001.resource.dbentities.Order;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Lists the dishes in an order of a user from the database.
 */
public final class ListDishesFromOrderDAO extends AbstractDAO<FullOrder> {

    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "SELECT * FROM Festival.select_orders_by_user_and_status(?,?::Festival.OrderStatus)";

    /**
     * The user_id associated with the pending order
     */
    private final int user;

    /**
     * Creates a new object for listing the dishes in an order.
     *
     * @param con   the connection to the database.
     * @param user the user_id associated with the pending order.
     */
    public ListDishesFromOrderDAO(final Connection con, final int user) {
        super(con);
        this.user = user;
    }

    @Override
    protected final void doAccess() throws SQLException {
		
		int dish_id = 0;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Order order = null;
        List<FullDish> dishes = new ArrayList<FullDish>();

        // the result of the search
        FullOrder fullOrder = null;

        try {
            pstmt = con.prepareStatement(STATEMENT);
            pstmt.setInt(1, user);
            pstmt.setString(2,"pending");

            rs = pstmt.executeQuery();

            while (rs.next()) {
                order = new Order(rs.getInt("order_id"), rs.getFloat("o_price"),
                        rs.getTimestamp("placedOn"), "pending", user);
				
				dish_id = rs.getInt("dish_id");
				
				if(dish_id != 0)
                	dishes.add(new FullDish(new Dish(dish_id, rs.getFloat("d_price"),
                        rs.getString("d_name"), false, rs.getInt("restaurant")),
                        rs.getBoolean("isDeleted"), rs.getInt("quantity"), rs.getString("r_name")));

            }
            if( order != null ) {
                fullOrder = new FullOrder(order, dishes);
            }

            LOGGER.info("Full order successfully retrieved from the database.");

        } finally {
            if (rs != null) {
                rs.close();
            }

            if (pstmt != null) {
                pstmt.close();
            }
        }

        outputParam = fullOrder;
    }
}
