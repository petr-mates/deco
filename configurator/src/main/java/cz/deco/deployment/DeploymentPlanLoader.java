package cz.deco.deployment;

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


import cz.deco.DecoException;
import cz.deco.javaee.deployment_plan.DeploymentPlan;
import cz.deco.xml.XMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class to load and validate deployment plan. All validations on deployment plan should be done here.
 */
public class DeploymentPlanLoader {

    private static final Logger LOG = LoggerFactory.getLogger(DeploymentPlanLoader.class);

    /**
     * load and call validation of the loaded deployment plan.
     *
     * @param plan
     * @return
     */
    public DeploymentPlan load(DeploymentPlanFile plan) {
        validatePlan(plan);
        JAXBContext context = XMLFactory.newInstance().getContext();
        try (InputStream input = plan.getInputStream()) {
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (DeploymentPlan) unmarshaller.unmarshal(input);

        } catch (JAXBException | IOException e) {
            throw new DecoException("cannot unmarshall deployment plan", e);
        }
    }

    /**
     * do schema validation.
     *
     * @param plan
     */
    protected void validatePlan(DeploymentPlanFile plan) {

        XMLFactory xmlFactory = XMLFactory.newInstance();
        DocumentBuilder builder = xmlFactory.getBuilderNs();
        Validator validator = null;
        Document document = null;
        LOG.debug("deployment plan source {}", plan.getFile());
        try (InputStream xmlStream = plan.getInputStream();
             InputStream xsdStream = xmlFactory.getSchemaStream()) {
            document = builder.parse(xmlStream);
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            Source schemaFile = new StreamSource(xsdStream);
            Schema schema = factory.newSchema(schemaFile);

            validator = schema.newValidator();
            LOG.trace("xml validation OK");
        } catch (IOException | SAXException e) {
            throw new DecoException("error loading xml", e);
        }
        try {
            validator.validate(new DOMSource(document));
        } catch (IOException | SAXException sex) {
            throw new DecoException("deployment plan validation exception ", sex);
        }
    }
}
