package it.unipd.dei.webapp.wa001.resource.dbentities;

import it.unipd.dei.webapp.wa001.resource.AbstractResource;
import com.fasterxml.jackson.core.*;
import java.io.*;

/**
 * Represents the data about a Dish
 */
public class Dish extends AbstractResource {
    /**
     * The dish identifier
     */
    private final int dish_id;
    /**
     * The Dish price
     */
    private final float price;
    /**
     * The Dish name
     */
    private final String name;
    /**
     * The deletion flag. It indicates if a dish is not on the menu anymore
     */
    private final boolean isDeleted;
    /**
     * The restaurant that offers this Dish
     */
    private final int restaurant;

    /**
     * It creates a {@link Dish}
     * @param dish_id The dish identifier
     * @param price The Dish price
     * @param name The Dish name
	 * @param isDeleted true if the dish is deleted, false otherwise
     * @param restaurant The restaurant that offers this Dish
     */
    public Dish(int dish_id, float price, String name, boolean isDeleted, int restaurant) {
        this.dish_id = dish_id;
        this.price = price;
        this.name = name;
        this.isDeleted = isDeleted;
        this.restaurant = restaurant;
    }

    /**
     * It returns the Dish identifier
     * @return the Dish identifier
     */
    public final int getDish_id() {
        return dish_id;
    }

    /**
     * It returns the Dish price
     * @return the Dish price
     */
    public final float getPrice() {
        return price;
    }

    /**
     * It returns the Dish name
     * @return the Dish name
     */
    public final String getName() {
        return name;
    }

    /**
     * It returns true if the Dish has been deleted from the menu, false otherwise
     * @return true if the Dish has been deleted from the menu, false otherwise
     */
    public final boolean isDeleted() {
        return isDeleted;
    }

    /**
     * It returns the identifier of the Restaurant that offers the Dish
     * @return the identifier of the Restaurant that offers the Dish
     */
    public final int getRestaurant() {
        return restaurant;
    }

    /**
     * It writes the Dish in the output stream
     *
     * @param out output stream in which to write
     */
    @Override
    protected final void writeJSON(final OutputStream out) throws IOException {

        final JsonGenerator jg = JSON_FACTORY.createGenerator(out);

        jg.writeStartObject();

        jg.writeFieldName("dish");

        jg.writeStartObject();

        jg.writeNumberField("dish_id", dish_id);

        jg.writeStringField("name", name);

        jg.writeNumberField("price", price);

        jg.writeBooleanField("isDeleted", isDeleted);

        jg.writeNumberField("restaurant", restaurant);

        jg.writeEndObject();

        jg.writeEndObject();

        jg.flush();
    }

    /**
     * Creates a {@code Dish} from its JSON representation.
     *
     * @param in the input stream containing the JSON document.
     *
     * @return the {@code User} created from the JSON representation.
     *
     * @throws IOException if something goes wrong while parsing.
     */
    public static Dish fromJSON(final InputStream in) throws IOException  {

        // the fields read from JSON
        int jDish = -1;
        String jName = null;
        float jPrice = -1;
        boolean jIsDeleted = false;
        int jRestaurant = -1;

        try {
            final JsonParser jp = JSON_FACTORY.createParser(in);

            // while we are not on the start of an element or the element is not
            // a token element, advance to the next element (if any)
            while (jp.getCurrentToken() != JsonToken.FIELD_NAME || !"dish".equals(jp.getCurrentName())) {

                // there are no more events
                if (jp.nextToken() == null) {
                    LOGGER.error("No Dish object found in the stream.");
                    throw new EOFException("Unable to parse JSON: no Dish object found.");
                }
            }

            while (jp.nextToken() != JsonToken.END_OBJECT) {

                if (jp.getCurrentToken() == JsonToken.FIELD_NAME) {

                    switch (jp.getCurrentName()) {
                        case "dish_id":
                            jp.nextToken();
                            jDish = jp.getIntValue();
                            break;
                        case "name":
                            jp.nextToken();
                            jName = jp.getText().trim();
                            break;
                        case "price":
                            jp.nextToken();
                            jPrice = jp.getFloatValue();
                            break;
                        case "isDeleted":
                            jp.nextToken();
                            jIsDeleted = jp.getBooleanValue();
                            break;
                        case "restaurant":
                            jp.nextToken();
                            jRestaurant = jp.getIntValue();
                            break;
                    }
                }
            }
        } catch(IOException e) {
            LOGGER.error("Unable to parse a Dish object from JSON.", e);
            throw e;
        }

        return new Dish(jDish, jPrice, jName, jIsDeleted, jRestaurant);
    }

}
