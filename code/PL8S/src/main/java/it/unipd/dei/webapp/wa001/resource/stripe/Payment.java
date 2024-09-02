package it.unipd.dei.webapp.wa001.resource.stripe;

import it.unipd.dei.webapp.wa001.resource.AbstractResource;

import com.stripe.model.PaymentIntent;
import com.fasterxml.jackson.core.*;
import java.io.*;

/**
 * Represents the data about a payment intent
 */
public class Payment extends AbstractResource {
    
	/**
     * The Stripe payment intent
     */
    private final PaymentIntent pi;

    /**
     * It creates a Payment
     * @param pi The Stripe payment intent
     */
    public Payment(PaymentIntent pi) {
        this.pi = pi;
    }

    /**
     * It returns the Stripe payment intent
     * @return the Stripe payment intent
     */
    public final PaymentIntent getPaymentIntent() {
        return pi;
    }

    /**
     * It writes the Stripe payment intent in the output stream
     *
     * @param out output stream in which to write
     */
    @Override
    protected final void writeJSON(final OutputStream out) throws IOException {

        final JsonGenerator jg = JSON_FACTORY.createGenerator(out);

        jg.writeRaw(pi.toJson());

        jg.flush();
    }
}
