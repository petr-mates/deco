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

    protected void replaceXml(Node where, ReplaceOperation type, Node what) {
        Element xml = (Element) what;
        NodeList nodeList = xml.getChildNodes();
        if (nodeList.getLength() > 0) {
            replaceNodes(where, type, nodeList);
        } else {
            replaceNode(where, type, null);
        }
    }

    protected void replaceNodes(Node where, ReplaceOperation type, NodeList nodeList) {
        Document document = where.getOwnerDocument();
        Node firstChild = null;
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

    /**
     * replace node.
     *
     * @param where   node where operation will be done
     * @param type    type of operation.
     * @param newNode node to be inserted.
     */
    protected void replaceNode(Node where, ReplaceOperation type, Node newNode) {
        LOG.debug("node: {} type: {} newNode {}", where, type, newNode);
        switch (type) {
            case CONTENT:
                replaceNodeContent(where, newNode);
                break;
            case ENTIRE_NODE:
                replaceEntireNode(where, newNode);
                break;
            default:
                break;
        }
    }

    /**
     * replace entire node. if newNode is null, entire node will be deleted.
     *
     * @param where
     * @param newNode
     */
    protected void replaceEntireNode(Node where, Node newNode) {
        if (newNode == null) {
            where.getParentNode().removeChild(where);
        } else {
            where.getParentNode().replaceChild(newNode, where);
        }
    }

    /**
     * Replace node context in where node. if newNode is null, node content will be deleted.
     *
     * @param where
     * @param newNode
     */
    protected void replaceNodeContent(Node where, Node newNode) {
        NodeList childNodes = where.getChildNodes();
        int length = childNodes.getLength();
        for (int i = length - 1; i > -1; i--) {
            where.removeChild(childNodes.item(i));
        }
        if (newNode != null) {
            where.appendChild(newNode);
        }
    }
}
