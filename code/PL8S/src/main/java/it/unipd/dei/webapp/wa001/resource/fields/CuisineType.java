package it.unipd.dei.webapp.wa001.resource.fields;

import com.fasterxml.jackson.core.JsonGenerator;
import it.unipd.dei.webapp.wa001.resource.AbstractResource;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Contains the name of a Cuisine type
 */
public class CuisineType extends AbstractResource {
    private final String type ;

    /**
     * Creates a new Cuisine type
     * @param type The Cuisine Type

     */
    public CuisineType(String type) {
        this.type = type;
    }

    /**
     * Returns the cuisine type name
     * @return the cuisine type name
     */
    public final String getType() {
        return type;
    }

    @Override
    protected final void writeJSON(final OutputStream out) throws IOException {

        final JsonGenerator jg = JSON_FACTORY.createGenerator(out);
        jg.writeStartObject();
        jg.writeFieldName("CuisineType");
        jg.writeStartObject();
        jg.writeStringField("type", type);
        jg.writeEndObject();
        jg.writeEndObject();

        jg.flush();
    }

}
