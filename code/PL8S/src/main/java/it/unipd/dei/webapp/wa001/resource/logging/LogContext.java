/*
 * Copyright (c) 2023 University of Padua, Italy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.unipd.dei.webapp.wa001.resource.logging;

import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.Logger;


/**
 * Provides the log context for the application on a per-thread basis.
 *
 */
public final class LogContext {

	/**
	 * The user who is performing an action
	 */
	private static final String USER = "USER";

	/**
	 * The IP address of the user who is performing an action
	 */
	private static final String IP = "IP";

	/**
	 * The action performed by the user
	 */
	private static final String ACTION = "ACTION";

	/**
	 * The resource currently processed
	 */
	private static final String RESOURCE = "RESOURCE";

	/**
	 * Sets the {@code user} currently performing actions.
	 * <p>
	 * If {@code null}, it simply returns.
	 *
	 * @param user the {@code user} currently performing actions.
	 */
	public static void setUser(final String user) {
		if (user != null && !user.isEmpty()) {
			ThreadContext.put(USER, user);
		}
	}

	/**
	 * Removes the {@code User} currently performing actions.
	 */
	public static void removeUser() {
		ThreadContext.remove(USER);
	}

	/**
	 * Sets the {@code IP} addressed of the user currently performing actions.
	 * <p>
	 * If {@code null} or empty, it simply returns.
	 *
	 * @param ip the {@code IP} addressed of the user currently performing actions.
	 */
	public static void setIPAddress(final String ip) {
		if (ip != null && !ip.isEmpty()) {
			ThreadContext.put(IP, ip);
		}
	}

	/**
	 * Removes the {@code IP} addressed of the user currently performing actions.
	 */
	public static void removeIPAddress() {
		ThreadContext.remove(IP);
	}


	/**
	 * Sets the {@code action} currently performed.
	 * <p>
	 * If {@code null}, it simply returns.
	 *
	 * @param action the action currently performed.
	 */
	public static void setAction(final String action) {
		if (action != null) {
			ThreadContext.put(ACTION, action);
		}
	}


	/**
	 * Removes the action currently performed.
	 */
	public static void removeAction() {
		ThreadContext.remove(ACTION);
	}

	/**
	 * Sets the {@code resource} currently processed.
	 * <p>
	 * If {@code null}, it simply returns.
	 *
	 * @param resource the resource currently processed.
	 */
	public static void setResource(final String resource) {
		if (resource != null && !resource.isEmpty()) {
			ThreadContext.put(RESOURCE, resource);
		}
	}

	/**
	 * Removes the resource currently processed.
	 */
	public static void removeResource() {
		ThreadContext.remove(RESOURCE);
	}

	/**
	 * Sets all to the LogContext.
	 * @param user the user of the request
	 * @param ip the ip of the request
	 * @param action the action to perform
	 * @param resource the resource to be accessed
	 */
	public static void setAll(final String user, final String ip, final String action, final String resource){
		LogContext.setUser(user);
		LogContext.setIPAddress(ip);
		LogContext.setAction(action);
		LogContext.setResource(resource);
	}

	/**
	 * Removes all from the LogContext.
	 */
	public static void removAll() {
		LogContext.removeUser();
		LogContext.removeIPAddress();
		LogContext.removeAction();
		LogContext.removeResource();
	}

	/**
	 * This class can be neither instantiated nor sub-classed.
	 */
	private LogContext() {
		throw new AssertionError(String.format("No instances of %s allowed.", LogContext.class.getName()));
	}
}
