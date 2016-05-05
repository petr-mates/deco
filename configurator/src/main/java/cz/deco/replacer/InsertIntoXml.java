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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class should handle all insert operation on target xml file.
 * Insert type are.<br>
 * </uL>INSERT_AFTER selected node
 * <uL>INSERT_BEFORE selected node
 * <uL>INSERT_AS_FISRT_CHILD of the selected node
 * <uL>INSERT_AS_LAST_CHILD of the selected node.
 */
public class InsertIntoXml {
    private static final Logger LOG = LoggerFactory.getLogger(InsertIntoXml.class);

    protected void insertXml(Node intoNode, InsertOperation type, Node what) {
        Element xml = (Element) what;
        NodeList nodeList = xml.getChildNodes();
        Document document = intoNode.getOwnerDocument();
        Node firstChild = null;
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (i == 0) {
                firstChild = nodeList.item(i);
                LOG.debug("node to replace {}", firstChild);
                Node nodeToInsert = firstChild.cloneNode(true);
                nodeToInsert = document.adoptNode(nodeToInsert);
                insertIntoNode(intoNode, type, nodeToInsert);
                firstChild = nodeToInsert;
            } else {
                Node nextChild = nodeList.item(i);
                LOG.debug("node to replace {}", nextChild);
                Node nodeToInsert = nextChild.cloneNode(true);
                nodeToInsert = document.adoptNode(nodeToInsert);
                insertIntoNode(firstChild, InsertOperation.INSERT_AFTER, nodeToInsert);
            }
        }
    }

    protected void insertIntoNode(Node intoNode, InsertOperation type, Node nodeToInsert) {
        LOG.debug("node: {} type: {} nodeToInsert: {}", intoNode, type, nodeToInsert);
        switch (type) {
            case INSERT_AS_FIRST_CHILD_OF:
                insertAsFirstChild(intoNode, nodeToInsert);
                break;
            case INSERT_AS_LAST_CHILD_OF:
                intoNode.appendChild(nodeToInsert);
                break;

            case INSERT_BEFORE:
                intoNode.getParentNode().insertBefore(nodeToInsert, intoNode);
                break;

            case INSERT_AFTER:
                insertAfter(intoNode, nodeToInsert);
                break;
            default:
                break;
        }
    }

    protected void insertAfter(Node intoNode, Node nodeToInsert) {
        Node nextSibling = intoNode.getNextSibling();
        if (nextSibling != null) {
            intoNode.getParentNode().insertBefore(nodeToInsert, nextSibling);
        } else {
            intoNode.getParentNode().appendChild(nodeToInsert);
        }
    }

    protected void insertAsFirstChild(Node intoNode, Node nodeToInsert) {
        Node firstChild1 = intoNode.getFirstChild();
        if (firstChild1 == null) {
            intoNode.appendChild(nodeToInsert);
        } else {
            intoNode.insertBefore(nodeToInsert, firstChild1);
        }
    }
}
