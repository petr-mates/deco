package cz.deco.el;

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
import cz.deco.javaee.deployment_plan.DescriptorOverride;
import cz.deco.javaee.deployment_plan.Insert;
import cz.deco.javaee.deployment_plan.ModuleDescriptor;
import cz.deco.javaee.deployment_plan.Replace;
import cz.deco.javaee.deployment_plan.VariableDefinitions;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.List;

public class PlanELResolver {

    public void resolveAllExpressions(DeploymentPlan plan) {
        ELReplacer replacer = getELReplacer(plan);
        List<DescriptorOverride> descriptors = plan.getDescriptorOverride();
        for (DescriptorOverride descriptor : descriptors) {
            for (ModuleDescriptor moduleDescriptor : descriptor.getModuleDescriptor()) {
                List<Object> insertOrReplace = moduleDescriptor.getInsertOrReplace();
                replaceInsertOrReplace(replacer, insertOrReplace);
            }
        }
    }

    protected ELReplacer getELReplacer(DeploymentPlan plan) {
        VariableDefinitions variableDefinitions = plan.getVariableDefinitions();
        if (variableDefinitions == null) {
            variableDefinitions = new VariableDefinitions();
        }
        return new ELReplacer(variableDefinitions.getVariable());
    }

    protected void replaceInsertOrReplace(ELReplacer replacer, List<Object> insertOrReplace) {
        for (Object object : insertOrReplace) {
            if (object instanceof Insert) {
                Object value = ((Insert) object).getValue();
                replaceInObject(replacer, value);
            }
            if (object instanceof Replace) {
                Object value = ((Replace) object).getValue();
                replaceInObject(replacer, value);
            }
        }
    }

    protected void replaceInObject(ELReplacer replacer, Object value) {
        if (value instanceof Element) {
            NodeList childNodes = ((Element) value).getChildNodes();
            replacer.replaceText(childNodes);
        }
    }
}
