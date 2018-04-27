/*
 * Copyright (C) 2016, 2017 Rafal Kasa
 * Copyright (C) 2016 Ismail Simsek
 * Copyright (C) 2017 Thomas Traude
 * Copyright (C) 2017 Daniel Watt
 * Copyright (C) 2017 Nils Petzaell
 *
 * This file is part of SchemaSpy.
 *
 * SchemaSpy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SchemaSpy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with SchemaSpy. If not, see <http://www.gnu.org/licenses/>.
 */
package org.schemaspy.view;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.schemaspy.Config;
import org.schemaspy.model.InvalidConfigurationException;

import java.io.*;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by rkasa on 2016-03-22.
 *
 * @author Rafal Kasa
 * @author Ismail Simsek
 * @author Thomas Traude
 * @author Daniel Watt
 * @author Nils Petzaell
 */
public class MustacheWriter {
    private File outputDir;
    private HashMap<String, Object> scopes;
    private  String rootPath;
    private  String rootPathtoHome;
    private String databaseName;
    private String templateDirectory = Config.getInstance().getTemplateDirectory();

    public MustacheWriter(File outputDir, HashMap<String, Object> scopes, String rootPath, String databaseName) {
        this.outputDir = outputDir;
        this.scopes = scopes;
        this.rootPath = rootPath;
        boolean isOneOfMultipleSchemas = Config.getInstance().isOneOfMultipleSchemas();
        if(isOneOfMultipleSchemas){
            this.rootPathtoHome = "../"+rootPath;
        }else{
            this.rootPathtoHome = rootPath;
        }
        this.databaseName = databaseName;
    }

    public void write(String templatePath, String destination, String scriptFileName) {
        MustacheFactory mf = new DefaultMustacheFactory();
        MustacheFactory contentMf = new DefaultMustacheFactory();
        StringWriter content = new StringWriter();
        StringWriter result = new StringWriter();
        FileUtils fileUtils = new FileUtils();

        HashMap<String, Object> mainScope = new HashMap<String, Object>();
        //URL containerTemplate = getClass().getResource(Paths.get(templateDirectory,"container.html").toString());
       // URL template = getClass().getResource(templatePath);

        try {
            String path = getTemplatePath(templatePath);
            Mustache mustache = mf.compile(getReader(path),"template");
            mustache.execute(result, scopes).flush();

            mainScope.put("databaseName", databaseName);
            mainScope.put("content", result);
            mainScope.put("pageScript",scriptFileName);
            mainScope.put("rootPath", rootPath);
            mainScope.put("rootPathtoHome", rootPathtoHome);

            path = getTemplatePath("container.html");
            Mustache mustacheContent = contentMf.compile(getReader(path), "container");
            mustacheContent.execute(content, mainScope).flush();

            File destinationFile = new File(outputDir, destination);

            fileUtils.writeStringToFile(destinationFile, content.toString(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getTemplatePath(String templatePath) {
        return new File(templateDirectory, templatePath).getPath();
    }

    private static StringReader getReader(String fileName) throws IOException {
    	InputStream cssStream = null;
        if (new File(fileName).exists()){
        	cssStream = new FileInputStream(fileName);
        }else if (new File(System.getProperty("user.dir"), fileName).exists()){
	        	cssStream = new FileInputStream(fileName);
        } else {
            cssStream = MustacheWriter.class.getClassLoader().getResourceAsStream(fileName);
        }

        if (cssStream == null)
            throw new ParseException("Unable to find requested file: " + fileName);
        String inputStream = IOUtils.toString(cssStream, "UTF-8");
        return new StringReader(inputStream);
    }

    /**
     * Indicates an exception in parsing the css
     */
    public static class ParseException extends InvalidConfigurationException {
        private static final long serialVersionUID = 1L;

        /**
         * @param cause root exception that caused the failure
         */
        public ParseException(Exception cause) {
            super(cause);
        }

        /**
         * @param msg textual description of the failure
         */
        public ParseException(String msg) {
            super(msg);
        }
    }

}
