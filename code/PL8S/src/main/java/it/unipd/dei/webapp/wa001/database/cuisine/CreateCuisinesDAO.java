package it.unipd.dei.webapp.wa001.database.cuisine;

import it.unipd.dei.webapp.wa001.database.AbstractDAO;
import it.unipd.dei.webapp.wa001.resource.dbentities.Cuisine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Create cuisine in the platform
 */
public class CreateCuisinesDAO extends AbstractDAO<List<Cuisine>> {

    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "CALL Festival.insert_cuisine(?::Festival.Country,?,?,?)";

    /**
     * The cuisine(s) to be created in the database
     */
    private final List<Cuisine> cuisines;

    /**
     * Creates a new object Cuisine that has been inserted in the database.
     *
     * @param con the connection to the database.
     * @param cuisines the cuisines to create.
     */
    public CreateCuisinesDAO(final Connection con, final List<Cuisine> cuisines) {
        super(con);

        if (cuisines == null) {
            LOGGER.error("The cuisine cannot be null.");
            throw new NullPointerException("The cuisine cannot be null.");
        }

        this.cuisines = cuisines;
    }

    @Override
    protected final void doAccess() throws SQLException {

        CallableStatement cstmt = null;
        List<Cuisine> cl_ret = new ArrayList<>();

        try {
            for(Cuisine cuisine : cuisines){
                cstmt = con.prepareCall(STATEMENT);
                cstmt.setString(1, cuisine.getCountry());
                cstmt.setString(2, cuisine.getType());
                cstmt.setInt(3, cuisine.getRestaurant());

                cstmt.registerOutParameter(4, Types.INTEGER);

                cstmt.execute();

                cl_ret.add(new Cuisine(cstmt.getInt(4), cuisine.getCountry(), cuisine.getType(), cuisine.getRestaurant()));
            }

            LOGGER.info("%d Cuisine(s) successfully created.", cl_ret.size());
        } catch(SQLException e){
            throw new SQLException("Cannot create cuisines.", e);
        } finally{

            if (cstmt != null) {
                cstmt.close();
            }

        }

        this.outputParam = cl_ret;
    }
}
