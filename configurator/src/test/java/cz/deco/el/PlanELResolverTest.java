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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class PlanELResolverTest {

    @Mock
    private ELReplacer replacer;
    @Mock
    private Document document;

    boolean replaceInsertOrReplaceEntered;
    boolean replaceInObjectEntered;

    @Before
    public void init() {
        replaceInObjectEntered = false;
        replaceInsertOrReplaceEntered = false;
    }

    @Test
    public void testGetElReplacer() {
        ELReplacer elReplacer = new PlanELResolver().getELReplacer(new DeploymentPlan());
        Assert.assertNotNull(elReplacer);
    }

    @Test
    public void testResolveAllExpressionsNoneDefinitions() {
        PlanELResolver planELResolver = new PlanELResolver() {
            @Override
            protected ELReplacer getELReplacer(DeploymentPlan plan) {
                return replacer;
            }
        };
        planELResolver.resolveAllExpressions(new DeploymentPlan());
    }

    @Test
    public void testResolveAllExpressionsSomedefintions() {
        PlanELResolver planELResolver = new PlanELResolver() {
            @Override
            protected ELReplacer getELReplacer(DeploymentPlan plan) {
                return replacer;
            }

            @Override
            protected void replaceInsertOrReplace(ELReplacer replacer, List<Object> insertOrReplace) {
                replaceInsertOrReplaceEntered = true;
            }
        };
        DeploymentPlan plan = new DeploymentPlan();
        DescriptorOverride override = new DescriptorOverride();
        override.getModuleDescriptor().add(new ModuleDescriptor());
        plan.getDescriptorOverride().add(override);
        planELResolver.resolveAllExpressions(plan);
        Assert.assertTrue(replaceInsertOrReplaceEntered);
    }

    @Test
    public void testReplaceInsertOrReplace() {
        PlanELResolver planELResolver = new PlanELResolver() {
            @Override
            protected void replaceInObject(ELReplacer replacer, Object value) {
                replaceInObjectEntered = true;
            }
        };
        planELResolver.replaceInsertOrReplace(replacer, Collections.emptyList());
        Assert.assertFalse(replaceInObjectEntered);
        planELResolver.replaceInsertOrReplace(replacer, Collections.singletonList((Object) new Insert()));
        Assert.assertTrue(replaceInObjectEntered);
        replaceInObjectEntered = false;
        planELResolver.replaceInsertOrReplace(replacer, Collections.singletonList((Object) new Replace()));
        Assert.assertTrue(replaceInObjectEntered);
    }

    @Test
    public void testReplaceInObject() {
        PlanELResolver planELResolver = new PlanELResolver();
        planELResolver.replaceInObject(replacer, new Object());
        Mockito.when(document.getDocumentElement()).thenReturn(Mockito.mock(Element.class));
        planELResolver.replaceInObject(replacer, document);
        Mockito.verify(replacer, Mockito.times(1)).replaceText((NodeList) Mockito.any());
    }
}
