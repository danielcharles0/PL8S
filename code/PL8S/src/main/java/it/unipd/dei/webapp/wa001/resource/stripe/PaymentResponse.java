package it.unipd.dei.webapp.wa001.resource.stripe;

import it.unipd.dei.webapp.wa001.resource.AbstractResource;

import io.jsonwebtoken.io.IOException;

import com.fasterxml.jackson.core.*;
import java.io.*;

/**
 * Represents the data about a payment response
 */
public class PaymentResponse extends AbstractResource {
    
	/**
     * The Stripe payment clientSecret
     */
    private final String clientSecret;

    /**
     * It creates a Payment response
     * @param clientSecret The Stripe payment client secret
     */
    public PaymentResponse(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    /**
     * It returns the Stripe payment client secret
     * @return the Stripe payment client secret
     */
    public final String getClientSecret() {
        return clientSecret;
    }

    /**
     * It writes the Stripe payment response in the output stream
     *
     * @param out output stream in which to write
     */
    @Override
    protected final void writeJSON(final OutputStream out) throws java.io.IOException {

        final JsonGenerator jg = JSON_FACTORY.createGenerator(out);

		jg.writeStartObject();

        jg.writeStringField("clientSecret", clientSecret);

        jg.writeEndObject();

        jg.flush();
    }
}
