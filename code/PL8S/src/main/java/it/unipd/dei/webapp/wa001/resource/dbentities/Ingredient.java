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
 * Represents the data about an Ingredient
 */
public class Ingredient extends AbstractResource{
    /**
     * The Ingredient identifier
     */
    private final int ingredient_id;
    /**
     * The diet an ingredient belongs to. It can be
     * 'vegan' 'vegetarian' or 'carnivorous'
     */
    private final String diet;
    /**
     * The name of the ingredient
     */
    private final String name;

    /**
     * It creates an Ingredient
     * @param ingredient_id The Ingredient identifier
     * @param diet The diet an ingredient belongs to. It can be'vegan'
     *    'vegetarian' or 'carnivorous'
     * @param name The name of the ingredient
     */
    public Ingredient(int ingredient_id, String diet, String name) {
        this.ingredient_id = ingredient_id;
        this.diet = diet;
        this.name = name;
    }

    /**
     * It returns the Ingredient identifier
     * @return the Ingredient identifier
     */
    public final int getIngredient_id() {
        return ingredient_id;
    }

    /**
     * It returns the Diet an Ingredient belongs to
     * @return the Diet an Ingredient belongs to
     */
    public final String getDiet() {
        return diet;
    }

    /**
     * It returns the name of the Ingredient
     * @return the name of the Ingredient
     */
    public final String getName() {
        return name;
    }

    /**
     * Creates an {@code Ingredient} from its JSON representation.
     *
     * @param in the input stream containing the JSON document.
     *
     * @return the {@code Ingredient} created from the JSON representation.
     *
     * @throws IOException if something goes wrong while parsing.
     */
    public static Ingredient fromJSON(final InputStream in) throws IOException  {

        // the fields read from JSON
        int jIngredient_id = -1;
        String jName = null;
        String jDiet = null;

        try {
            final JsonParser jp = JSON_FACTORY.createParser(in);

            // while we are not on the start of an element or the element is not
            // a token element, advance to the next element (if any)
            while (jp.getCurrentToken() != JsonToken.FIELD_NAME || !"user".equals(jp.getCurrentName())) {

                // there are no more events
                if (jp.nextToken() == null) {
                    LOGGER.error("No Ingredient object found in the stream.");
                    throw new EOFException("Unable to parse JSON: no Ingredient object found.");
                }
            }

            while (jp.nextToken() != JsonToken.END_OBJECT) {

                if (jp.getCurrentToken() == JsonToken.FIELD_NAME) {

                    switch (jp.getCurrentName()) {
                        /* The ingredient id is managed internally, we never need to know it */
                        /*case "ingredient_id":
                            jp.nextToken();
                            jIngredient_id = jp.getIntValue();
                            break;*/
                        case "name":
                            jp.nextToken();
                            jName = jp.getText();
                            break;
                        case "diet":
                            jp.nextToken();
                            jDiet = jp.getText();
                            break;
                    }
                }
            }
        } catch(IOException e) {
            LOGGER.error("Unable to parse a Ingredient object from JSON.", e);
            throw e;
        }

        return new Ingredient(jIngredient_id, jName, jDiet);
    }

    @Override
    protected void writeJSON(OutputStream out) throws Exception {
		
		final JsonGenerator jg = JSON_FACTORY.createGenerator(out);

		jg.writeStartObject();

        jg.writeFieldName("ingredient");

        jg.writeStartObject();

		jg.writeNumberField("ingredient_id", ingredient_id);

		jg.writeStringField("name", name);

        jg.writeStringField("diet", diet);

        jg.writeEndObject();

		jg.writeEndObject();

		jg.flush();
    }
}
