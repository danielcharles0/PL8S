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
import it.unipd.dei.webapp.wa001.resource.dbentities.DishIngredient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Lists all the {@link DishIngredient}s in the database by Reataurant,
 * If or many parameters are null they will be interpreted as any.
 */
public final class SearchDishesByRestaurantByNameByDietDAO extends AbstractDAO<List<DishIngredient>> {

	/**
	 * The SQL statement to be executed
	 */
	private static final String STATEMENT = "SELECT * FROM Festival.select_dishes_by_restaurant_by_name_by_diet(?,?,?)";

	/**
	 * The restaurant id of the restaurant we want to list dishes.
	 */
	private final Integer restaurant_id;
	private final String diet;
	private final String dish_name;

	/**
	 * Creates a new object for searching dishes according to the provided parameters.
	 * @param con the connection to the database..
	 * @param restaurant_id the Restaurant to search dishes from. If {@code null} it searches in all restaurants
	 * @param diet the diet the dish belongs to.
	 *             If {@code carnivorous},it returns dishes with any kind of ingredients.
	 *             If {@code vegetarian}, it returns dishes containing only vegan and vegetarian ingredients
	 *             If {@code vegan}, it returns dishes containing only vegan ingredients
	 * @param dish_name the name of the dish to be searched. It will return any dish containing @param in the name.
	 *                  If {@code null} it will return any name
	 */
	public SearchDishesByRestaurantByNameByDietDAO(final Connection con, final Integer restaurant_id, final String diet, final String dish_name) {
		super(con);
		this.restaurant_id = restaurant_id;
		this.diet = diet;
		this.dish_name = dish_name;
	}

	@Override
	protected final void doAccess() throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		// the results of the search
		final List<DishIngredient> dishes = new ArrayList<DishIngredient>();

		try {
			String maskedDishName;
			// This will be needed in order to search any string that contains dish_name
			if (dish_name != null){
				maskedDishName = "%" + dish_name + "%";
			}
			else {
				maskedDishName = null;
			}
			pstmt = con.prepareStatement(STATEMENT);

			pstmt.setObject(1, restaurant_id, Types.INTEGER);
			pstmt.setString(2, diet);
			pstmt.setString(3, maskedDishName);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				dishes.add(new DishIngredient(rs.getInt("dish_id"), rs.getFloat("price"), rs.getString("name")
				, false, rs.getInt("restaurant"), (Integer[])rs.getArray("ingredient_ids").getArray()
				, (String[]) rs.getArray("ingredient_names").getArray(), (String[]) rs.getArray("ingredient_diets").getArray()));
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
