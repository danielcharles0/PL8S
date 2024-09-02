/*
 * Copyright 2018 University of Padua, Italy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.unipd.dei.webapp.wa001.servlet;

import it.unipd.dei.webapp.wa001.resource.dbentities.Dish;
import it.unipd.dei.webapp.wa001.resource.dbentities.Order;
import it.unipd.dei.webapp.wa001.resource.dbentities.Restaurant;
import it.unipd.dei.webapp.wa001.resource.dbentities.User;
import it.unipd.dei.webapp.wa001.resource.fields.CuisineType;
import it.unipd.dei.webapp.wa001.resource.logging.*;
import it.unipd.dei.webapp.wa001.resource.Message;
import it.unipd.dei.webapp.wa001.resource.ErrorCodes;
import it.unipd.dei.webapp.wa001.rest.cuisine.ListCuisineTypesRR;
import it.unipd.dei.webapp.wa001.rest.dish.CreateDishRR;
import it.unipd.dei.webapp.wa001.rest.dish.UpdateDishRR;
import it.unipd.dei.webapp.wa001.rest.dish.DeleteDishRR;
import it.unipd.dei.webapp.wa001.rest.dish.ListOrdersRR;
import it.unipd.dei.webapp.wa001.rest.dish.SearchDishesByRestaurantByNameByDietRR;
import it.unipd.dei.webapp.wa001.rest.order.*;
import it.unipd.dei.webapp.wa001.rest.restaurant.*;
import it.unipd.dei.webapp.wa001.rest.restaurant.CreateRestaurantRR;
import it.unipd.dei.webapp.wa001.rest.user.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;

	/**
	 * Dispatches the request to the proper REST resource.
	 */
	public final class RestDispatcherServlet extends AbstractDatabaseServlet {

	/**
	 * The JSON UTF-8 MIME media type
	 */
	private static final String JSON_UTF_8_MEDIA_TYPE = "application/json; charset=utf-8";

	@Override
	protected void service(final HttpServletRequest req, final HttpServletResponse res) throws IOException {

		LogContext.setIPAddress(req.getRemoteAddr());

		final OutputStream out = res.getOutputStream();
		try {
			String [] pathTokens = getPathTokens(req);
			res.setContentType(JSON_UTF_8_MEDIA_TYPE);
			// if the requested resource is a Restaurant, delegate its processing and return
			if (processRestaurant(req, pathTokens, res)) {
				return;
			}
			// if the requested resource is a User, delegate its processing and return
			if (processUser(req, pathTokens, res)) {
				return;
			}
			// if the requested resource is a Dish, delegate its processing and return
			if (processDish(req, pathTokens, res)) {
				return;
			}
			// if the requested resource is an Order, delegate its processing and return
			if (processOrder(req, pathTokens, res)) {
				return;
			}
			// if the requested resource is a Cuisine type
			if (processCuisineTypes(req, pathTokens, res)) {
				return;
			}

			// if none of the above process methods succeeds, it means an unknown resource has been requested
			LOGGER.warn("Unknown resource requested: %s.", req.getRequestURI());

			final Message m = new Message("Unknown resource requested.", ErrorCodes.UNKNOWN_RESOURCE,
					String.format("Requested resource is %s.", req.getRequestURI()));
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			res.setContentType(JSON_UTF_8_MEDIA_TYPE);
			m.toJSON(out);
		} catch (Throwable t) {
			LOGGER.error("Unexpected error while processing the REST resource.", t);

			final Message m = new Message("Unexpected error.", ErrorCodes.UNEXPECTED_ERROR , t.getMessage());
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			m.toJSON(out);
		} finally {

			// ensure to always flush and close the output stream
			if (out != null) {
				out.flush();
				out.close();
			}

			LogContext.removeIPAddress();
		}
	}

	/**
	 * Checks whether the request is for a {@link CuisineType} resource and, in case, processes it.
	 *
	 * @param req the HTTP request.
	 * @param res the HTTP response.
	 *
	 * @return {@code true} if the request was for a {@code CuisineType}; {@code false} otherwise.
	 *
	 * @throws Exception if any error occurs.
	 */
	private boolean processCuisineTypes(final HttpServletRequest req, final String[] pathTokens, final HttpServletResponse res) throws Exception {
		final String method = req.getMethod();

		if (!pathTokens[0].equals("cuisine")){
			return false;
		}
		
		if(pathTokens.length==2){
//			cuisine/types
			if(pathTokens[1].equals("types")){
				switch (method) {
					case "GET":
						new ListCuisineTypesRR(req, res, getConnection()).serve();
						break;
					default:
						raiseUnsupportedOperation(req,method,res);
						break;
				}
			}else{
				raiseUnrecognizedUriPattern(req,res);
			}
		}else{
			raiseUnrecognizedUriPattern(req,res);
		}
		
		return true;
	}

	/**
	 * Checks whether the request if for a {@link Order} resource and, in case, processes it.
	 *
	 * @param req the HTTP request.
	 * @param res the HTTP response.
	 *
	 * @return {@code true} if the request was for a {@code Order}; {@code false} otherwise.
	 *
	 * @throws Exception if any error occurs.
	 */
	private boolean processOrder(final HttpServletRequest req, final String[] pathTokens, final HttpServletResponse res) throws Exception{
		final String method = req.getMethod();
//		if the first token is not "order" than it is not an Order Resource
		if (!pathTokens[0].equals("order")){
			return false;
		}
		if (pathTokens.length==1){
			switch (method) {
				case "GET":
					new ListDishesFromOrderRR(req, res, getConnection()).serve();
					break;
				case "PUT":
					new CreatePaymentRR(req, res, getConnection()).serve();
					break;
				default:
					raiseUnsupportedOperation(req,method,res);
					break;
			}
		} else if(pathTokens.length==2){
//			order/dishes
			if(pathTokens[1].equals("dishes")){
				switch (method) {
					case "DELETE":
						new DeleteAllDishesFromOrderRR(req, res, getConnection()).serve();
						break;
					case "GET":
						new ListDishesFromOrderRR(req, res, getConnection()).serve();
						break;
					default:
						raiseUnsupportedOperation(req,method,res);
						break;
				}
//			order/previous
			}else if (pathTokens[1].equals("previous")){
				switch (method) {
					case "GET":
						new ListPreviousOrdersRR(req, res, getConnection()).serve();
						break;
					default:
						raiseUnsupportedOperation(req,method,res);
						break;
				}
			}else if (pathTokens[1].equals("payment")){
				switch (method) {
					case "POST":
						/* Server to server communication to confirm the order after the payment */
						new CompleteOrderRR(req, res, getConnection()).serve();
						break;
					default:
						raiseUnsupportedOperation(req,method,res);
						break;
				}
			}else{
				raiseUnrecognizedUriPattern(req,res);
			}
//		order/dishes/{dish_id}
		}else if(pathTokens.length==3){
			if(pathTokens[1].equals("dishes")){
				switch (method) {
					case "DELETE":
						new DeleteDishFromOrderRR(req, res, getConnection()).serve();
						break;
					case "POST":
						new AddDishToOrderRR(req, res, getConnection()).serve();
						break;
					case "PUT":
						new UpdateDishQuantityRR(req, res, getConnection()).serve();
						break;
					default:
						raiseUnsupportedOperation(req,method,res);
						break;
				}
			}else{
				raiseUnrecognizedUriPattern(req,res);
			}
//		order/previous-orders/{order_id}/dishes
		}else {
			if (pathTokens[1].equals("previous-orders") && pathTokens[3].equals("dishes")){
				switch (method) {
					case "GET":
//						new ListDishesFromOrderRR(req, res, getConnection()).serve();
						break;
					default:
						raiseUnsupportedOperation(req,method,res);
						break;
				}
			}else{
				raiseUnrecognizedUriPattern(req,res);
			}
		}
		return true;
	}

	/**
	 * Checks whether the request is for a {@link Dish} resource and, in case, processes it.
	 *
	 * @param req the HTTP request.
	 * @param res the HTTP response.
	 *
	 * @return {@code true} if the request was for a {@code Dish}; {@code false} otherwise.
	 *
	 * @throws Exception if any error occurs.
	 */
	private boolean processDish(final HttpServletRequest req, final String[] pathTokens, final HttpServletResponse res) throws Exception {
		final String method = req.getMethod();
//		if the first token is not "dish" than it is not a Dish Resource
		if (!pathTokens[0].equals("dish") && !pathTokens[0].equals("dishes")){
			return false;
		}
//		/dish
		if (pathTokens.length==1){
			if (pathTokens[0].equals("dish")) {
				switch (method) {
					case "POST":
						new CreateDishRR(req, res, getConnection()).serve();
						break;
					default:
						raiseUnsupportedOperation(req,method,res);
						break;
				}
//			/dishes
			}else {
				switch (method) {
					case "GET":
						new SearchDishesByRestaurantByNameByDietRR(req, res, getConnection()).serve();
						break;
					default:
						raiseUnsupportedOperation(req,method,res);
						break;
				}

			}

//		dish/{dish_id}
		}else if (pathTokens.length==2 && pathTokens[0].equals("dish")){
			switch (method) {
				case "PUT":
					new UpdateDishRR(req, res, getConnection()).serve();
					break;
				case "DELETE":
					new DeleteDishRR(req, res, getConnection()).serve();
					break;
				default:
					raiseUnsupportedOperation(req,method,res);
					break;
			}
//		dishes/name/{name}/restaurant_id/{restaurant_id}/diet/{diet}
//		or a combination of it
		} else {
			
			if (method.equals("GET")){
				if(pathTokens.length==3 && pathTokens[0].equals("dish") && pathTokens[2].equals("orders")){
					new ListOrdersRR(req, res, getConnection()).serve();
				}else if (pathTokens[0].equals("dishes") && (pathTokens[1].equals("name") || pathTokens[1].equals("restaurant_id") || pathTokens[1].equals("diet"))) {
					new SearchDishesByRestaurantByNameByDietRR(req, res, getConnection()).serve();
				}else{
					raiseUnrecognizedUriPattern(req,res);
				}
			}else{
				raiseUnsupportedOperation(req,method,res);
			}
		}
		return true;
	}

	/**
	 * Checks whether the request is for a {@link User} resource and, in case, processes it.
	 *
	 * @param req the HTTP request.
	 * @param res the HTTP response.
	 *
	 * @return {@code true} if the request was for a {@code User}; {@code false} otherwise.
	 *
	 * @throws Exception if any error occurs.
	 */
	private boolean processUser(final HttpServletRequest req, final String[] pathTokens, final HttpServletResponse res) throws Exception{
		final String method = req.getMethod();
//		if the first token is not "user" than it is not a User Resource
		if (!pathTokens[0].equals("user")){
			return false;
		}
//		/user
		if (pathTokens.length==1){
			switch (method) {
				case "PUT":
					new UpdateUserRR(req, res, getConnection()).serve();
					break;
				default:
					raiseUnsupportedOperation(req,method,res);
					break;
			}
		} else if(pathTokens.length==2){
            switch (pathTokens[1]) {
//				user/register
                case "register" -> {
                    switch (method) {
                        case "POST":
                            new RegisterRR(req, res, getConnection()).serve();
                            break;
                        default:
                            raiseUnsupportedOperation(req, method, res);
                            break;
                    }
				}
//				user/create
				case "create" -> {
                    switch (method) {
                        case "POST":
                            new RegisterRR(req, res, getConnection()).serve();
                            break;
                        default:
                            raiseUnsupportedOperation(req, method, res);
                            break;
                    }
				}
//				user/login
                case "login" -> {
                    switch (method) {
                        case "GET":
                            new LoginRR(req, res, getConnection()).serve();
                            break;
                        default:
                            raiseUnsupportedOperation(req, method, res);
                            break;
                    }
				}
//				user/logout
                case "logout" -> {
                    switch (method) {
                        case "GET":
                            new LogoutRR(req, res, getConnection()).serve();
                            break;
                        default:
                            raiseUnsupportedOperation(req, method, res);
                            break;
                    }
				}
//				user/cards
                case "cards" -> {
                    switch (method) {
                        case "GET":
                            new UserCardsRR(req, res, getConnection()).serve();
                            break;
                        default:
                            raiseUnsupportedOperation(req, method, res);
                            break;
                    }
                }
//				user/list
				case "list" -> {
					switch (method) {
						case "GET":
							new ListUsersRR(req, res, getConnection()).serve();
							break;
						default:
							raiseUnsupportedOperation(req,method,res);
							break;
					}
				}
                default -> raiseUnrecognizedUriPattern(req, res);
            }
		} else if(pathTokens.length == 3){
//			user/email/{user_email}
			if (pathTokens[1].equals("email")) {
				switch (method) {
					case "GET":
						new SearchUserByEmailRR(req, res, getConnection()).serve();
						break;
					default:
						raiseUnsupportedOperation(req, method, res);
						break;
				}
			} else if (pathTokens[1].equals("delete")) {
//				user/delete/{user_id}
				switch (method) {
					case "DELETE":
						new DeleteUserRR(req, res, getConnection()).serve();
						break;
					default:
						raiseUnsupportedOperation(req,method,res);
						break;
				}
			}

		}else{
			raiseUnrecognizedUriPattern(req,res);
		}
		return true;
	}

	/**
	 * Checks whether the request is for an {@link Restaurant} resource and, in case, processes it.
	 *
	 * @param req the HTTP request.
	 * @param res the HTTP response.
	 *
	 * @return {@code true} if the request was for an {@code Restaurant}; {@code false} otherwise.
	 *
	 * @throws Exception if any error occurs.
	 */
	private boolean processRestaurant(final HttpServletRequest req, final String[] pathTokens, final HttpServletResponse res) throws Exception {
		final String method = req.getMethod();
//		if the first token is not "restaurant" than it is not a Restaurant Resource
		if (!pathTokens[0].equals("restaurants") && !pathTokens[0].equals("restaurant")){
			return false;
		}
//		/restaurants
		if (pathTokens.length==1){
			if(pathTokens[0].equals("restaurants")) {
				switch (method) {
					case "GET":
						new SearchRestaurantByNameByCuisineRR(req, res, getConnection()).serve();
						break;
					default:
						raiseUnsupportedOperation(req,method,res);
						break;
				}
			}else {
				raiseUnrecognizedUriPattern(req,res);
			}
		} else if(pathTokens.length==2) {
			if(pathTokens[0].equals("restaurant")) {
//				restaurant/cuisine-types
				if (pathTokens[1].equals("cuisine-types")) {
					switch (method) {
						case "GET":
							new ListCuisineTypesRR(req, res, getConnection()).serve();
							break;
						default:
							raiseUnsupportedOperation(req, method, res);
							break;
					}
//				restaurant/create
				} else if (pathTokens[1].equals("create")) {
					switch (method) {
						case "POST":
							new CreateRestaurantRR(req, res, getConnection()).serve();
							break;
						default:
							raiseUnsupportedOperation(req, method, res);
							break;
					}
//				restaurant/{restaurant_id}
				} else {
					switch (method) {
						case "GET":
							new SearchRestaurantByNameByCuisineRR(req, res, getConnection()).serve();
							break;
						default:
							raiseUnsupportedOperation(req, method, res);
							break;
					}
				}
			} else {
//				restaurants/manager
				if (pathTokens[1].equals("manager")) {
					switch (method) {
						case "GET":
							new ListRestaurantManagerRR(req, res, getConnection()).serve();
							break;
						default:
							raiseUnsupportedOperation(req, method, res);
							break;
					}
                }
			}
		} else if (pathTokens.length == 3 && pathTokens[0].equals("restaurant")){
//			/restaurant/update/{restaurant_id}
			if(pathTokens[1].equals("update")) {
				switch (method) {
					case "PUT":
						new UpdateRestaurantRR(req, res, getConnection()).serve();
						break;
					default:
						raiseUnsupportedOperation(req,method,res);
						break;
				}
//			/restaurant/delete/{restaurant_id}
			}else if(pathTokens[1].equals("delete")) {
				switch (method) {
					case "DELETE":
						new DeleteRestaurantRR(req, res, getConnection()).serve();
						break;
					default:
						raiseUnsupportedOperation(req, method, res);
						break;
				}
			}
//		restaurants/name/{name} OR
//		restaurants/name/{name}/cuisine_type/{cuisine_type}	OR
//		restaurants/cuisine_type/{cuisine_type}	etc.
		} else if (pathTokens.length>=3 && pathTokens[0].equals("restaurants")){
			if (pathTokens[1].equals("name") || pathTokens[1].equals("cuisine_type" )){
				switch (method) {
					case "GET":
						new SearchRestaurantByNameByCuisineRR(req, res, getConnection()).serve();
						break;
					default:
						raiseUnsupportedOperation(req,method,res);
						break;
				}
			}else{
				raiseUnrecognizedUriPattern(req,res);
			}
		}else {
			raiseUnrecognizedUriPattern(req,res);
		}
		return true;
	}

	/**
	 * It raises an unsupported operation error and logs it
	 * @param req the HTTP request.
	 * @param method the request method
	 * @param res the HTTP response.
	 * @throws IOException if any error occurs during logging
	 */
	private void raiseUnsupportedOperation(final HttpServletRequest req, final String method, final HttpServletResponse res) throws IOException {
		LOGGER.warn("Unsupported operation for URI %s: %s.", req.getPathInfo(), method);

		Message m = new Message("Unsupported operation for URI " + req.getPathInfo(), ErrorCodes.UNSUPPORTED_OPERATION,
				String.format("Requested operation %s.", method));
		res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		m.toJSON(res.getOutputStream());
	}
	/**
	 * It raises an unrecognized URI pattern error and logs it
	 * @param req the HTTP request.
	 * @param res the HTTP response.
	 * @throws IOException if any error occurs during logging
	 */
	private void raiseUnrecognizedUriPattern(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
		LOGGER.warn("Unrecognized pattern for URI %s", req.getPathInfo());

		Message m = new Message("Unrecognized pattern for URI " + req.getPathInfo(), ErrorCodes.UNRECOGNIZED_URI_PATTERN,
				"Wrong pattern. There are no resources with this URI pattern");
		res.setStatus(HttpServletResponse.SC_NOT_FOUND);
		m.toJSON(res.getOutputStream());

	}

	/**
	 * Returns a string array containing the resources parsed from the URL. If the url ends or starts with a {@code /}
	 * it strips it.
	 * @param req the HTTP request.
	 * @return a string array containing the resources parsed from the URL.
	 */
	private String[] getPathTokens(HttpServletRequest req) {
		String path = req.getPathInfo();
		int i = 0;
		int z = path.length();
		if(path.startsWith("/")){
			i=1;
		}
		if (path.endsWith("/")){
			z -= 1;
		}
		path = path.substring(i, z);
		return path.split("/");
	}
}

