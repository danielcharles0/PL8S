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

package it.unipd.dei.webapp.wa001.database.dish;

import it.unipd.dei.webapp.wa001.database.AbstractDAO;
import it.unipd.dei.webapp.wa001.resource.dbentities.Order;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Lists all the orders for a specific dish from the database.
 */
public final class ListOrdersDAO extends AbstractDAO<List<Order>> {

    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "SELECT * FROM Festival.select_dish_orders(?)";

    /**
     * The dish_id to be contained in the orders
     */
    private final int dish_id;

    /**
     * Creates a new object for listing the dish orders.
     *
     * @param con the connection to the database.
     * @param dish_id the dish_id to be contained in the orders.
     */
    public ListOrdersDAO(final Connection con, final int dish_id) {
        super(con);
        this.dish_id = dish_id;
    }

    @Override
    protected final void doAccess() throws SQLException {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        // the result of the search
        final List<Order> orders = new ArrayList<Order>();

        try {
            pstmt = con.prepareStatement(STATEMENT);
            pstmt.setInt(1, dish_id);

            rs = pstmt.executeQuery();

            while (rs.next())
                orders.add(
					new Order(
						rs.getInt("order_id")
						, rs.getFloat("o_price")
						, rs.getTimestamp("o_placedOn")
						, rs.getString("o_status")
						, rs.getInt("o_user")
					)
				);

            LOGGER.info("Previous orders successfully retrieved from the database.");
        } finally {
            if (rs != null) {
                rs.close();
            }

            if (pstmt != null) {
                pstmt.close();
            }
        }

        outputParam = orders;
    }
}
