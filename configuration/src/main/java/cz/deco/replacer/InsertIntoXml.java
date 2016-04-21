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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class InsertIntoXml {


    protected void insertXml(Document document, Node intoNode, InsertOperation type, Node what) {
        Element xml = (Element) what;
        Node firstChild = xml.getFirstChild();
        Node nodeToInsert = firstChild.cloneNode(true);
        nodeToInsert = document.adoptNode(nodeToInsert);

        switch (type) {
            case INSERT_AS_FIRST_CHILD_OF:
                Node firstChild1 = intoNode.getFirstChild();
                if (firstChild1 == null) {
                    intoNode.appendChild(nodeToInsert);
                } else {
                    intoNode.insertBefore(nodeToInsert, firstChild1);
                }
                break;
            case INSERT_AS_LAST_CHILD_OF:
                intoNode.appendChild(nodeToInsert);
                break;

            case INSERT_BEFORE:
                intoNode.getParentNode().insertBefore(nodeToInsert, intoNode);
                break;

            case INSERT_AFTER:
                Node nextSibling = intoNode.getNextSibling();
                if (nextSibling != null) {
                    intoNode.getParentNode().insertBefore(nodeToInsert, nextSibling);
                } else {
                    intoNode.getParentNode().appendChild(nodeToInsert);
                }
                break;
        }
    }
}
