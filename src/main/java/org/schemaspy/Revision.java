/*
 * This file is a part of the SchemaSpy project (http://schemaspy.org).
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011 John Currier
 *
 * SchemaSpy is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * SchemaSpy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.schemaspy;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author John Currier
 */
public class Revision {
    private static final Logger logger = Logger.getLogger(Revision.class.getName());
    private static String rev = "Unknown";
    private static final String resourceName = "/META-INF/MANIFEST.MF";

    static {
        initialize();
    }

    private static void initialize() {
        try (InputStream in = Revision.class.getResourceAsStream(resourceName)) {
            if (in != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {

                    String line;

                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("Implementation-Build:")) {
                            rev = line.split(" ")[1];
                            break;
                        }
                    }
                }
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE,ex.getMessage(),ex);
        }
    }

    @Override
    public String toString() {
        return rev;
    }
}