package cz.deco.xml;

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

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Implementation of the NamespaceContext. Class cache all xmlns prefixes and namespaces in the xml document.
 */
public class DecoContextNamespace implements NamespaceContext {
    private Map<String, String> prefixNs = new HashMap<>();

    public DecoContextNamespace(Node node) {
        if (node != null) {
            Element documentElement = node.getOwnerDocument().getDocumentElement();
            findRecurse(documentElement);
            findNamespaces(documentElement.getAttributes());
        }
    }

    private void findRecurse(Node node) {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() == Node.ELEMENT_NODE) {
                findNamespaces(item.getAttributes());
                findRecurse(item);
            }
        }
    }

    private void findNamespaces(NamedNodeMap attributes) {
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(attribute.getNamespaceURI())) {
                if (attribute.getNodeName().equals(XMLConstants.XMLNS_ATTRIBUTE)) {
                    prefixNs.put("", attribute.getNodeValue());
                } else {
                    prefixNs.put(attribute.getLocalName(), attribute.getNodeValue());
                }
            }
        }
    }

    @Override
    public String getNamespaceURI(String prefix) {
        return prefixNs.get(prefix);
    }

    @Override
    public String getPrefix(String namespaceURI) {
        return null;
    }

    @Override
    public Iterator getPrefixes(String namespaceURI) {
        return null;
    }
}
