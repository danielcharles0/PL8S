package it.unipd.dei.webapp.wa001.resource.dbentities;

import com.fasterxml.jackson.core.JsonGenerator;
import it.unipd.dei.webapp.wa001.resource.AbstractResource;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents the data about a Dish in an order
 */
public class FullDish extends AbstractResource {

	/**
     * The {@link Dish}
     */
    private final Dish dish;

    /**
     * The isDeleted property of the dish in the order
     */
    private final boolean isDeleted;

	/**
     * The quantity of the dish in the order
     */
    private final int quantity;

    /**
     * The restaurant that prepares the dish
     */
    private final String restaurant;

	/**
     * It creates an {@link FullDish}
	 * 
     * @param dish The dish
     * @param isDeleted the isDeleted property of the dish
     * @param quantity The quantity of the dish in the order
     * @param restaurant the name of the restaurant that made the dish
     */
    public FullDish(Dish dish, boolean isDeleted, int quantity, String restaurant){
        this.dish = dish;
        this.isDeleted = isDeleted;
        this.quantity = quantity;
        this.restaurant = restaurant;
    }

    /**
     * It returns the dish_id of the dish
     * @return the dish_id
     */
    public final int getDish_id(){ return dish.getDish_id(); }

    /**
     * It returns the dish name
     * @return the dish name
     */
    public final String getName(){ return dish.getName(); }

    /**
     * It returns the dish price
     * @return the dish price
     */
    public final float getPrice(){ return dish.getPrice(); }

    /**
     * It returns the restaurant_id associated with the dish
     * @return the restaurant_id of the dish
     */
    public final int getRestaurant(){ return dish.getRestaurant(); }

    /**
     * It returns the dish isDeleted property
     * @return the dish isDeleted property
     */
    public final boolean getIsDeleted(){ return isDeleted; }

	/**
     * It returns the dish quantity
     * @return the dish quantity
     */
    public final int getQuantity(){ return quantity; }

    /**
     * It returns the dish's restaurant
     * @return the dish's restaurant
     */
    public final String getRestaurantName(){ return restaurant; }

    @Override
    protected final void writeJSON(final OutputStream out) throws IOException {

        final JsonGenerator jg = JSON_FACTORY.createGenerator(out);

        jg.writeStartObject();

        jg.writeFieldName("dish");

        jg.writeStartObject();

        jg.writeNumberField("dish_id", dish.getDish_id());

        jg.writeStringField("name", dish.getName());

        jg.writeNumberField("price", dish.getPrice());

        jg.writeNumberField("restaurant", dish.getRestaurant());

        jg.writeNumberField("quantity", quantity);

        jg.writeEndObject();

        jg.writeEndObject();

        jg.flush();
    }
}
