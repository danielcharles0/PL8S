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

package it.unipd.dei.webapp.wa001.resource.dbentities;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.*;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import it.unipd.dei.webapp.wa001.resource.AbstractResource;

/**
 * Represents the data about a User.
 */
public class User extends AbstractResource{

	/**
	 * The user identifier
	 */
	private final int user_id;

	/**
	 * The user email
	 */
	private final String email;

	/**
	 * The user password
	 */
	private final String password;
	/**
	 * The user first name
	 */
	private final String name;
	/**
	 * The user surname
	 */
	private final String surname;

	/**
	 * The Stripe ID necessary to perform a payment
	 */
	private final String stripe_id;

	/**
	 * The role of the user. E.g. customer, manager, admin
	 */
	private final String role;

	/**
	 * @param user_id The user identifier
	 * @param email The user email
	 * @param password The user password
	 * @param name The user first name
	 * @param surname The user surname
	 * @param stripe_id The Stripe ID necessary to perform a payment
	 * @param role The role of the user. E.g. customer, manager, admin
	 */
	public User(int user_id, String email, String password, String name, String surname, String stripe_id, String role) {
		this.user_id = user_id;
		this.email = email;
		this.password = password;
		this.name = name;
		this.surname = surname;
		this.stripe_id = stripe_id;
		this.role = role;
	}

	/**
	 * @param email The user email
	 * @param password The user password
	 * @param name The user first name
	 * @param surname The user surname
	 * @param role The role of the user. E.g. customer, manager, admin
	 * 
	 * The other parameters are setted to the default value
	 */
	public User(String email, String password, String name, String surname, String role){
		this(-1, email, password, name, surname, null, role);
	}

	/**
	 * User for Login
	 * @param email user email
	 * @param password user password
	 * 
	 * The other parameters are setted to the default value
	 */
	public User(String email, String password){
		this(-1, email, password, null, null, null, null);
	}

	/**
	 * User for authentication.
	 * @param user_id id of the user
	 * @param stripe_id id of stripe customer
	 * @param role role of the user
	 * 
	 * The other parameters are setted to the default value
	 */
	public User(int user_id, String stripe_id, String role){
		this(user_id, null, null, null, null, stripe_id, role);
	}

	/**
	 * It returns the User identifier
	 * @return the User identifier
	 */
	public final int getUser_id() {
		return user_id;
	}

	/**
	 * It returns the User password
	 * @return the User password
	 */
	public final String getPassword() {
		return password;
	}

	/**
	 * It returns the Stripe ID necessary to perform a payment
	 * @return the Stripe ID necessary to perform a payment
	 */
	public final String getStripe_id() {
		return stripe_id;
	}

	/**
	 * It returns the role of the user. E.g. customer, manager, admin
	 * @return the role of the user. E.g. customer, manager, admin
	 */
	public final String getRole() {
		return role;
	}

	/**
	 * It returns the user email
	 * @return the user email
	 */
	public final String getEmail() {
		return email;
	}

	/**
	 * It returns the user first name
	 * @return the user first name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * It returns the user surname
	 * @return the user surname
	 */
	public final String getSurname() {
		return surname;
	}

	/**
	 * It writes the User in the output stream
	 *
	 * @param out output stream in which to write
	 */
	@Override
	protected final void writeJSON(final OutputStream out) throws IOException {

		final JsonGenerator jg = JSON_FACTORY.createGenerator(out);

		jg.writeStartObject();

		jg.writeFieldName("user");

		jg.writeStartObject();

		jg.writeNumberField("user_id", user_id);

		jg.writeStringField("email", email);

		jg.writeStringField("name", name);

		jg.writeStringField("surname", surname);

		jg.writeStringField("stripe_id", stripe_id);

		jg.writeStringField("role", role);

		jg.writeEndObject();

		jg.writeEndObject();

		jg.flush();
	}

	/**
	 * Creates a {@code User} from its JSON representation.
	 *
	 * @param in the input stream containing the JSON document.
	 *
	 * @return the {@code User} created from the JSON representation.
	 *
	 * @throws IOException if something goes wrong while parsing.
	 */
	public static User fromJSON(final InputStream in) throws IOException  {

		// the fields read from JSON
		int jUser = -1;
		String jEmail = null;
		String jPassword = null;
		String jName = null;
		String jSurname = null;
		String jStripe = null;
		String jRole = null;

		try {
			final JsonParser jp = JSON_FACTORY.createParser(in);

			// while we are not on the start of an element or the element is not
			// a token element, advance to the next element (if any)
			while (jp.getCurrentToken() != JsonToken.FIELD_NAME || !"user".equals(jp.getCurrentName())) {

				// there are no more events
				if (jp.nextToken() == null) {
					LOGGER.error("No User object found in the stream.");
					throw new EOFException("Unable to parse JSON: no User object found.");
				}
			}

			while (jp.nextToken() != JsonToken.END_OBJECT) {

				if (jp.getCurrentToken() == JsonToken.FIELD_NAME) {

					switch (jp.getCurrentName()) {
						case "user_id":
							jp.nextToken();
							jUser = jp.getIntValue();
							break;
						case "email":
							jp.nextToken();
							jEmail = jp.getText().trim().toLowerCase();
							break;
						case "password":
							jp.nextToken();
							jPassword = jp.getText().trim();
							break;
						case "name":
							jp.nextToken();
							jName = jp.getText().trim();
							break;
						case "surname":
							jp.nextToken();
							jSurname = jp.getText().trim();
							break;
						case "stripe_id":
							jp.nextToken();
							jStripe = jp.getText().trim();
							break;
						case "role":
							jp.nextToken();
							jRole = jp.getText().trim();
							break;
					}
				}
			}
		} catch(IOException e) {
			LOGGER.error("Unable to parse a User object from JSON.", e);
			throw e;
		}

		return new User(jUser, jEmail, jPassword, jName, jSurname, jStripe, jRole);
	}

}
