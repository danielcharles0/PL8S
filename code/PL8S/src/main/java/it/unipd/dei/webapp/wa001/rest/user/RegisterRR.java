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
import io.jsonwebtoken.JwtException;
import it.unipd.dei.webapp.wa001.database.user.PasswordGenerator;
import it.unipd.dei.webapp.wa001.database.user.RegisterDAO;
import it.unipd.dei.webapp.wa001.filter.JWT;
import it.unipd.dei.webapp.wa001.resource.dbentities.User;
import it.unipd.dei.webapp.wa001.resource.logging.Actions;
import com.stripe.exception.StripeException;
import it.unipd.dei.webapp.wa001.resource.Message;
import it.unipd.dei.webapp.wa001.resource.ErrorCodes;
import it.unipd.dei.webapp.wa001.resource.logging.LogContext;
import it.unipd.dei.webapp.wa001.rest.AbstractRR;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.validator.routines.*;

import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * A REST resource for registering a new user {@link RegisterRR}s. *
 */
public final class RegisterRR extends AbstractRR {

    /**
     * The JSON UTF-8 MIME media type
     */
    private static final String JSON_UTF_8_MEDIA_TYPE = "application/json; charset=utf-8";

    /**
     * Creates a new REST resource for registering a {@code User}.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @param con the connection to the database.
     */
    public RegisterRR(final HttpServletRequest req, final HttpServletResponse res, Connection con) {
        super(Actions.REGISTER_USER, req, res, con);
    }

    @Override
    protected void doServe() throws IOException {

        User user = null;
        Message m = null;

        try {
            user = User.fromJSON(req.getInputStream());

            //check that email syntax is valid
            EmailValidator ev = EmailValidator.getInstance();

            if(!ev.isValid(user.getEmail()))
                throw new NumberFormatException("Invalid email address.");

            //check that password syntax is valid
            RegexValidator pv = new RegexValidator("^(?=.*[a-z])(?=.*[0-9])(?=.*[^\\w\\*])[^\\s]{6,20}$");

            if(!pv.isValid(user.getPassword()))
                throw new NumberFormatException("Invalid password.");

            // password validated, then encode it
            String password = PasswordGenerator.generatePassword(user.getPassword());

            // if the comes from the admin, then the user role must be 'manager', otherwise 'customer'
            if(auth_user != null && auth_user.getRole().equals("admin"))
                user = new User(user.getUser_id(), user.getEmail(), password, user.getName(), user.getSurname(), user.getStripe_id(), "manager");
            else
                user = new User(user.getUser_id(), user.getEmail(), password, user.getName(), user.getSurname(), user.getStripe_id(), "customer");

            // creates a new DAO for accessing the database
            user = new RegisterDAO(con, user).access().getOutputParam();

            if (user != null) {
                LOGGER.info("User (%d) successfully registered.", user.getUser_id());

                String token = JWT.encode(user);

                res.setStatus(HttpServletResponse.SC_OK);
                res.setContentType(JSON_UTF_8_MEDIA_TYPE);

                writeJwtAndUserJSON(res.getOutputStream(), user, token);
            } else { // it should not happen
                LOGGER.error("Fatal error while creating user.");

                m = new Message("Cannot create the user: Unexpected error.", ErrorCodes.UNEXPECTED_DB_ERROR, null);
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                res.setContentType(JSON_UTF_8_MEDIA_TYPE);
                m.toJSON(res.getOutputStream());
            }
        } catch (NumberFormatException ex) {
            LOGGER.error("Cannot create the user: Invalid input parameters.", ex);

            m = new Message("Cannot create the user: Invalid input parameters.", ErrorCodes.INVALID_INPUT_PARAMETER, ex.getMessage());
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            m.toJSON(res.getOutputStream());
        } catch (SQLException ex) {
            LOGGER.error("Cannot create the user.", ex);

            m = new Message("Cannot create the user.", ErrorCodes.UNEXPECTED_DB_ERROR, ex.getMessage());
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            m.toJSON(res.getOutputStream());
        } catch (NoSuchAlgorithmException e){
            LOGGER.error("Cannot create the user: Unable to generate password encoding.", e);

            m = new Message("Cannot create the user.", ErrorCodes.PASSWORD_ENCODING_ERROR, "Unable to generate password encoding.");
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setContentType(JSON_UTF_8_MEDIA_TYPE);
            m.toJSON(res.getOutputStream());
        }
    }

    /**
     * Writes in the output stream the generated jwt and the user just registered.
     *
     * @param out output stream
     * @param user user just registered
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
