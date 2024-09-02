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
import it.unipd.dei.webapp.wa001.resource.dbentities.Dish;
import it.unipd.dei.webapp.wa001.resource.dbentities.DishIngredient;

import java.sql.*;

/**
 * Selects a {@link Dish} in the database.
 */
public final class SelectDishDAO extends AbstractDAO<DishIngredient> {

	/**
	 * The SQL statement to be executed
	 */
	private static final String STATEMENT = "SELECT * FROM Festival.select_dish(?)";

	/**
	 * The dish id of the dish to select.
	 */
	private final Integer dish_id;

	/**
	 * Creates a new object for searching dishes according to the provided parameters.
	 * @param con the connection to the database.
	 * @param dish_id the dish id to the dish to select.
	 */
	public SelectDishDAO(final Connection con, final int dish_id) {
		super(con);
		this.dish_id = dish_id;
	}

	@Override
	protected final void doAccess() throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		// the results of the search
		final DishIngredient dish;

		try {

			pstmt = con.prepareStatement(STATEMENT);

			pstmt.setInt(1, dish_id);

			rs = pstmt.executeQuery();

			rs.next();

			dish = new DishIngredient(rs.getInt("dish_id"), rs.getFloat("price"), rs.getString("name")
				, false, rs.getInt("restaurant"), rs.getString("diet"),
					(Integer[]) rs.getArray("ingredient_ids").getArray(), (String[])rs.getArray("ingredient_names").getArray(),
                    (String[])rs.getArray("ingredient_diets").getArray());

			LOGGER.info("Dish successfully selected from the database.");
		} finally {
			if (rs != null) {
				rs.close();
			}

			if (pstmt != null) {
				pstmt.close();
			}

		}

		outputParam = dish;
	}
}
