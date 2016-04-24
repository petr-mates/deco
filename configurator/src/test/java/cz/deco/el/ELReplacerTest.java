package cz.deco.el;

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

import cz.deco.javaee.deployment_plan.VariableDefinition;
import cz.deco.xml.XMLFactory;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class ELReplacerTest {

    protected NodeList getNodes(String xml) {
        try {
            Document parse = XMLFactory.newInstance().getBuilderNs().parse(new InputSource(new StringReader(xml)));
            return parse.getDocumentElement().getChildNodes();
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<VariableDefinition> getVariables() {
        List<VariableDefinition> objects = new ArrayList<>();
        VariableDefinition variableDefinition = new VariableDefinition();
        variableDefinition.setName("name");
        variableDefinition.setValue("10");
        objects.add(variableDefinition);
        return objects;
    }

    @Test
    public void testELResolver() throws TransformerException, XPathExpressionException {
        NodeList nodes = getNodes("<x><a></a><b>${name}</b></x>");
        ELReplacer elReplacer = new ELReplacer(getVariables());
        elReplacer.replaceText(nodes);
        String nodeValue = nodes.item(0).getNodeValue();
        Document ownerDocument = nodes.item(0).getOwnerDocument();
        Assert.assertEquals("10", XMLFactory.newInstance().getXPathFactory().newXPath().evaluate("/x/b/text()", ownerDocument, XPathConstants.STRING));
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
}
