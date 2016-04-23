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

import cz.deco.DeploymentPlanException;
import cz.deco.javaee.deployment_plan.Insert;
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

public class Replacer {

    private static final Logger LOG = LoggerFactory.getLogger(Replacer.class);

    private Document document;

    private ReplaceInXml replacer = new ReplaceInXml();

    private InsertIntoXml inserter = new InsertIntoXml();

    public void load() {
        //ToDo load document
    }

    public void store() {
        //ToDo store document
    }

    public void apply(Insert insert) {
        String xpath = insert.getXpath();
        XPath xPath = getXPath((Node) insert.getValue());
        NodeList list = (NodeList) evaluate(xpath, xPath);
        int length = list.getLength();
        if (length == 0) {
            LOG.warn("insert XPATH '{}'does not match any node ", xpath);
        }
        for (int i = 0; i < length; i++) {
            Node item = list.item(i);
            if (insert.getValue() != null) {
                inserter.insertXml(document, item, insert.getType(), (Node) insert.getValue());
            }
        }
    }

    protected Object evaluate(String xpath, XPath xPath) {
        try {
            return xPath.evaluate(xpath, document, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new DeploymentPlanException("Xpath error " + e.getLocalizedMessage(), e);
        }
    }

    protected XPath getXPath(final Node node) {
        XPath xPath = XMLFactory.newInstance().getXPathFactory().newXPath();
        xPath.setNamespaceContext(new DecoContextNamespace(node));
        return xPath;
    }

    public void apply(Replace replace) {
        String xpath = replace.getXpath();
        XPath xPath = getXPath((Node) replace.getValue());
        NodeList list = (NodeList) evaluate(xpath, xPath);
        int length = list.getLength();
        if (length == 0) {
            LOG.warn("insert XPATH '{}'does not match any node ", xpath);
        }
        for (int i = 0; i < length; i++) {
            Node item = list.item(i);
            if (replace.getValue() != null) {
                replacer.replaceXml(document, item, replace.getType(), (Node) replace.getValue());
            }
        }
    }

    protected void setDocument(Document document) {
        this.document = document;
    }

    protected void setInserter(InsertIntoXml inserter) {
        this.inserter = inserter;
    }

    protected void setReplacer(ReplaceInXml replacer) {
        this.replacer = replacer;
    }

}
