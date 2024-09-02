package it.unipd.dei.webapp.wa001.resource.dbentities;

import it.unipd.dei.webapp.wa001.resource.AbstractResource;

import com.fasterxml.jackson.core.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the data about a Dish
 */
public class DishIngredient extends AbstractResource {
	
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
     * The Dish diet
     */
    private final String diet;
    /**
     * Ingredients the dish is made of.
     */
    private final Ingredient[] ingredients;

	/**
	 * It computes the dish diet given its ingredients
	 * @param ings dish_id The dish identifier
     * @return the diet of the dish
	*/
	private static String ComputeDiet(Ingredient[] ings){
		
		String diet = "vegan";

		for (Ingredient ing : ings) {
			if(diet.equals("vegan"))
				diet = ing.getDiet();
			else if(diet.equals("vegetarian")){
				if(!ing.getDiet().equals("vegan")){
					diet = ing.getDiet();
				}
			}
			else
				break;
		}

		return diet;
	}

	/**
	 * It builds the array of ingredients starting from the components
	 * @param ingredient_ids the ingredients ids the dish is composed of
	 * @param ingredient_names the ingredients names the dish is composed of
	 * @param ingredient_diets the ingredients diets the dish is composed of
	 * @return the array of ingredients
	 */
	private static Ingredient[] BuildIngredients(Integer[] ingredient_ids, String[] ingredient_names, String[] ingredient_diets){
		
		Ingredient[] ings = new Ingredient[ingredient_ids.length];

		for (int i = 0; i < ingredient_ids.length; i++)
			ings[i] = new Ingredient(ingredient_ids[i], ingredient_diets[i], ingredient_names[i]);
		
		return ings;
	}

	/**
     * It creates a {@link DishIngredient}
     * @param dish_id The dish identifier
     * @param price The Dish price
     * @param name The Dish name
     * @param isDeleted true if the dish is deleted, false otherwise
     * @param restaurant The restaurant that offers this Dish
	 * @param diet The Dish diet
     * @param ingredients the ingredients the dish is composed of
     */
    public DishIngredient(int dish_id, float price, String name, boolean isDeleted, int restaurant, String diet, Ingredient[] ingredients) {
        this.dish_id = dish_id;
        this.price = price;
        this.name = name;
        this.isDeleted = isDeleted;
        this.restaurant = restaurant;
		this.diet = diet;
        this.ingredients = ingredients;
    }

    /**
     * It creates a {@link DishIngredient}
     * @param dish_id The dish identifier
     * @param price The Dish price
     * @param name The Dish name
     * @param isDeleted true if the dish is deleted, false otherwise
     * @param restaurant The restaurant that offers this Dish
     * @param ingredients the ingredients the dish is composed of
     */
    public DishIngredient(int dish_id, float price, String name, boolean isDeleted, int restaurant, Ingredient[] ingredients) {
        this(dish_id, price, name, isDeleted, restaurant, ComputeDiet(ingredients), ingredients);
    }

	/**
     * It creates a {@link DishIngredient}
     * @param dish_id The dish identifier
     * @param price The Dish price
     * @param name The Dish name
     * @param isDeleted true if the dish is deleted, false otherwise
     * @param restaurant The restaurant that offers this Dish
	 * @param diet The Dish diet
     * @param ingredient_ids the ingredients ids the dish is composed of
	 * @param ingredient_names the ingredients names the dish is composed of
	 * @param ingredient_diets the ingredients diets the dish is composed of
     */
    public DishIngredient(int dish_id, float price, String name, boolean isDeleted, int restaurant, String diet, Integer[] ingredient_ids, String[] ingredient_names, String[] ingredient_diets) {
        this(dish_id, price, name, isDeleted, restaurant, diet, BuildIngredients(ingredient_ids, ingredient_names, ingredient_diets));
    }

	/**
     * It creates a {@link DishIngredient}
     * @param dish_id The dish identifier
     * @param price The Dish price
     * @param name The Dish name
     * @param isDeleted true if the dish is deleted, false otherwise
     * @param restaurant The restaurant that offers this Dish
     * @param ingredient_ids the ingredients ids the dish is composed of
	 * @param ingredient_names the ingredients names the dish is composed of
	 * @param ingredient_diets the ingredients diets the dish is composed of
     */
    public DishIngredient(int dish_id, float price, String name, boolean isDeleted, int restaurant, Integer[] ingredient_ids, String[] ingredient_names, String[] ingredient_diets) {
        this(dish_id, price, name, isDeleted, restaurant, BuildIngredients(ingredient_ids, ingredient_names, ingredient_diets));
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
     * It returns the Dish diet
     * @return the Dish diet
     */
    public final String getDiet() {
        return diet;
    }

    /**
     * It returns the array containing the ingredients the dish is made of
     * @return the ingredients the dish is made of
     */
    public final Ingredient[] getIngredients(){ return ingredients; }

    /**
     * It writes the DishIngredient in the output stream
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

        jg.writeStringField("diet", diet);

        jg.writeNumberField("restaurant", restaurant);

		jg.writeArrayFieldStart("ingredients");

		jg.flush();

		ingredients[0].toJSON(out);

        for(int i = 1; i < ingredients.length; i++){
			jg.writeRaw(',');
			jg.flush();
            ingredients[i].toJSON(out);
		}

        jg.writeEndArray();

        jg.writeEndObject();

        jg.writeEndObject();

        jg.flush();
    }

    /**
     * Creates a {@code DishIngredient} from its JSON representation.
     *
     * @param in the input stream containing the JSON document.
     *
     * @return the {@code User} created from the JSON representation.
     *
     * @throws IOException if something goes wrong while parsing.
     */
    public static DishIngredient fromJSON(final InputStream in) throws IOException  {

        // the fields read from JSON
        int jDish = -1;
        String jName = null;
        float jPrice = -1;
        boolean jIsDeleted = false;
        int jRestaurant = -1;
        List<Ingredient> jIngredients = new ArrayList<>();

        try {
            final JsonParser jp = JSON_FACTORY.createParser(in);

            // while we are not on the start of an element or the element is not
            // a token element, advance to the next element (if any)
            while (jp.getCurrentToken() != JsonToken.FIELD_NAME || !"dish".equals(jp.getCurrentName())) {

                // there are no more events
                if (jp.nextToken() == null) {
                    LOGGER.error("No DishIngredient object found in the stream.");
                    throw new EOFException("Unable to parse JSON: no DishIngredient object found.");
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
                        case "ingredients":
                            jp.nextToken();
                            while(jp.nextToken() != JsonToken.END_ARRAY){
                                String jIngName = null;
                                String jDiet = null;
                                while(jp.nextToken() != JsonToken.END_OBJECT) {
                                    switch (jp.currentName()) {
                                        case "name":
                                            jp.nextToken();
                                            jIngName = jp.getText();
                                            break;
                                        case "diet":
                                            jp.nextToken();
                                            jDiet = jp.getText();
                                            break;
                                    }
                                }
                                if (jIngName != null && jDiet != null)
                                    jIngredients.add(new Ingredient(-1, jDiet, jIngName));
                            }
                            break;
                    }
                }
            }
        } catch(IOException e) {
            LOGGER.error("Unable to parse a DishIngredient object from JSON.", e);
            throw e;
        }
        Ingredient[] array_ingredients = new Ingredient[jIngredients.size()];
        jIngredients.toArray(array_ingredients);
        return new DishIngredient(jDish, jPrice, jName, jIsDeleted, jRestaurant, array_ingredients);
    }

}