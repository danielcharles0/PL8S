package it.unipd.dei.webapp.wa001.resource.dbentities;

import com.fasterxml.jackson.core.JsonGenerator;
import it.unipd.dei.webapp.wa001.resource.AbstractResource;
import it.unipd.dei.webapp.wa001.resource.Resource;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Represents the data about an order and its list of dishes
 */
public class FullOrder extends AbstractResource {

    /**
     * The {@link Order}
     */
    private final Order order;

    /**
     * The list of {@link FullDish}
     */
    private final List<FullDish> dishes;

    /**
     * It creates an {@link FullOrder}
     *
     * @param order The order
     * @param dishes The list of dishes in the order
     */
    public FullOrder(Order order, List<FullDish> dishes){
        this.order = order;
        this.dishes = dishes;
    }

	/**
     * It returns the Order object
     * @return It returns the Order object
     */
    public final Order getOrder() {
        return order;
    }

    @Override
    protected final void writeJSON(final OutputStream out) throws IOException {

        final JsonGenerator jg = JSON_FACTORY.createGenerator(out);

        jg.writeStartObject();

        jg.writeFieldName("order");

        jg.writeStartObject();

        jg.writeNumberField("order_id", order.getOrder_id());

        jg.writeNumberField("price", order.getPrice());

        jg.writeStringField("placedOn", order.getPlacedOn().toString());

        jg.writeStringField("status", order.getStatus());

        jg.writeNumberField("user", order.getUser());

        jg.writeFieldName("dishes");

        jg.writeStartArray();

        jg.flush();

        for(final FullDish dish : dishes) {

            jg.writeStartObject();

            jg.writeNumberField("dish_id", dish.getDish_id());

            jg.writeStringField("name", dish.getName());

            jg.writeNumberField("price", dish.getPrice());

            jg.writeNumberField("restaurant", dish.getRestaurant());

            jg.writeStringField("restaurant_name", dish.getRestaurantName());

            jg.writeNumberField("quantity", dish.getQuantity());

            jg.writeEndObject();

        }

        jg.writeEndArray();

        jg.writeEndObject();

        jg.writeEndObject();

        jg.flush();
    }
}
