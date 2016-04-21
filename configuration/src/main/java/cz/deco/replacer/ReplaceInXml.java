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


import cz.deco.javaee.deployment_plan.ReplaceOperation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ReplaceInXml {

    protected void replaceXml(Document document, Node where, ReplaceOperation type, Node what) {
        Element xml = (Element) what;
        Node firstChild = xml.getFirstChild();
        Node nodeToInsert = firstChild.cloneNode(true);
        nodeToInsert = document.adoptNode(nodeToInsert);
        switch (type) {
            case CONTENT:
                NodeList childNodes = where.getChildNodes();
                int length = childNodes.getLength();
                for (int i = length - 1; i > -1; i--) {
                    where.removeChild(childNodes.item(i));
                }
                where.appendChild(nodeToInsert);
                break;
            case ENTIRE_NODE:
                where.getParentNode().replaceChild(nodeToInsert, where);
                break;
        }
    }
}
