/*
 * Copyright 2020-2023 University of Padua, Italy
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

package it.unipd.dei.webapp.wa001.filter;


import io.jsonwebtoken.JwtException;
import it.unipd.dei.webapp.wa001.resource.Message;
import it.unipd.dei.webapp.wa001.resource.ErrorCodes;
import it.unipd.dei.webapp.wa001.resource.dbentities.User;
import it.unipd.dei.webapp.wa001.resource.logging.Actions;
import it.unipd.dei.webapp.wa001.resource.logging.LogContext;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;

/**
 * Checks for successful authentication to allow for accessing manager resources.
 */
public class ManagerResourceFilter implements Filter {

    /**
     * A LOGGER available for all the subclasses.
     */
    protected static final Logger LOGGER = LogManager.getLogger(ManagerResourceFilter.class, StringFormatterMessageFactory.INSTANCE);

    /**
     * The JSON UTF-8 MIME media type
     */
    private static final String JSON_UTF_8_MEDIA_TYPE = "application/json; charset=utf-8";

    /**
     * The configuration for the filter
     */
    private FilterConfig config = null;

    /**
     * The connection pool to the database.
     */
    private DataSource ds;

    @Override
    public void init(final FilterConfig config) throws ServletException {

        if (config == null) {
            LOGGER.error("Filter configuration cannot be null.");
            throw new ServletException("Filter configuration cannot be null.");
        }
        this.config = config;

        /*
        Here we could pass configuration parameters to the filter, if needed.
         */

        // the JNDI lookup context
        InitialContext cxt;

        try {
            cxt = new InitialContext();
            ds = (DataSource) cxt.lookup("java:/comp/env/jdbc/pl8s");
        } catch (NamingException e) {
            ds = null;

            LOGGER.error("Unable to acquire the connection pool to the database.", e);

            throw new ServletException("Unable to acquire the connection pool to the database", e);
        }
    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain chain) throws
            IOException, ServletException {

        LogContext.setIPAddress(servletRequest.getRemoteAddr());

        try {
            if (!(servletRequest instanceof HttpServletRequest) || !(servletResponse instanceof HttpServletResponse)) {
                LOGGER.error("Only HTTP requests/responses are allowed.");
                throw new ServletException("Only HTTP requests/responses are allowed.");
            }

            // Safe to downcast at this point.
            final HttpServletRequest req = (HttpServletRequest) servletRequest;
            final HttpServletResponse res = (HttpServletResponse) servletResponse;

            LOGGER.info("request URL =  %s", req.getRequestURL());

            // authenticated and authorized user
            if(!authenticateUser(req, res))
                return;

            // the user is properly authenticated and in session, continue the processing
            chain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            LOGGER.error("Unable to perform the manager resource filtering.", e);
            throw e;
        } finally {
            LogContext.removeUser();
            LogContext.removeIPAddress();
            LogContext.removeAction();
        }
    }

    /**
     * Authenticates and authorizes the user.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     *
     * @return {@code true} if the user has been successfully authenticated; {@code false otherwise}.
     */
    private boolean authenticateUser(HttpServletRequest req, HttpServletResponse res) {

        LogContext.setAction(Actions.AUTHENTICATE_USER);
        LOGGER.info("Trying to authenticate the user");

        try {
            // get the authorization information
            final String auth = req.getHeader("Authorization");

            // if there is no authorization information, send the authentication challenge again
            if (auth == null || auth.isBlank()) {

                LOGGER.info("No authorization header sent by the client.");

                sendAuthenticationChallenge(res, "No authorization header sent.");

                return false;
            }

            // if it is not HTTP Basic authentication, send the authentication challenge again
            if (!auth.toUpperCase().startsWith("BEARER ")) {

                LOGGER.warn("Bearer authentication is expected. Clients sent instead: %s", auth);

                sendAuthenticationChallenge(res, "Bearer authentication is expected.");

                return false;
            }

            // get jwt
            final String token = auth.substring(7);

            User auth_user = null;

            try {
                auth_user = JWT.decode(token);
            } catch (JwtException e){
                sendAuthenticationChallenge(res, "Invalid JWT.");
                return false;
            }

            // if the user is successfully authenticated then check if it can access the resource
            if(auth_user != null){

                if(auth_user.getRole().equals("manager")) {

                    req.setAttribute("auth_user", auth_user);

                    LOGGER.info("User (%d) succesfully authenticated and authorized.", auth_user.getUser_id());

                    return true;

                } else {
                    Message m = new Message("Unauthorized user for the requested resource.", ErrorCodes.UNAUTHORIZED_USER, null);
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.setContentType(JSON_UTF_8_MEDIA_TYPE);
                    m.toJSON(res.getOutputStream());
                    return false;
                }

            }

            // as a fallback, always send the authentication challenge again
            sendAuthenticationChallenge(res, "JWT expired.");
        } catch (Exception e) {
            LOGGER.error("Unable to authenticate the user.", e);
        } finally {
            LogContext.removeAction();
        }

        return false;
    }

    /**
     * Sends the authentication challenge.
     *
     * @param res the HTTP servlet response.
     * @param details details of the error occurred.
     *
     * @throws IOException if anything goes wrong while sending the authentication challenge.
     */
    private void sendAuthenticationChallenge(HttpServletResponse res, String details) throws IOException {

        try {
            res.setHeader("WWW-Authenticate", "Bearer realm=User");

            Message m = new Message("Cannot authenticate the user.", ErrorCodes.UNAUTHENTICATED_USER, details);
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            m.toJSON(res.getOutputStream());

            LOGGER.info("Bearer Authentication Challenge sent.");
        } catch (Exception e) {
            LOGGER.error("Unable to send authentication challenge.", e);
            throw e;
        }
    }

    @Override
    public void destroy() {
        config = null;
        ds = null;
    }

}
