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

package it.unipd.dei.webapp.wa001.database.restaurant;

import it.unipd.dei.webapp.wa001.resource.dbentities.Dish;
import it.unipd.dei.webapp.wa001.database.AbstractDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lists all the {@link Dish}es for a specific restaurant.
 */
@Deprecated
public final class ListDishesDAO extends AbstractDAO<List<Dish>> {

	/**
	 * The SQL statement to be executed
	 */
	private static final String STATEMENT = "SELECT * FROM Festival.select_dishes_by_restaurant(?)";

	/**
	 * The restaurant id of the restaurant we want to list dishes.
	 */
	private final int restaurant_id;

	/**
	 * Creates a new object for listing all the dishes.
	 *
	 * @param con the connection to the database.
	 * @param restaurant_id the restaurant id of which to list dishes
	 */
	public ListDishesDAO(final Connection con, final int restaurant_id) {
		super(con);
		this.restaurant_id = restaurant_id;
	}

	@Override
	protected final void doAccess() throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		// the results of the search
		final List<Dish> dishes = new ArrayList<Dish>();

		try {
			pstmt = con.prepareStatement(STATEMENT);

			pstmt.setInt(1, restaurant_id);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				dishes.add(new Dish(rs.getInt("dish_id"), rs.getFloat("price"), rs.getString("name")
				, false, restaurant_id));
			}

			LOGGER.info("Dish(es) successfully listed from the database.");
		} finally {
			if (rs != null) {
				rs.close();
			}

			if (pstmt != null) {
				pstmt.close();
			}

		}

		outputParam = dishes;
	}
}
