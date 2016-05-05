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

import cz.deco.core.DecoException;
import cz.deco.javaee.deployment_plan.DescriptorOverride;
import cz.deco.javaee.deployment_plan.Insert;
import cz.deco.javaee.deployment_plan.ModuleDescriptor;
import cz.deco.javaee.deployment_plan.Replace;
import cz.deco.xml.DecoContextNamespace;
import cz.deco.xml.XMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.util.List;

public class Replacer {

    private static final Logger LOG = LoggerFactory.getLogger(Replacer.class);

    private ReplaceInXml replaceInXml = new ReplaceInXml();

    private InsertIntoXml insertIntoXml = new InsertIntoXml();

    public void apply(Document document, DescriptorOverride descriptor) {
        for (ModuleDescriptor moduleDescriptor : descriptor.getModuleDescriptor()) {
            List<Object> insertOrReplace = moduleDescriptor.getInsertOrReplace();
            for (Object object : insertOrReplace) {
                if (object instanceof Insert) {
                    apply(document, (Insert) object);
                }
                if (object instanceof Replace) {
                    apply(document, (Replace) object);
                }
            }
        }
    }

    public void apply(Document document, Insert insert) {
        String xpathExpression = insert.getXpath();
        XPath xPath = getXPath((Node) insert.getValue());
        NodeList list = (NodeList) evaluate(xpathExpression, xPath, document);
        int length = list.getLength();
        if (length == 0) {
            LOG.warn("insert XPATH '{}'does not match any node ", xpathExpression);
        }
        for (int i = 0; i < length; i++) {
            Node item = list.item(i);
            if (insert.getValue() != null) {
                insertIntoXml.insertXml(item, insert.getType(), (Node) insert.getValue());
            }
        }
    }

    public void apply(Document document, Replace replace) {
        String xpath = replace.getXpath();
        XPath xPath = getXPath((Node) replace.getValue());
        NodeList list = (NodeList) evaluate(xpath, xPath, document);
        int length = list.getLength();
        if (length == 0) {
            LOG.warn("insert XPATH '{}'does not match any node ", xpath);
        }
        for (int i = 0; i < length; i++) {
            Node item = list.item(i);
            if (replace.getValue() != null) {
                replaceInXml.replaceXml(item, replace.getType(), (Node) replace.getValue());
            }
        }
    }

    protected Object evaluate(String xpathExpression, XPath xPath, Document document) {
        try {
            return xPath.evaluate(xpathExpression, document, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new DecoException("Xpath error " + e.getLocalizedMessage(), e);
        }
    }

    protected XPath getXPath(final Node node) {
        XPath xPath = XMLFactory.newInstance().getXPathFactory().newXPath();
        xPath.setNamespaceContext(new DecoContextNamespace(node));
        return xPath;
    }
}
