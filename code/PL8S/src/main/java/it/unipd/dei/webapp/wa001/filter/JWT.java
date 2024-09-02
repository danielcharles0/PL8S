package it.unipd.dei.webapp.wa001.filter;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import it.unipd.dei.webapp.wa001.resource.dbentities.User;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

/**
 * JWT manager
 */
public class JWT {

    /**
     * Secret key used to sign JWTs
     */
    private static final SecretKey key = Keys.hmacShaKeyFor(
            "YXNqZHVoYWtudmFoaWJzcm9rYW93aXJoZmE2MzIwNDU5dW9zamJ2dDM4OTI0cjA5M3Vy".getBytes(StandardCharsets.UTF_8)
    );

    /**
     * The JSON factory to be used for creating JSON generator.
     */
    protected static final JsonFactory JSON_FACTORY;

    static {
        // set up the JSON factory
        JSON_FACTORY = new JsonFactory();
        JSON_FACTORY.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
        JSON_FACTORY.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
    }


    /**
     * Creates a JWT for the user passed in input
     *
     * @param user user to get data to insert in jwt
     * @return jwt
     */
    public static String encode(User user){

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR_OF_DAY, 8);      // adds 8 hours to current date
        Date exp_date = cal.getTime();          // current date + 8 hours = expiration date

        String jwt = Jwts.builder()
                .header()
                    .add("app", "PL8S")
                    .and()
                .claim("uid", user.getUser_id())
                .claim("rol", user.getRole())
                .claim("str", user.getStripe_id())
                .claim("dat", exp_date)
                .signWith(key)
                .compact();

        return jwt;
    }

    /**
     * Decodes a jwt and returns the associated user
     *
     * @param token jwt
     * @return user associated to the token
     * @throws JwtException if some problem happens in the decoding phase
     */
    public static User decode(String token) throws JwtException{

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Date date = new Date();

        // if token is expired
        if(date.after(claims.get("dat", Date.class)))
            return null;

        int user_id = claims.get("uid", Integer.class);
        String stripe_id = claims.get("str", String.class);
        String role = claims.get("rol", String.class);

        return new User(user_id, stripe_id, role);

    }

    /**
     * Writes the generated token into the output stream.
     *
     * @param out output stream
     * @param token jwt
     * @throws IOException if some problems with the stream happen.
     */
    public static void writeJson(final OutputStream out, final String token) throws IOException {

        if(out == null) {
            throw new IOException("The output stream cannot be null.");
        }

        final JsonGenerator jg = JSON_FACTORY.createGenerator(out);

        jg.writeStartObject();

        jg.writeFieldName("jwt");

        jg.writeStartObject();

        jg.writeStringField("access_token", token);

        jg.writeStringField("token_type", "Bearer");

        jg.writeNumberField("expires_in", 28800); // 8 hours in seconds

        jg.writeEndObject();

        jg.writeEndObject();

        jg.flush();
    }
}
