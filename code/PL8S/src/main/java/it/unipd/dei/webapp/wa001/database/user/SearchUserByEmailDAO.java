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

package it.unipd.dei.webapp.wa001.database.user;

import it.unipd.dei.webapp.wa001.database.AbstractDAO;
import it.unipd.dei.webapp.wa001.resource.dbentities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Search for all the {@code User}s in the database having a specific email
 */
public final class SearchUserByEmailDAO extends AbstractDAO<List<User>> {

	/**
	 * The SQL statement to be executed
	 */
	private static final String STATEMENT = "SELECT * FROM Festival.select_user_by_email_masked(?)";
	private final String email;

	/**
	 * Creates a new object for searching all users by the provided email address.
	 * @param con the connection to the database.
	 * @param email address to be searched
	 */
	public SearchUserByEmailDAO(Connection con, final String email) {
		super(con);
		this.email = email;
	}

	@Override
	protected final void doAccess() throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		// the results of the search
		final List<User> users = new ArrayList<User>();

		try {
			String maskedEmail;
			if (email != null){
				maskedEmail = "%" + email + "%";
			}
			else {
				maskedEmail = null;
			}
			pstmt = con.prepareStatement(STATEMENT);
			pstmt.setString(1, maskedEmail);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				users.add(new User(rs.getInt("user_id"), rs.getString("email"), rs.getString("password")
				, rs.getString("name"), rs.getString("surname"), rs.getString("stripe_id"), rs.getString("role")));
			}

			LOGGER.info("Users by email, if any, successfully listed from the database.");
		} finally {
			if (rs != null) {
				rs.close();
			}

			if (pstmt != null) {
				pstmt.close();
			}

		}
		outputParam = users;
	}
}
