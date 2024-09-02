package it.unipd.dei.webapp.wa001.database.user;

import it.unipd.dei.webapp.wa001.database.AbstractDAO;
import it.unipd.dei.webapp.wa001.resource.dbentities.User;
import it.unipd.dei.webapp.wa001.resource.StripeService;
import com.stripe.model.Customer;
import com.stripe.exception.StripeException;

import java.sql.*;

/**
 * Register a user to the platform.
 */
public class RegisterDAO extends AbstractDAO<User> {

    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "call Festival.insert_user(?,?,?,?,?::Festival.UserRole,?,?)";

    /**
     * The user to be stored into the database
     */
    private final User user;

    /**
     * Creates a new object User that has been inserted in the database.
     *
     * @param con the connection to the database.
     * @param user user to be inserted in the database.
     */
    public RegisterDAO(final Connection con, final User user) {
        super(con);

        if (user == null) {
            LOGGER.error("The user cannot be null.");
            throw new NullPointerException("The user cannot be null.");
        }

        this.user = user;
    }

    @Override
    protected final void doAccess() throws SQLException {

		Customer cus = null;
        CallableStatement cstmt = null;
        User u_ret = null;
		
		try {
			
			cus = StripeService.createCustomer(user);

			LOGGER.info("Stripe customer (%s) successfully created.", cus.getId());
		} catch(StripeException e){
			throw new SQLException("Cannot create stripe customer.", e);
		}

        try {

            cstmt = con.prepareCall(STATEMENT);
            cstmt.setString(1, user.getEmail());
            cstmt.setString(2, user.getPassword());
            cstmt.setString(3, user.getName());
            cstmt.setString(4, user.getSurname());
            cstmt.setString(5, user.getRole());
            cstmt.setString(6, cus.getId());

            cstmt.registerOutParameter(7, Types.INTEGER);

            cstmt.execute();

            u_ret = new User(cstmt.getInt(7), user.getEmail(), user.getPassword(), user.getName(), user.getSurname(), cus.getId(), user.getRole());

			LOGGER.info("User (%d) successfully created.", u_ret.getUser_id());

        } catch(SQLException e){
			LOGGER.info("User already registered.");
			try{
				StripeService.deleteCustomer(cus);
				LOGGER.info("Customer (%s) deleted!", cus.getId());
			}catch(StripeException ex){
				LOGGER.error("Error while deleting the customer.");
				throw new SQLException("Cannot relate user and stripe customer.", ex);
			}
            throw new SQLException("User already registered.", e);
		} finally{

            if (cstmt != null) {
                cstmt.close();
            }

        }
		
		try {
			StripeService.relateCustomer(u_ret);

			LOGGER.info("User and customer (%s) successfully related.", cus.getId());
		} catch(StripeException e){
			throw new SQLException("Cannot relate user and stripe customer.", e);
		}

        this.outputParam = u_ret;
    }
}
