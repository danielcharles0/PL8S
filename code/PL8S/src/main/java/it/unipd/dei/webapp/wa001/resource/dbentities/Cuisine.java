package it.unipd.dei.webapp.wa001.resource.dbentities;

/**
 * Represents the data about a Cuisine
 */
public class Cuisine {
    /**
     * The Cuisine identifier
     */
    private final int cuisine_id;
    /**
     * The Cuisine Type
     */
    private final String type ;
    /**
     * The Country this cuisine originates from
     */
    private final String country;
    /**
     * The Restaurant that provides this cuisine
     */
    private final int restaurant;

    /**
     * Creates a new Cuisine
     * @param cuisine_id The Cuisine identifier
     * @param type The Cuisine Type
     * @param country The Country this cuisine originates from
     * @param restaurant The Restaurant that provides this cuisine
     */
    public Cuisine(int cuisine_id, String type, String country, int restaurant) {
        this.cuisine_id = cuisine_id;
        this.type = type;
        this.country = country;
        this.restaurant = restaurant;
    }

    /**
     * Returns the Cuisine identifier
     * @return the Cuisine identifier
     */
    public final int getCuisine_id() {
        return cuisine_id;
    }

    /**
     * Returns the Cuisine type
     * @return the Cuisine type
     */
    public final String getType() {
        return type;
    }

    /**
     * Returns the Country the cuisine originates from
     * @return the Country the cuisine originates from
     */
    public final String getCountry() {
        return country;
    }

    /**
     * Returns the Restaurant that provides this cuisine
     * @return the Restaurant that provides this cuisine
     */
    public int getRestaurant() {
        return restaurant;
    }

}
