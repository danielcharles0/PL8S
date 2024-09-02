package it.unipd.dei.webapp.wa001.database.cuisine;

import it.unipd.dei.webapp.wa001.database.AbstractDAO;
import it.unipd.dei.webapp.wa001.resource.dbentities.Cuisine;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Update cuisine in the platform
 */
public class UpdateCuisinesDAO extends AbstractDAO<List<Cuisine>> {

    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "SELECT * FROM Festival.update_restaurant_cuisines(?,?)";

    /**
     * Id of the restaurant whose cuisine type is going to be updated
     */
    private final int restaurant_id;

    /**
     * Types of cuisine provided by the restaurant
     */
    private final List<String[]> cuisines;

    /**
     * Creates a new object Cuisine that has been updated in the database.
     * @param con the connection to the database.
     * @param restaurant_id the restaurant identifier
     * @param cuisines the cuisines to create.
     */
    public UpdateCuisinesDAO(final Connection con, final int restaurant_id, final List<String[]> cuisines) {
        super(con);

        if (cuisines == null) {
            LOGGER.error("The cuisine types cannot be null.");
            throw new NullPointerException("The cuisine types cannot be null.");
        }

        this.restaurant_id = restaurant_id;
        this.cuisines = cuisines;

    }

    @Override
    protected final void doAccess() throws SQLException {

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Cuisine> cl_ret = new ArrayList<>();

        try {
            // convert list of cuisine types to array
            Array cs = con.createArrayOf("TEXT", cuisines.toArray());

            pstmt = con.prepareStatement(STATEMENT);
            pstmt.setInt(1, restaurant_id);
            pstmt.setArray(2, cs);

            rs = pstmt.executeQuery();

            while(rs.next())
                cl_ret.add(new Cuisine(rs.getInt("cuisine_id"), rs.getString("type"), rs.getString("country"), rs.getInt("restaurant")));

            LOGGER.info("%d Cuisine(s) successfully updated.", cl_ret.size());
        } catch(SQLException e){
            throw new SQLException("Cannot update cuisines.", e);
        } finally{

            if (pstmt != null) {
                pstmt.close();
            }

        }

        this.outputParam = cl_ret;
    }
}
