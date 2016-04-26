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

import cz.deco.deployment.DeploymentPlanFile;
import cz.deco.deployment.DeploymentPlanLoader;
import cz.deco.javaee.deployment_plan.DeploymentPlan;
import cz.deco.javaee.deployment_plan.Insert;
import cz.deco.xml.XMLFactory;
import org.junit.Test;
import org.w3c.dom.Document;

import java.io.File;
import java.util.List;

public class ReplacerFullTest {

    private Replacer replacer;

    @Test
    public void init() throws Exception {
        replacer = new Replacer();
        Document doc = XMLFactory.newInstance().getBuilderNs()
                .parse("src/test/resources/test-web/WEB-INF/web.xml");
        replacer.setDocument(doc);
        DeploymentPlanLoader loder = new DeploymentPlanLoader();
        DeploymentPlanFile deploymentPlanFile = new DeploymentPlanFile(
                new File("src/test/resources/test-web/deployment-plan.xml").toURI());
        DeploymentPlan plan = loder.load(deploymentPlanFile);
        List<Object> insertOrReplace = plan.getModuleOverride().get(0).getModuleDescriptor().get(0).getInsertOrReplace();
        Insert insert = (Insert) insertOrReplace.get(0);
        replacer.apply(insert);
        //XMLTestSupport.printXml(doc);
    }
}
