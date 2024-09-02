/*
 * Copyright 2018-2023 University of Padua, Italy
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

package it.unipd.dei.webapp.wa001.resource;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.*;

/**
 * Represents a list of {@link AbstractResource} objects.
 *
 * @param <T>
 *            the type of the actual class extending {@code AbstractResource}.
 */
public final class ResourceList<T extends Resource> extends AbstractResource {
	
	/**
     * The name of the list of {@code AbstractResource}s.
     */
	private final String name;

    /**
     * The list of {@code AbstractResource}s.
     */
    private final Iterable<T> list;

    /**
     * Creates a list of {@code AbstractResource}s.
     * @param name the name of the list of {@code AbstractResource}s.
     * @param list the list of {@code AbstractResource}s.
     */
    public ResourceList(final String name, final Iterable<T> list) {

        if(list == null) {
            LOGGER.error("Resource list cannot be null.");
            throw new NullPointerException("Resource list cannot be null.");
        }

		this.name = name;
        this.list = list;
    }

	/**
     * Creates a list of {@code AbstractResource}s.
     *
     * @param list the list of {@code AbstractResource}s.
     */
    public ResourceList(final Iterable<T> list) {

        this("resource-list", list);
    }

    @Override
    protected void writeJSON(final OutputStream out) throws IOException {

        final JsonGenerator jg = JSON_FACTORY.createGenerator(out);

        jg.writeStartObject();

        jg.writeFieldName(this.name);

        jg.writeStartArray();

        jg.flush();

        boolean firstElement = true;

        for (final Resource r : list) {

            // very bad work-around to add commas between resources
            if (firstElement) {
                r.toJSON(out);
                jg.flush();

                firstElement = false;
            } else {
                jg.writeRaw(',');
                jg.flush();

                r.toJSON(out);
                jg.flush();
            }
        }

        jg.writeEndArray();

        jg.writeEndObject();

        jg.flush();
    }

}