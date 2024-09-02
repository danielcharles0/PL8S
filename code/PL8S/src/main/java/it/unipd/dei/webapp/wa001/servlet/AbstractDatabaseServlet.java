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

package it.unipd.dei.webapp.wa001.servlet;

import it.unipd.dei.webapp.wa001.resource.logging.LogContext;
import it.unipd.dei.webapp.wa001.resource.logging.Actions;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Gets the {@code DataSource} for managing the connection pool to the database.
 *
 */
public abstract class AbstractDatabaseServlet extends HttpServlet {

	/**
	 * A LOGGER available for all the subclasses.
	 */
	protected static final Logger LOGGER = LogManager.getLogger(AbstractDatabaseServlet.class,
			StringFormatterMessageFactory.INSTANCE);

	/**
	 * The connection pool to the database.
	 */
	private DataSource ds;

	/**
	 * The ip looking for the connection.
	 */
	private static final String IP_ADDRESS = "localhost";

	/**
	 * The connection pool resource.
	 */
	private static final String RESOURCE = "java:/comp/env/jdbc/pl8s";

	/**
	 * The connection user.
	 */
	private static final String USER = "webuser";

	/**
	 * Gets the {@code DataSource} for managing the connection pool to the database.
	 *
	 * @param config a {@code ServletConfig} object containing the servlet's configuration and initialization
	 *               parameters.
	 *
	 * @throws ServletException if an exception has occurred that interferes with the servlet's normal operation
	 */
	public void init(ServletConfig config) throws ServletException {

		// the JNDI lookup context
		InitialContext cxt;

		try {
			
			LogContext.setAll(
				AbstractDatabaseServlet.USER
				, AbstractDatabaseServlet.IP_ADDRESS
				, Actions.DATABASE_CONNECTION
				, AbstractDatabaseServlet.RESOURCE
			);

			cxt = new InitialContext();
			ds = (DataSource) cxt.lookup("java:/comp/env/jdbc/pl8s");

			LOGGER.info("Connection pool to the database successfully acquired.");
		} catch (NamingException e) {
			ds = null;

			LOGGER.error("Unable to acquire the connection pool to the database.", e);

			throw new ServletException("Unable to acquire the connection pool to the database", e);
		}finally{
			LogContext.removAll();
		}
	}

	/**
	 * Releases the {@code DataSource} for managing the connection pool to the database.
	 */
	public void destroy() {

		LogContext.setAll(
			AbstractDatabaseServlet.USER
			, AbstractDatabaseServlet.IP_ADDRESS
			, Actions.DATABASE_CONNECTION_RELEASE
			, AbstractDatabaseServlet.RESOURCE
		);

		ds = null;
		LOGGER.info("Connection pool to the database successfully released.");

		LogContext.removAll();
	}

	/**
	 * Returns a {@link  Connection} for accessing the database.
	 *
	 * @return a {@link Connection} for accessing the database
	 *
	 * @throws SQLException if anything goes wrong in obtaining the connection.
	 */
	protected final Connection getConnection() throws SQLException {
		try {
			return ds.getConnection();
		} catch (final SQLException e) {
			LOGGER.error("Unable to acquire the connection from the pool.", e);
			throw e;
		}
	}

}
