package cz.deco.replacer;

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

import cz.deco.javaee.deployment_plan.ReplaceOperation;
import cz.deco.xml.XMLFactory;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPathConstants;
import java.io.IOException;
import java.io.StringReader;

public class ReplaceInXmlTest {

    private Node getNodeToPlace() throws SAXException, IOException {
        return XMLFactory.newInstance().getBuilderNs().parse(new InputSource(new StringReader("<xml><nodeToInsert/></xml>"))).getDocumentElement();
    }

    private Document loadDocument() throws IOException, SAXException {
        DocumentBuilder builderNs = XMLFactory.newInstance().getBuilderNs();
        return builderNs.parse(this.getClass().getClassLoader().getResourceAsStream("test-document.xml"));
    }


    @Test
    public void replaceContent() throws Exception {
        ReplaceInXml replacer = new ReplaceInXml();
        Node parse = getNodeToPlace();
        Document document = loadDocument();
        replacer.replaceXml(document, document.getElementsByTagName("node1").item(0), ReplaceOperation.CONTENT, parse);
        Assert.assertNotNull(XMLFactory.newInstance().getXPathFactory().newXPath().evaluate("/test/node1/nodeToInsert", document, XPathConstants.NODE));
        Assert.assertNull(XMLFactory.newInstance().getXPathFactory().newXPath().evaluate("/test/node1/node2", document, XPathConstants.NODE));
    }

    @Test
    public void replaceEntireNode() throws Exception {
        ReplaceInXml replacer = new ReplaceInXml();
        Node parse = getNodeToPlace();
        Document document = loadDocument();
        replacer.replaceXml(document, document.getElementsByTagName("node1").item(0), ReplaceOperation.ENTIRE_NODE, parse);
        Assert.assertNotNull(XMLFactory.newInstance().getXPathFactory().newXPath().evaluate("/test/nodeToInsert", document, XPathConstants.NODE));
        Assert.assertNull(XMLFactory.newInstance().getXPathFactory().newXPath().evaluate("/test/node1", document, XPathConstants.NODE));
    }

}