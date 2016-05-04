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

import cz.deco.core.DecoException;
import cz.deco.javaee.deployment_plan.DeploymentPlan;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class DeploymentPlanLoaderTest {

    @Test
    public void validatePlan() throws Exception {
        DeploymentPlanFile deploymentPlanFile = new DeploymentPlanFile(
                new File("src/test/resources/deployment-plan.xml").toURI());
        new DeploymentPlanLoader().validatePlan(deploymentPlanFile);
    }

    @Test(expected = DecoException.class)
    public void validatePlanInvalidNoXml() throws Exception {
        DeploymentPlanFile deploymentPlanFile = new DeploymentPlanFile(
                new File("src/test/resources/no-xml.xml").toURI());
        new DeploymentPlanLoader().validatePlan(deploymentPlanFile);
    }

    @Test(expected = DecoException.class)
    public void validatePlanInvalid() throws Exception {
        DeploymentPlanFile deploymentPlanFile = new DeploymentPlanFile(
                new File("src/test/resources/invalid-deployment-plan.xml").toURI());
        new DeploymentPlanLoader().validatePlan(deploymentPlanFile);
    }

    @Test
    public void load() throws Exception {
        DeploymentPlanFile deploymentPlanFile = new DeploymentPlanFile(
                new File("src/test/resources/deployment-plan.xml").toURI());
        DeploymentPlan load = new DeploymentPlanLoader().load(deploymentPlanFile);
        Assert.assertEquals("string", load.getApplicationName());
    }
}
