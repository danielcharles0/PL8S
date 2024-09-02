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

import it.unipd.dei.webapp.wa001.database.AbstractDAO;
import it.unipd.dei.webapp.wa001.resource.dbentities.Restaurant;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Lists all the {@link Restaurant}s in the database
 */
public final class SearchRestaurantByNameByCuisineDAO extends AbstractDAO<List<Restaurant>> {

	/**
	 * The SQL statement to be executed
	 */
	private static final String STATEMENT = "SELECT * FROM Festival.select_restaurants_by_name_by_cuisinetype(?,?,?)";
	
	/**
	 * The restaurant name to look for
	 */
	private final String restName;

	/**
	 * The cuisine type of the restaurants to look for
	 */
	private final String cuisType;

	/**
	 * The restaurant_id of the restaurant to search
	 */
	private final Integer restaurant_id;


	/**
	 * Creates a new object for listing all the restaurants.
	 *
	 * @param con the connection to the database.
	 * @param restName the restaurant name to look for.
	 * @param cuisType the cuisine type of the restaurants to look for.
	 * @param restaurant_id the restaurant_id to search for
	 */
	public SearchRestaurantByNameByCuisineDAO(final Connection con, final Integer restaurant_id,  final String restName,  final String cuisType ) {
		super(con);
		this.cuisType = cuisType;
		this.restName = restName;
		this.restaurant_id = restaurant_id;
	}

	@Override
	protected final void doAccess() throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		// the results of the search
		final List<Restaurant> restaurants = new ArrayList<Restaurant>();


		try {
			String maskedRestName;
//			it will be searched for any string containing restName
			if (restName != null) {
				maskedRestName = "%" + restName + "%";
			}
			else {
				maskedRestName = null;
			}
			pstmt = con.prepareStatement(STATEMENT);
			pstmt.setObject(1, restaurant_id, Types.INTEGER);
			pstmt.setString(2, maskedRestName);
			pstmt.setString(3, cuisType);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				restaurants.add(new Restaurant(rs.getInt("restaurant_id"), rs.getString("name"), rs.getString("description")
				, rs.getInt("manager"), rs.getTime("opening_at")
				, rs.getTime("closing_at"), (String[]) rs.getArray("countries").getArray(),
				(String[]) rs.getArray("cuisine_types").getArray()));
			}

			LOGGER.info("Restaurant(s) successfully listed from the database.");
		} finally {
			if (rs != null) {
				rs.close();
			}

			if (pstmt != null) {
				pstmt.close();
			}

		}

		outputParam = restaurants;
	}
}
