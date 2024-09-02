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

import it.unipd.dei.webapp.wa001.resource.dbentities.Restaurant;
import it.unipd.dei.webapp.wa001.database.AbstractDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lists all the {@link Restaurant}s in the database
 */
@Deprecated
public final class ListRestaurantDAO extends AbstractDAO<List<Restaurant>> {

	/**
	 * The SQL statement to be executed
	 */
	private static final String STATEMENT = "SELECT * FROM Festival.select_restaurants()";
	/**
	 * Creates a new object for listing all the restaurants.
	 *
	 * @param con the connection to the database.
	 */
	public ListRestaurantDAO(final Connection con) {
		super(con);
	}

	@Override
	protected final void doAccess() throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		// the results of the search
		final List<Restaurant> restaurants = new ArrayList<Restaurant>();

		try {
			pstmt = con.prepareStatement(STATEMENT);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				restaurants.add(new Restaurant(rs.getInt("restaurant_id"), rs.getString("name"), rs.getString("description")
				, rs.getInt("manager"), rs.getTime("opening_at"), rs.getTime("closing_at"),
				(String[]) rs.getArray("countries").getArray(), (String[]) rs.getArray("cuisine_types").getArray()));
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
