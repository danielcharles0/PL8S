package it.unipd.dei.webapp.wa001.resource.dbentities;

import java.sql.Time;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import it.unipd.dei.webapp.wa001.resource.AbstractResource;

import java.io.*;
import java.util.Iterator;

/**
 * Represents the data about a Restaurant
 */
public class Restaurant extends AbstractResource {
    /**
     * The Restaurant identifier
     */
    private final int restaurant_id;
    /**
     * The Restaurant name
     */
    private final String name;
    /**
     * The Restaurant description
     */
    private final String description;
    /**
     * The id of the manager of the restaurant
     */
    private final int manager;
    /**
     * The email of the manager of the restaurant
     */
    private final String manager_email;
    /**
     * The opening time
     */
    private final Time opening_at;
    /**
     * The closing time
     */
    private final Time closing_at;
	
	/**
	 * The countries of the cuisines of the restaurant
	*/
	private final String[] countries;

	/**
	 * The cuisines types of the restaurant E.g, "vegetarian, vegan" or "carnivorous"
	*/
	private final String[] cuisine_types;

    /**
     * It creates a Restaurant
     *
     * @param restaurant_id The Restaurant identifier
     * @param name The Restaurant name
     * @param description The Restaurant description
     * @param manager The manager of the restaurant
     * @param opening_at The opening time
     * @param closing_at The closing time
	 * @param countries The countries
     * @param cuisine_types The cuisine types. E.g, "vegetarian, vegan" or "carnivorous"
     */
    public Restaurant(int restaurant_id, String name, String description, int manager, Time opening_at, Time closing_at, String[] countries, String[] cuisine_types) {
        this.restaurant_id = restaurant_id;
        this.name = name;
        this.description = description;
        this.manager = manager;
        this.opening_at = opening_at;
        this.closing_at = closing_at;
		this.countries = countries;
        this.cuisine_types = cuisine_types;

        this.manager_email = "";
    }

    /**
     * It creates a Restaurant without countries and cuisine_types
     *
     * @param restaurant_id The Restaurant identifier
     * @param name The Restaurant name
     * @param description The Restaurant description
     * @param manager The manager of the restaurant
     * @param opening_at The opening time
     * @param closing_at The closing time
     */
    public Restaurant(int restaurant_id, String name, String description, int manager, Time opening_at, Time closing_at){
        this(restaurant_id, name, description, manager, opening_at, closing_at, null, null);
    }

    /**
     * It creates a Restaurant for the admin view, it has manager's email instead of id
     *
     * @param restaurant_id The Restaurant identifier
     * @param name The Restaurant name
     * @param description The Restaurant description
     * @param manager The id of the manager of the restaurant
     * @param manager_email The email of the manager of the restaurant
     * @param opening_at The opening time
     * @param closing_at The closing time
     * @param countries The countries
     * @param cuisine_types The cuisine types. E.g, "vegetarian, vegan" or "carnivorous"
     */
    public Restaurant(int restaurant_id, String name, String description, int manager, String manager_email, Time opening_at, Time closing_at, String[] countries, String[] cuisine_types) {
        this.restaurant_id = restaurant_id;
        this.name = name;
        this.description = description;
        this.manager = manager;
        this.manager_email = manager_email;
        this.opening_at = opening_at;
        this.closing_at = closing_at;
        this.countries = countries;
        this.cuisine_types = cuisine_types;
    }

    /**
     * It returns the Restaurant identifier
     * @return The Restaurant identifier
     */
    public final int getRestaurant_id() {
        return restaurant_id;
    }

    /**
     * It returns he Restaurant name
     * @return The Restaurant name
     */
    public final String getName() {
        return name;
    }

    /**
     * It returns the Restaurant description
     * @return The Restaurant description
     */
    public final String getDescription() {
        return description;
    }

    /**
     * It returns the Restaurant manager
     * @return The Restaurant manager
     */
    public final int getManager() {
		return manager;
	}

    /**
     * It returns the Restaurant manager email
     * @return The Restaurant manager email
     */
    public final String getManager_email() {
        return manager_email;
    }

    /**
     * It returns the opening time
     * @return The opening time
     */
    public final Time getOpening_at() {
        return opening_at;
    }

    /**
     * It returns the closing time
     * @return The closing time
     */
    public final Time getClosing_at() {
        return closing_at;
    }
    
	/**
     * It returns the countries of the cuisines of the restaurant
     * @return The countries of the cuisines of the restaurant
     */
    public final String[] getCountries() {
        return countries;
    }

    /**
     * It returns the cuisine types. E.g, "vegetarian, vegan" or "carnivorous"
     * @return The cuisine types. E.g, "vegetarian, vegan" or "carnivorous"
     */
    public final String[] getCuisine_types() {
        return cuisine_types;
    }

    /**
     * It writes the Restaurant in the output stream
     *
     * @param out output stream in which to write
     */
	@Override
	protected final void writeJSON(final OutputStream out) throws IOException {

		final JsonGenerator jg = JSON_FACTORY.createGenerator(out);

		jg.writeStartObject();

		jg.writeFieldName("restaurant");

		jg.writeStartObject();

		jg.writeNumberField("restaurant_id", restaurant_id);

		jg.writeStringField("name", name);
		
		jg.writeStringField("description", description);

		jg.writeNumberField("manager", manager);

        jg.writeStringField("manager_email", manager_email);

		jg.writeStringField("opening_at", opening_at.toString());

		jg.writeStringField("closing_at", closing_at.toString());

        jg.writeFieldName("countries");

		if(countries != null)
			jg.writeArray(countries, 0, countries.length);
		else{
			jg.writeStartArray();
			jg.writeEndArray();
		}

		jg.writeFieldName("cuisine_types");

		if(cuisine_types != null)
        	jg.writeArray(cuisine_types, 0, cuisine_types.length);
		else{
			jg.writeStartArray();
			jg.writeEndArray();
		}

		jg.writeEndObject();

		jg.writeEndObject();

		jg.flush();
	}

    /**
     * Creates a {@code Restaurant} from its JSON representation.
     *
     * @param in the input stream containing the JSON document.
     *
     * @return the {@code User} created from the JSON representation.
     *
     * @throws IOException if something goes wrong while parsing.
     */
    public static Restaurant fromJSON(final InputStream in) throws IOException  {

        // the fields read from JSON
        int jRestaurant = -1;
        String jName = null;
        String jDescription = null;
        int jManager = -1;
        Time jOpening_at = null;
        Time jClosing_at = null;
        String[] jCountries = null;
        String[] jCuisine_types = null;

        try {
            final JsonParser jp = JSON_FACTORY.createParser(in);

            // while we are not on the start of an element or the element is not
            // a token element, advance to the next element (if any)
            while (jp.getCurrentToken() != JsonToken.FIELD_NAME || !"restaurant".equals(jp.getCurrentName())) {

                // there are no more events
                if (jp.nextToken() == null) {
                    LOGGER.error("No Restaurant object found in the stream.");
                    throw new EOFException("Unable to parse JSON: no Restaurant object found.");
                }
            }

            while (jp.nextToken() != JsonToken.END_OBJECT) {

                if (jp.getCurrentToken() == JsonToken.FIELD_NAME) {

                    switch (jp.getCurrentName()) {
                        case "restaurant_id":
                            jp.nextToken();
                            jRestaurant = jp.getIntValue();
                            break;
                        case "name":
                            jp.nextToken();
                            jName = jp.getText().trim();
                            break;
                        case "description":
                            jp.nextToken();
                            jDescription = jp.getText().trim();
                            break;
                        case "manager":
                            jp.nextToken();
                            jManager = jp.getIntValue();
                            break;
                        case "opening_at":
                            jp.nextToken();
                            jOpening_at = Time.valueOf(jp.getText().trim());
                            break;
                        case "closing_at":
                            jp.nextToken();
                            jClosing_at = Time.valueOf(jp.getText().trim());
                            break;
                        case "countries":
                            jp.nextToken();
                            ArrayNode cou = new ObjectMapper().readTree(jp);
                            Iterator<JsonNode> cou_iterator = cou.elements();
                            jCountries = new String[cou.size()];
                            for (int i = 0; i < cou.size(); i++)
                                if (cou_iterator.hasNext())
                                    jCountries[i] = cou_iterator.next().asText();
                            break;
                        case "cuisine_types":
                            jp.nextToken();
                            ArrayNode cui = new ObjectMapper().readTree(jp);
                            Iterator<JsonNode> cui_iterator = cui.elements();
                            jCuisine_types = new String[cui.size()];
                            for (int i = 0; i < cui.size(); i++)
                                if (cui_iterator.hasNext())
                                    jCuisine_types[i] = cui_iterator.next().asText();
                            break;
                    }
                }
            }
        } catch(IOException e) {
            LOGGER.error("Unable to parse a Restaurant object from JSON.", e);
            throw e;
        }

        return new Restaurant(jRestaurant, jName, jDescription, jManager, jOpening_at, jClosing_at, jCountries, jCuisine_types);
    }
}
