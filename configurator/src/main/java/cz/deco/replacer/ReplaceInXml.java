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
import cz.deco.javaee.deployment_plan.ReplaceOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class should handle all replace operation on target xml file.
 * Replace type are.<BR>
 * <uL>CONTENT of the selected node
 * <uL>ENTIRE_NODE of the selected node.
 */
public class ReplaceInXml {

    private static final Logger LOG = LoggerFactory.getLogger(ReplaceInXml.class);

    private InsertIntoXml insertIntoXml = new InsertIntoXml();

    protected void replaceXml(Document document, Node where, ReplaceOperation type, Node what) {
        Element xml = (Element) what;
        NodeList nodeList = xml.getChildNodes();
        Node firstChild = null;
        if (nodeList.getLength() > 0) {
            replaceNodes(document, where, type, nodeList, firstChild);
        } else {
            replaceNode(where, type, null);
        }
    }

    private void replaceNodes(Document document, Node where, ReplaceOperation type, NodeList nodeList, Node firstChild) {
        for (int j = 0; j < nodeList.getLength(); j++) {
            if (j == 0) {
                firstChild = nodeList.item(j);
                Node nodeToInsert = firstChild.cloneNode(true);
                nodeToInsert = document.adoptNode(nodeToInsert);
                firstChild = nodeToInsert;
                replaceNode(where, type, nodeToInsert);
            } else {
                Node nodeToInsert = nodeList.item(j).cloneNode(true);
                nodeToInsert = document.adoptNode(nodeToInsert);
                insertIntoXml.insertIntoNode(firstChild, InsertOperation.INSERT_AFTER, nodeToInsert);
            }
        }
    }

    protected void replaceNode(Node where, ReplaceOperation type, Node nodeToInsert) {
        LOG.debug("node: {} type: {} nodeToInsert {}", where, type, nodeToInsert);
        switch (type) {
            case CONTENT:
                replaceNodeContent(where, nodeToInsert);
                break;
            case ENTIRE_NODE:
                replaceEntireNode(where, nodeToInsert);
                break;
            default:
                break;
        }
    }

    protected void replaceEntireNode(Node where, Node nodeToInsert) {
        if (nodeToInsert == null) {
            where.getParentNode().removeChild(where);
        } else {
            where.getParentNode().replaceChild(nodeToInsert, where);
        }
    }

    protected void replaceNodeContent(Node where, Node nodeToInsert) {
        NodeList childNodes = where.getChildNodes();
        int length = childNodes.getLength();
        for (int i = length - 1; i > -1; i--) {
            where.removeChild(childNodes.item(i));
        }
        if (nodeToInsert != null) {
            where.appendChild(nodeToInsert);
        }
    }
}
