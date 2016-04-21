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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;

public class ReplacerTest {

    private Object detectInnerClass;

    @Before
    public void init(){
        detectInnerClass = null;
    }

    public Document loadDocument() throws IOException, SAXException {
        DocumentBuilder builderNs = XMLFactory.newInstance().getBuilderNs();
        return builderNs.parse(this.getClass().getClassLoader().getResourceAsStream("test-document.xml"));
    }

    public void printXml(Document doc) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(doc);
        transformer.transform(source, result);
        String xmlString = result.getWriter().toString();
        System.out.println(xmlString);
    }

    @Test
    public void applyInsertText() throws Exception {
        Replacer replacer = new Replacer();
        Insert insert = new Insert();
        insert.setXpath("/test");
        insert.setType(InsertOperation.INSERT_AS_FIRST_CHILD_OF);
        insert.setText("text node");
        replacer.setInserter(new InsertIntoXml(){
            @Override
            protected void insertXml(Document document, Node intoNode, InsertOperation type, Node what) {
                detectInnerClass = new Object();
                Assert.assertNotNull(what);
                Assert.assertEquals("fake", what.getNodeName());
            }
        });
        replacer.setDocument(loadDocument());
        replacer.apply(insert);
        Assert.assertNotNull(detectInnerClass);
    }

    @Test
    public void applyInsertXml() throws Exception {
        Replacer replacer = new Replacer();
        Insert insert = new Insert();
        insert.setXpath("/test");
        insert.setType(InsertOperation.INSERT_AS_FIRST_CHILD_OF);
        final Element elementToInsert = loadDocument().createElement("elementToInsert");
        insert.setXml(elementToInsert);
        replacer.setInserter(new InsertIntoXml(){
            @Override
            protected void insertXml(Document document, Node intoNode, InsertOperation type, Node what) {
                detectInnerClass = new Object();
                Assert.assertSame(what, elementToInsert);
            }
        });
        replacer.setDocument(loadDocument());
        replacer.apply(insert);
        Assert.assertNotNull(detectInnerClass);
    }

    @Test
    public void applyReplaceXml() throws Exception {
        Replacer replacer = new Replacer();
        Replace replace = new Replace();
        replace.setXpath("/test");
        replace.setType(ReplaceOperation.CONTENT);
        final Element elementToInsert = loadDocument().createElement("elementToInsert");
        replace.setXml(elementToInsert);
        replacer.setReplacer(new ReplaceInXml(){
            @Override
            protected void replaceXml(Document document, Node intoNode, ReplaceOperation type, Node what) {
                detectInnerClass = new Object();
                Assert.assertSame(what, elementToInsert);
            }
        });
        replacer.setDocument(loadDocument());
        replacer.apply(replace);
        Assert.assertNotNull(detectInnerClass);
    }

    @Test
    public void applyReplaceText() throws Exception {
        Replacer replacer = new Replacer();
        Replace replace = new Replace();
        replace.setXpath("/test");
        replace.setType(ReplaceOperation.CONTENT);
        replace.setText("text to replace");
        replacer.setReplacer(new ReplaceInXml(){
            @Override
            protected void replaceXml(Document document, Node intoNode, ReplaceOperation type, Node what) {
                detectInnerClass = new Object();
                Assert.assertNotNull(what);
                Assert.assertEquals("fake", what.getNodeName());
            }
        });
        replacer.setDocument(loadDocument());
        replacer.apply(replace);
        Assert.assertNotNull(detectInnerClass);
    }

}