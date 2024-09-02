package it.unipd.dei.webapp.wa001.resource.stripe;

import it.unipd.dei.webapp.wa001.resource.AbstractResource;
import com.stripe.model.PaymentMethod;
import com.fasterxml.jackson.core.*;
import java.io.*;

/**
 * Represents the data about a credit Card
 */
public class Card extends AbstractResource {
    
	/**
     * The Stripe payment method
     */
    private final PaymentMethod pm;

    /**
     * It creates a Card
     * @param pm The Stripe payment method
     */
    public Card(PaymentMethod pm) {
        this.pm = pm;
    }

    /**
     * It returns the Stripe payment method
     * @return the Stripe payment method
     */
    public final PaymentMethod getPaymentMethod() {
        return pm;
    }

    /**
     * It writes the Stripe payment method in the output stream
     *
     * @param out output stream in which to write
     */
    @Override
    protected final void writeJSON(final OutputStream out) throws IOException {

        final JsonGenerator jg = JSON_FACTORY.createGenerator(out);

        jg.writeRaw(pm.toJson());

        jg.flush();
    }
}
