package it.unipd.dei.webapp.wa001.resource.dbentities;

import com.fasterxml.jackson.core.JsonGenerator;
import it.unipd.dei.webapp.wa001.resource.AbstractResource;

import java.sql.Timestamp;
import java.io.*;

/**
 * Represents the data about an Order
 */
public class Order extends AbstractResource {

    /**
     * The Order identifier
     */
    private final int order_id;

    /**
     * The price of the Order
     */
    private final float price;

    /**
     * Data and time the order has been placed
     */
    private final Timestamp placedOn;

    /**
     * The status of an Order. It can be 'pending' or 'completed'
     */
    private final String status;

    /**
     * The user tha placed the Order
     */
    private final int user;

    /**
     * @param order_id The Order identifier
     * @param price The price of the Order
     * @param placedOn Data and time the order has been placed
     * @param status The status of an Order. It can be 'pending' or 'completed'
     * @param user The user tha placed the Order
     */
    public Order(int order_id, float price, Timestamp placedOn, String status, int user) {
        this.order_id = order_id;
        this.price = price;
        this.placedOn = placedOn;
        this.status = status;
        this.user = user;
    }

    /**
     * It returns the Order Identifier
     * @return It returns the Order Identifier
     */
    public final int getOrder_id() {
        return order_id;
    }

    /**
     * It returns the Price of the Order
     * @return the Price of the Order
     */
    public final float getPrice() {

        return price;
    }

	/**
     * It returns the Price of the Order in cents
     * @return the Price of the Order in cents
     */
    public final long getCentsPrice() {

        return (long)price * 100;
    }

    /**
     * It returns the data and time the order has been placed
     * @return The data and time the order has been placed
     */
    public final Timestamp getPlacedOn() {

        return placedOn;
    }

    /**
     * It returns the status of an Order. It can be 'pending' or 'completed'
     * @return The status of an Order. It can be 'pending' or 'completed'
     */
    public final String getStatus() {

        return status;
    }

    /**
     * It returns the User tha placed the Order
     * @return The User tha placed the Order
     */
    public final int getUser() {
        return user;
    }


    //Writes the order in JSON format
    @Override
    protected final void writeJSON(final OutputStream out) throws IOException {

        final JsonGenerator jg = JSON_FACTORY.createGenerator(out);

        jg.writeStartObject();

        jg.writeFieldName("order");

        jg.writeStartObject();

        jg.writeNumberField("order_id", order_id);

        jg.writeNumberField("price", price);

        jg.writeStringField("placedOn", placedOn.toString());

        jg.writeStringField("status", status);

        jg.writeNumberField("user", user);

        jg.writeEndObject();

        jg.writeEndObject();

        jg.flush();
    }
}
