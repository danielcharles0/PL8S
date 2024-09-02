package it.unipd.dei.webapp.wa001.resource.dbentities;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import it.unipd.dei.webapp.wa001.resource.AbstractResource;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Represents the data about the Order and Dish many-to-many relation
 */
public class OrderDish extends AbstractResource {
    private final int order;
    private final int dish;
    private final int quantity;

	/**
     * @param order the Order identifier
     * @param dish the Dish identifier
     * @param quantity the Dish quantity
     */
    public OrderDish(int order, int dish, int quantity) {
        this.order = order;
        this.dish = dish;
        this.quantity = quantity;
    }

	/**
	 * It returns the Dish identifier
	 * @return the Dish identifier
	 */
    public final int getDish() {
        return dish;
    }

	/**
	 * It returns the Dish quantity
	 * @return the Dish quantity
	 */
    public final int getQuantity() {
        return quantity;
    }

	/**
	 * It returns the Order identifier
	 * @return the Order identifier
	 */
    public final int getOrder() {
        return order;
    }

    @Override
    protected final void writeJSON(final OutputStream out) throws IOException {

        final JsonGenerator jg = JSON_FACTORY.createGenerator(out);

        jg.writeStartObject();

        jg.writeFieldName("order_dish");

        jg.writeStartObject();

        jg.writeNumberField("order", order);

        jg.writeNumberField("dish", dish);

        jg.writeNumberField("quantity", quantity);

        jg.writeEndObject();

        jg.writeEndObject();

        jg.flush();
    }

    /**
     * Creates a {@code OrderDish} from its JSON representation.
     *
     * @param in the input stream containing the JSON document.
     *
     * @return the {@code OrderDish} created from the JSON representation.
     *
     * @throws IOException if something goes wrong while parsing.
     */
    public static OrderDish fromJSON(final InputStream in) throws IOException  {

        // the fields read from JSON
        int jOrder = -1;
        int jDish = -1;
        int jQuantity = -1;

        try {
            final JsonParser jp = JSON_FACTORY.createParser(in);

            // while we are not on the start of an element or the element is not
            // a token element, advance to the next element (if any)
            while (jp.getCurrentToken() != JsonToken.FIELD_NAME || !"order_dish".equals(jp.getCurrentName())) {

                // there are no more events
                if (jp.nextToken() == null) {
                    LOGGER.error("No OrderDish object found in the stream.");
                    throw new EOFException("Unable to parse JSON: no OrderDish object found.");
                }
            }

            while (jp.nextToken() != JsonToken.END_OBJECT) {

                if (jp.getCurrentToken() == JsonToken.FIELD_NAME) {

                    switch (jp.getCurrentName()) {
                        case "order_id":
                            jp.nextToken();
                            jOrder = jp.getIntValue();
                            break;
                        case "dish_id":
                            jp.nextToken();
                            jDish = jp.getIntValue();
                            break;
                        case "quantity":
                            jp.nextToken();
                            jQuantity = jp.getIntValue();
                            break;
                    }
                }
            }
        } catch(IOException e) {
            LOGGER.error("Unable to parse a OrderDish object from JSON.", e);
            throw e;
        }

        return new OrderDish(jOrder, jDish, jQuantity);
    }
}
