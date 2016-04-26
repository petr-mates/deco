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

import cz.deco.javaee.deployment_plan.DeploymentPlan;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;

/**
 * Factory class for working with XML
 */
public class XMLFactory {

    private JAXBContext context;

    private DocumentBuilder builderNs;

    private XPathFactory xPathFactory;

    private XMLFactory() {
        initJaxbContext();
        initDocumentBuilder();
        xPathFactory = XPathFactory.newInstance();
    }

    /**
     * return XPathFactory
     *
     * @return
     */
    public XPathFactory getXPathFactory() {
        return xPathFactory;
    }

    /**
     * return new instance of the XMLFactory.
     *
     * @return
     */
    public static XMLFactory newInstance() {
        return new XMLFactory();
    }

    /**
     * returns DocumentBuilder with setNamespaceAware On.
     *
     * @return
     */
    public DocumentBuilder getBuilderNs() {
        return builderNs;
    }

    /**
     * returns jaxb content for deployment plan file.
     *
     * @return
     */
    public JAXBContext getContext() {
        return context;
    }

    private void initJaxbContext() {
        try {
            context = JAXBContext.newInstance(DeploymentPlan.class.getPackage().getName());
        } catch (JAXBException e) {
            throw new IllegalStateException("jaxb context new instance error", e);
        }
    }

    private void initDocumentBuilder() {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        try {
            builderNs = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("error creating document builder", e);
        }
    }

    public InputStream getSchemaStream() {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream("deployment-plan.xsd");
    }
}
