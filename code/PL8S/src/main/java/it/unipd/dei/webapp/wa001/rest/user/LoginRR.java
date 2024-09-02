/*
 * Copyright 2023 University of Padua, Italy
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

package it.unipd.dei.webapp.wa001.rest.user;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import it.unipd.dei.webapp.wa001.database.user.LoginDAO;
import it.unipd.dei.webapp.wa001.database.user.PasswordGenerator;
import it.unipd.dei.webapp.wa001.filter.JWT;
import it.unipd.dei.webapp.wa001.resource.dbentities.User;
import it.unipd.dei.webapp.wa001.resource.logging.Actions;
import it.unipd.dei.webapp.wa001.resource.Message;
import it.unipd.dei.webapp.wa001.resource.ErrorCodes;
import it.unipd.dei.webapp.wa001.rest.AbstractRR;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.RegexValidator;

import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * A REST resource for logging in a user {@link LoginRR}s. *
 */
public final class LoginRR extends AbstractRR {

    /**
     * The JSON UTF-8 MIME media type
     */
    private static final String JSON_UTF_8_MEDIA_TYPE = "application/json; charset=utf-8";

    /**
     * Creates a new REST resource for logging in a {@code User}.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @param con the connection to the database.
     */
    public LoginRR(final HttpServletRequest req, final HttpServletResponse res, Connection con) {
        super(Actions.LOGIN_USER, req, res, con);
    }


    @Override
    protected void doServe() throws IOException {

        User user = null;
        Message m = null;

        try {
            // get the authorization information
            final String auth = req.getHeader("Authorization");

            /*
            // if there is no authorization information, send the authentication challenge again
            if (auth == null || auth.isBlank()) {

                LOGGER.info("No authorization header sent by the client.");

                sendAuthenticationChallenge(res);

                return false;
            }

            // if it is not HTTP Basic authentication, send the authentication challenge again
            if (!auth.toUpperCase().startsWith("BASIC ")) {

                LOGGER.warn("Basic authentication is expected. Clients sent instead: %s", auth);

                sendAuthenticationChallenge(res);

                return false;
            }
            */

            // perform Base64 decoding
            final String pair = new String(Base64.getDecoder().decode(auth.substring(6)));

            // userDetails[0] is the username; userDetails[1] is the password
            final String[] userDetails = pair.split(":", 2);

            user = new User(userDetails[0].toLowerCase().trim(), userDetails[1]);

            // check that email syntax is valid
            EmailValidator ev = EmailValidator.getInstance();

            if(!ev.isValid(user.getEmail()))
                throw new NumberFormatException("Invalid email address.");

            // check that password syntax is valid
            RegexValidator pv = new RegexValidator("^(?=.*[a-z])(?=.*[0-9])(?=.*[^\\w\\*])[^\\s]{6,20}$");

            if(!pv.isValid(user.getPassword()))
                throw new NumberFormatException("Invalid password.");

            // password validated, then encode it
            String password = PasswordGenerator.generatePassword(user.getPassword());

            // update user object with encoded password
            user = new User(user.getEmail(), password);

            // creates a new DAO for accessing the database
            user = new LoginDAO(con, user).access().getOutputParam();

            if (user != null) {
                LOGGER.info("User (%d) successfully logged in.", user.getUser_id());

                String token = JWT.encode(user);

                res.setStatus(HttpServletResponse.SC_OK);
                res.setContentType(JSON_UTF_8_MEDIA_TYPE);
                writeJwtAndUserJSON(res.getOutputStream(), user, token);
            } else { // it should not happen
                LOGGER.error("Fatal error while logging in user.");

                m = new Message("Cannot log in the user: Unexpected error.", ErrorCodes.UNEXPECTED_DB_ERROR, null);
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                res.setContentType(JSON_UTF_8_MEDIA_TYPE);
                m.toJSON(res.getOutputStream());
            }
        } catch (NumberFormatException ex) {
            LOGGER.error("Cannot log in the user: Invalid input parameters.", ex);

            m = new Message("Cannot log in the user: Invalid input parameters.", ErrorCodes.INVALID_INPUT_PARAMETER, ex.getMessage());
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            m.toJSON(res.getOutputStream());
        } catch (SQLException ex) {
            LOGGER.error("Cannot log in the user.", ex);

            m = new Message("Cannot log in the user.", ErrorCodes.UNEXPECTED_DB_ERROR, ex.getMessage());
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            m.toJSON(res.getOutputStream());
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Cannot log in the user: Unable to generate password encoding.", e);

            m = new Message("Cannot log in the user.", ErrorCodes.PASSWORD_ENCODING_ERROR, "Unable to generate password encoding.");
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            m.toJSON(res.getOutputStream());
        }
    }

    /**
     * Writes in the output stream the generated jwt and the user just logged in.
     *
     * @param out output stream
     * @param user user just logged in
     * @param token jwt just generated
     * @throws IOException if something goes wrong writing in the output stream
     */
    private void writeJwtAndUserJSON(final OutputStream out, User user, String token) throws IOException {

        if(out == null) {
            LOGGER.error("The output stream cannot be null.");
            throw new IOException("The output stream cannot be null.");
        }

        JsonFactory jf = new JsonFactory();
        jf.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
        jf.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);

        final JsonGenerator jg = jf.createGenerator(out);

        jg.writeStartObject();

        jg.writeFieldName("jwt");

        jg.writeStartObject();

        jg.writeStringField("access_token", token);

        jg.writeStringField("token_type", "Bearer");

        jg.writeNumberField("expires_in", 28800); // 8 hours in seconds

        jg.writeEndObject();

        jg.writeFieldName("user");

        jg.writeStartObject();

        jg.writeNumberField("user_id", user.getUser_id());

        jg.writeStringField("email", user.getEmail());

        jg.writeStringField("name", user.getName());

        jg.writeStringField("surname", user.getSurname());

        jg.writeStringField("stripe_id", user.getStripe_id());

        jg.writeStringField("role", user.getRole());

        jg.writeEndObject();

        jg.writeEndObject();

        jg.flush();

    }
}
