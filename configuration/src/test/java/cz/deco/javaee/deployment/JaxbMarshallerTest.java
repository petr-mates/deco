package cz.deco.javaee.deployment;

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
import cz.deco.javaee.deployment_plan.Replace;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class JaxbMarshallerTest {

    @Test
    public void unmashall() throws JAXBException, IOException {
        JAXBContext jaxbContext = JAXBContext.newInstance(DeploymentPlan.class.getPackage().getName());
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        ClassLoader classLoader = JaxbMarshallerTest.class.getClassLoader();
        try (InputStream xmlSource = classLoader.getResourceAsStream("deployment-plan.xml")) {
            DeploymentPlan plan = (DeploymentPlan) unmarshaller.unmarshal(xmlSource);

            List<Object> insertOrReplace = plan.getModuleOverride().get(1).getModuleDescriptor().get(0).getInsertOrReplace();
            Element xml = (Element) ((Replace) insertOrReplace.get(1)).getValue();
            Node item = xml.getChildNodes().item(0);
            Assert.assertEquals("env entry test", "env-entry", item.getLocalName());
            Assert.assertEquals("env entry test namespace", "http://test/with/namespace", item.getNamespaceURI());
        }
    }
}
