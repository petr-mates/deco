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


import cz.deco.javaee.deployment_plan.DescriptorOverride;
import cz.deco.javaee.deployment_plan.Insert;
import cz.deco.javaee.deployment_plan.InsertOperation;
import cz.deco.javaee.deployment_plan.ModuleDescriptor;
import cz.deco.javaee.deployment_plan.Replace;
import cz.deco.javaee.deployment_plan.ReplaceOperation;
import cz.deco.xml.XMLFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import java.io.IOException;

@RunWith(MockitoJUnitRunner.class)
public class ReplacerTest {

    @Mock
    private InsertIntoXml insertIntoXml;
    @Mock
    private ReplaceInXml replaceInXml;
    @InjectMocks
    private Replacer replacer = new Replacer();
    private Element elementToInsert;
    private Replace replace = new Replace();
    private Insert insert = new Insert();
    private Document document;
    private boolean applyReplaceEntered;
    private boolean applyInsertEntered;

    @Before
    public void init() throws IOException, SAXException {
        applyReplaceEntered = false;
        applyInsertEntered = false;
        document = loadDocument();
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
        replacer.apply(document, insert);
        Mockito.verify(insertIntoXml,
                Mockito.times(1)).insertXml(Mockito.same(document), (Node) Mockito.any(),
                Mockito.same(InsertOperation.INSERT_AS_FIRST_CHILD_OF), (Node) Mockito.any());
    }

    @Test
    public void applyReplaceXml() throws Exception {
        replace.setXpath("/test");
        replace.setType(ReplaceOperation.CONTENT);
        replace.setValue(elementToInsert);
        replacer.apply(document, replace);
        Mockito.verify(replaceInXml,
                Mockito.times(1)).replaceXml(Mockito.same(document), (Node) Mockito.any(),
                Mockito.same(ReplaceOperation.CONTENT), (Node) Mockito.any());
    }

    @Test
    public void applyReplaceXmlInvalidXpath() throws Exception {
        replace.setXpath("/invalidXpath");
        replace.setType(ReplaceOperation.CONTENT);
        replace.setValue(elementToInsert);
        replacer.apply(document, replace);
        Mockito.verify(replaceInXml,
                Mockito.times(0)).replaceXml(Mockito.same(document), (Node) Mockito.any(),
                Mockito.same(ReplaceOperation.CONTENT), (Node) Mockito.any());
    }

    @Test
    public void applyReplaceXmlNull() throws Exception {
        replace.setXpath("/invalidXpath");
        replace.setType(ReplaceOperation.CONTENT);
        replace.setValue(null);
        replacer.apply(document, replace);
        Mockito.verify(replaceInXml,
                Mockito.times(0)).replaceXml(Mockito.same(document), (Node) Mockito.any(),
                Mockito.same(ReplaceOperation.CONTENT), (Node) Mockito.any());
    }

    @Test
    public void applyReplaceXmlNSAware() throws Exception {
        Element elementToInsert = loadDocument("web-ns.xml")
                .createElementNS("http://java.sun.com/xml/ns/javaee", "elementToInsert");
        Document localDocument = loadDocument("web-ns.xml");
        insert.setXpath("/w:web-app/w:env-entry");
        insert.setType(InsertOperation.INSERT_AS_FIRST_CHILD_OF);
        insert.setValue(elementToInsert);
        replacer.apply(localDocument, insert);
        Mockito.verify(insertIntoXml,
                Mockito.times(1)).insertXml(Mockito.same(localDocument), (Node) Mockito.any(),
                Mockito.same(InsertOperation.INSERT_AS_FIRST_CHILD_OF), (Node) Mockito.any());
    }

    @Test
    public void testApplyDescriptor() {
        Replacer replacer = new Replacer() {
            @Override
            public void apply(Document document, Insert insert) {
                applyInsertEntered = true;
            }

            @Override
            public void apply(Document document, Replace replace) {
                applyReplaceEntered = true;
            }
        };
        DescriptorOverride descriptor = new DescriptorOverride();
        ModuleDescriptor moduleDescriptor = new ModuleDescriptor();
        moduleDescriptor.getInsertOrReplace().add(new Insert());
        moduleDescriptor.getInsertOrReplace().add(new Replace());
        descriptor.getModuleDescriptor().add(moduleDescriptor);
        replacer.apply(document, descriptor);
        Assert.assertTrue(applyInsertEntered);
        Assert.assertTrue(applyReplaceEntered);
    }
}
