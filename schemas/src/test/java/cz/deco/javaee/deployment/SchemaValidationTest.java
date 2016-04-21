package cz.deco.javaee.deployment;

/*
 * #%L
 * Descriptor Configurator
 * %%
 * Copyright (C) 2016 Mates
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.InputStream;

public class SchemaValidationTest {


    protected void testXml(String sourceXml) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder parser = documentBuilderFactory.newDocumentBuilder();

        ClassLoader classLoader = SchemaValidationTest.class.getClassLoader();
        try (InputStream xmlStream = classLoader.getResourceAsStream(sourceXml);
             InputStream xsdStream = classLoader.getResourceAsStream("deployment-plan.xsd")) {

            Document document = parser.parse(xmlStream);
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            Source schemaFile = new StreamSource(xsdStream);
            Schema schema = factory.newSchema(schemaFile);

            Validator validator = schema.newValidator();
            validator.validate(new DOMSource(document));
        }
    }


    @Test
    public void testSchemaValication() throws Exception {
        testXml("deployment-plan.xml");
        Assert.assertTrue(true);
    }

    @Test(expected = SAXParseException.class)
    public void testSchemaValicationNoNS() throws Exception {
        testXml("deployment-plan-noNS.xml");
        Assert.assertTrue(true);
    }

    @Test(expected = SAXParseException.class)
    public void testSchemaValicationError() throws Exception {
        testXml("deployment-plan-missing.xml");
        Assert.assertTrue(true);
    }

}
