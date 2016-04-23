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


import cz.deco.javaee.deployment_plan.InsertOperation;
import cz.deco.xml.XMLFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.StringReader;

public class InsertIntoXmlTest {

    private Node getNodeToInsert() throws SAXException, IOException {
        return XMLFactory.newInstance().getBuilderNs().parse(new InputSource(
                new StringReader("<xml><nodeToInsert/><nodeToInsert2/></xml>"))).getDocumentElement();
    }

    private Document loadDocument() throws IOException, SAXException {
        DocumentBuilder builderNs = XMLFactory.newInstance().getBuilderNs();
        return builderNs.parse(this.getClass().getClassLoader().getResourceAsStream("test-document.xml"));
    }

    protected Node evalXpath(String xpath) throws XPathExpressionException {
        return (Node) XMLFactory.newInstance().getXPathFactory()
                .newXPath().evaluate(xpath, document, XPathConstants.NODE);
    }

    protected Node getNodeByName(String name) {
        return document.getElementsByTagName(name).item(0);
    }

    private InsertIntoXml inserter = new InsertIntoXml();
    private Node parse;
    private Document document;

    @Before
    public void init() throws Exception {
        parse = getNodeToInsert();
        document = loadDocument();
    }

    @After
    public void destroy() throws XPathExpressionException {
        Assert.assertNotNull(evalXpath("//nodeToInsert/following-sibling::nodeToInsert2"));
        //XMLTestSupport.printXml(document);
    }


    @Test
    public void applyDomAfter() throws Exception {
        inserter.insertXml(document, getNodeByName("node2"), InsertOperation.INSERT_AFTER, parse);
        Assert.assertNotNull(evalXpath("/test/node1/node2/following-sibling::nodeToInsert"));
    }

    @Test
    public void applyDomAsFirstChild() throws Exception {
        inserter.insertXml(document, getNodeByName("node1"), InsertOperation.INSERT_AS_FIRST_CHILD_OF, parse);
        Assert.assertNotNull(evalXpath("/test/node1/nodeToInsert/following-sibling::node2"));
    }

    @Test
    public void applyDomAsLastChild() throws Exception {
        inserter.insertXml(document, getNodeByName("node1"), InsertOperation.INSERT_AS_LAST_CHILD_OF, parse);
        Assert.assertNotNull(evalXpath("/test/node1/node2/following-sibling::nodeToInsert"));
    }

    @Test
    public void applyDomBefore() throws Exception {
        inserter.insertXml(document, getNodeByName("node2"), InsertOperation.INSERT_BEFORE, parse);
        Assert.assertNotNull(evalXpath("/test/node1/nodeToInsert2/following-sibling::node2"));
    }
}
