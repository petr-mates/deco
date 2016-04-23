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


import cz.deco.javaee.deployment_plan.Insert;
import cz.deco.javaee.deployment_plan.InsertOperation;
import cz.deco.javaee.deployment_plan.Replace;
import cz.deco.javaee.deployment_plan.ReplaceOperation;
import cz.deco.xml.XMLFactory;
import cz.deco.xml.XMLTestSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPathConstants;
import java.io.IOException;

public class ReplacerTest {

    private Object detectInnerClass;
    private Replacer replacer = new Replacer();
    private Element elementToInsert;
    private Replace replace = new Replace();
    private Insert insert = new Insert();

    @Before
    public void init() throws IOException, SAXException {
        detectInnerClass = null;
        replacer.setDocument(loadDocument());
        elementToInsert = loadDocument().createElement("elementToInsert");
    }

    public Document loadDocument(String name) throws IOException, SAXException {
        DocumentBuilder builderNs = XMLFactory.newInstance().getBuilderNs();
        return builderNs.parse(this.getClass().getClassLoader().getResourceAsStream(name));
    }

    public Document loadDocument() throws IOException, SAXException {
        return loadDocument("test-document.xml");
    }

    @Test
    public void applyInsertXml() throws Exception {
        insert.setXpath("/test");
        insert.setType(InsertOperation.INSERT_AS_FIRST_CHILD_OF);
        insert.setValue(elementToInsert);
        replacer.setInserter(new InsertIntoXml() {
            @Override
            protected void insertXml(Document document, Node intoNode, InsertOperation type, Node what) {
                detectInnerClass = new Object();
                Assert.assertSame(what, elementToInsert);
            }
        });
        replacer.apply(insert);
        Assert.assertNotNull(detectInnerClass);
    }

    @Test
    public void applyReplaceXml() throws Exception {
        replace.setXpath("/test");
        replace.setType(ReplaceOperation.CONTENT);
        replace.setValue(elementToInsert);
        replacer.setReplacer(new ReplaceInXml() {
            @Override
            protected void replaceXml(Document document, Node intoNode, ReplaceOperation type, Node what) {
                detectInnerClass = new Object();
                Assert.assertSame(what, elementToInsert);
            }
        });
        replacer.apply(replace);
        Assert.assertNotNull(detectInnerClass);
    }

    @Test
    public void applyReplaceXmlInvalidXpath() throws Exception {
        replace.setXpath("/invalidXpath");
        replace.setType(ReplaceOperation.CONTENT);
        replace.setValue(elementToInsert);
        replacer.setReplacer(new ReplaceInXml() {
            @Override
            protected void replaceXml(Document document, Node intoNode, ReplaceOperation type, Node what) {
                detectInnerClass = new Object();
            }
        });
        replacer.apply(replace);
        Assert.assertNull(detectInnerClass);
    }

    @Test
    public void applyReplaceXmlNull() throws Exception {
        replace.setXpath("/invalidXpath");
        replace.setType(ReplaceOperation.CONTENT);
        replace.setValue(null);
        replacer.setReplacer(new ReplaceInXml() {
            @Override
            protected void replaceXml(Document document, Node intoNode, ReplaceOperation type, Node what) {
                detectInnerClass = new Object();
            }
        });
        replacer.apply(replace);
        Assert.assertNull(detectInnerClass);
    }

    @Test
    public void applyReplaceXmlNSAware() throws Exception {
        Element elementToInsert = loadDocument("web-ns.xml")
                .createElementNS("http://java.sun.com/xml/ns/javaee", "elementToInsert");
        Document document = loadDocument("web-ns.xml");
        replacer.setDocument(document);
        insert.setXpath("/w:web-app/w:env-entry");
        insert.setType(InsertOperation.INSERT_AS_FIRST_CHILD_OF);
        insert.setValue(elementToInsert);
        replacer.setInserter(new InsertIntoXml() {
            @Override
            protected void insertXml(Document document, Node intoNode, InsertOperation type, Node what) {
                detectInnerClass = new Object();
            }
        });
        replacer.apply(insert);
        Assert.assertNotNull(detectInnerClass);
    }
}
